package com.example.formular_cookie;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.formular_cookie.adapter.RecipeAdapter;
import com.example.formular_cookie.api.ApiClient;
import com.example.formular_cookie.model.Recipe;
import com.example.formular_cookie.model.RecipeSearchResponse;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeSearchFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private static final String API_KEY = BuildConfig.SPOONACULAR_API_KEY;
    private static final int RESULTS_PER_PAGE = 20;

    private EditText etSearch;
    private ImageButton btnClear;
    private ChipGroup chipGroup;
    private RecyclerView rvRecipes;
    private ProgressBar progressBar;
    private TextView tvNoResults;

    private RecipeAdapter recipeAdapter;
    private Call<RecipeSearchResponse> currentCall;
    private OnRecipeSelectedListener callback;

    // Cho phân trang
    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String currentQuery = "";
    private String currentCategory = "";

    // Giao diện giao tiếp với activity chủ
    public interface OnRecipeSelectedListener {
        void onRecipeSelected(Recipe recipe);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Đảm bảo activity chủ triển khai giao diện callback
        try {
            callback = (OnRecipeSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " phải triển khai OnRecipeSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_search, container, false);

        initViews(view);
        setupRecyclerView();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.et_search);
        btnClear = view.findViewById(R.id.btn_clear);
        chipGroup = view.findViewById(R.id.chip_group);
        rvRecipes = view.findViewById(R.id.rv_recipes);
        progressBar = view.findViewById(R.id.progress_bar);
        tvNoResults = view.findViewById(R.id.tv_no_results);
    }

    private void setupRecyclerView() {
        recipeAdapter = new RecipeAdapter(requireContext());
        recipeAdapter.setOnRecipeClickListener(this);

        // Sử dụng GridLayoutManager với 2 cột
        final GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // Nếu là loading item, sẽ chiếm toàn bộ hàng
                return recipeAdapter.getItemViewType(position) == 1 ? 2 : 1;
            }
        });

        rvRecipes.setLayoutManager(layoutManager);
        rvRecipes.setAdapter(recipeAdapter);

        // Thêm listener cuộn để phân trang
        rvRecipes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= RESULTS_PER_PAGE) {
                        loadMoreRecipes();
                    }
                }
            }
        });
    }

    private void setupListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                            event.getAction() == KeyEvent.ACTION_DOWN)) {

                // Lấy văn bản tìm kiếm
                String searchText = etSearch.getText().toString().trim();
                if (!searchText.isEmpty() || !currentCategory.isEmpty()) {
                    resetSearch();
                    currentQuery = searchText;
                    searchRecipes(currentQuery, currentCategory, 0);
                } else {
                    // Xóa kết quả nếu tìm kiếm trống
                    recipeAdapter.clearData();
                    tvNoResults.setVisibility(View.VISIBLE);
                    tvNoResults.setText(R.string.search_prompt);
                }
                return true; // Tiêu thụ sự kiện
            }
            return false;
        });
        // Nút xóa tìm kiếm
        btnClear.setOnClickListener(v -> {
            etSearch.setText("");
            resetSearch();
        });

        // Chọn danh mục
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == View.NO_ID) {
                // Không có chip nào được chọn
                currentCategory = "";
            } else {
                // Lọc theo danh mục đã chọn
                Chip chip = getView().findViewById(checkedId);
                if (chip != null) {
                    currentCategory = chip.getText().toString();
                }
            }

            // Chỉ tìm kiếm khi có truy vấn hoặc danh mục
            if (!currentQuery.isEmpty() || !currentCategory.isEmpty()) {
                resetSearch();
                searchRecipes(currentQuery, currentCategory, 0);
            }
        });
    }

    private void resetSearch() {
        currentPage = 0;
        isLastPage = false;
        recipeAdapter.clearData();
    }

    private void searchRecipes(String query, String category, int offset) {
        // Không tìm kiếm nếu cả hai truy vấn và danh mục đều trống
        if (query.isEmpty() && category.isEmpty()) {
            return;
        }

        // Hủy bỏ tìm kiếm đang diễn ra
        if (currentCall != null) {
            currentCall.cancel();
        }

        // Hiển thị quá trình
        isLoading = true;
        if (offset == 0) {
            progressBar.setVisibility(View.VISIBLE);
            tvNoResults.setVisibility(View.GONE);
        } else {
            recipeAdapter.setLoadingMore(true);
        }

        // Kết hợp truy vấn và danh mục nếu cả hai đều có
        String finalQuery = query;
        if (!category.isEmpty()) {
            if (!query.isEmpty()) {
                finalQuery = query + " " + category;
            } else {
                finalQuery = category;
            }
        }

        // Gọi API
        currentCall = ApiClient.getSpoonacularApi().searchRecipes(
                API_KEY,
                finalQuery,
                RESULTS_PER_PAGE,
                true, // Thêm thông tin công thức
                true, // Thêm hướng dẫn công thức
                true, // Điền thông tin nguyên liệu
                offset // Cho phân trang
        );

        currentCall.enqueue(new Callback<RecipeSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecipeSearchResponse> call,
                    @NonNull Response<RecipeSearchResponse> response) {
                if (!isAdded())
                    return;

                isLoading = false;
                progressBar.setVisibility(View.GONE);
                recipeAdapter.setLoadingMore(false);

                if (response.isSuccessful() && response.body() != null) {
                    RecipeSearchResponse searchResponse = response.body();
                    List<Recipe> recipes = searchResponse.getRecipes();

                    if (recipes != null && !recipes.isEmpty()) {
                        currentPage++;
                        isLastPage = recipes.size() < RESULTS_PER_PAGE ||
                                (offset + recipes.size()) >= searchResponse.getTotalResults();

                        recipeAdapter.addData(recipes);
                        rvRecipes.setVisibility(View.VISIBLE);
                        tvNoResults.setVisibility(View.GONE);

                    } else {
                        // Không tìm thấy kết quả
                        if (offset == 0) {
                            tvNoResults.setVisibility(View.VISIBLE);
                            tvNoResults.setText(R.string.no_results);
                        } else {
                            isLastPage = true;
                        }
                    }
                } else {
                    // Xử lý lỗi
                    if (offset == 0) {
                        tvNoResults.setVisibility(View.VISIBLE);
                        tvNoResults.setText(R.string.search_error);
                    }

                    Toast.makeText(requireContext(),
                            "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecipeSearchResponse> call, Throwable t) {
                if (!isAdded())
                    return;

                if (!call.isCanceled()) {
                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
                    recipeAdapter.setLoadingMore(false);

                    if (offset == 0) {
                        tvNoResults.setVisibility(View.VISIBLE);
                        tvNoResults.setText(R.string.network_error);
                    }

                    Toast.makeText(requireContext(),
                            "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadMoreRecipes() {
        if (!isLoading && !isLastPage) {
            int nextOffset = currentPage * RESULTS_PER_PAGE;
            searchRecipes(currentQuery, currentCategory, nextOffset);
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe, int position) {
        // Thông báo cho host activity rằng một công thức đã được chọn
        callback.onRecipeSelected(recipe);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }
}
