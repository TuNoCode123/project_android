package com.example.formular_cookie.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.formular_cookie.R;
import com.example.formular_cookie.adapter.RecipeAdapter;
import com.example.formular_cookie.model.Recipe;
import com.example.formular_cookie.repository.FirebaseRecipeRepository;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

// Fragment để tìm kiếm công thức nấu ăn.
public class RecipeSearchFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private EditText etSearch;
    private ImageButton btnClear;
    private ChipGroup chipGroup;
    private RecyclerView rvRecipes;
    private ProgressBar progressBar;
    private TextView tvNoResults;

    private RecipeAdapter recipeAdapter;
    private OnRecipeSelectedListener callback;
    private FirebaseRecipeRepository recipeRepository;

    private String currentQuery = "";
    private final List<Recipe> currentRecipes = new ArrayList<>();
    private int selectedChipId = View.NO_ID;

    public interface OnRecipeSelectedListener {
        void onRecipeSelected(Recipe recipe);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Phải đảm bảo rằng host activity đã implement interface
        try {
            callback = (OnRecipeSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnRecipeSelectedListener");
        }

        // Khởi tạo repository
        recipeRepository = FirebaseRecipeRepository.getInstance(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_search, container, false);

        initViews(view);
        setupRecyclerView();
        setupListeners();
        // Khôi phục trạng thái nếu có
        if (savedInstanceState != null) {
            currentQuery = savedInstanceState.getString("currentQuery", "");
            selectedChipId = savedInstanceState.getInt("selectedChipId", View.NO_ID);

            // Khôi phục trạng thái tìm kiếm
            if (!currentQuery.isEmpty()) {
                etSearch.setText(currentQuery);
                searchRecipes();
            } else if (selectedChipId != View.NO_ID) {
                chipGroup.check(selectedChipId);
            }
        } else {
            // Hiển thị gợi ý tìm kiếm nếu không có trạng thái được lưu
            tvNoResults.setVisibility(View.VISIBLE);
            tvNoResults.setText(R.string.search_prompt);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Lưu trạng thái tìm kiếm hiện tại
        outState.putString("currentQuery", currentQuery);
        outState.putInt("selectedChipId", selectedChipId);
    }

    // Khởi tạo các view trong fragment.
    private void initViews(View view) {
        etSearch = view.findViewById(R.id.et_search);
        btnClear = view.findViewById(R.id.btn_clear);
        if(getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {

        }
        chipGroup = view.findViewById(R.id.chip_group);
        rvRecipes = view.findViewById(R.id.rv_recipes);
        progressBar = view.findViewById(R.id.progress_bar);
        tvNoResults = view.findViewById(R.id.tv_no_results);
    }

    // Thiết lập RecyclerView để hiển thị danh sách công thức.
    private void setupRecyclerView() {
        recipeAdapter = new RecipeAdapter(requireContext());
        recipeAdapter.setOnRecipeClickListener(this);

        var spanCount = 2;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 4; // Hiển thị 3 cột trong chế độ ngang
        }

        // Dùng GridLayoutManager để hiển thị các món ăn theo dạng lưới
        final GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), spanCount);
        rvRecipes.setLayoutManager(layoutManager);
        rvRecipes.setAdapter(recipeAdapter);

        // Khôi phục dữ liệu nếu có
        if (!currentRecipes.isEmpty()) {
            recipeAdapter.updateData(currentRecipes);
            tvNoResults.setVisibility(View.GONE);
        }
    }

    // Thiết lập các listener cho các sự kiện trong giao diện.
    private void setupListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            private long lastTextEdit = 0;
            private static final long DEBOUNCE_DELAY = 500; // milliseconds

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastTextEdit = System.currentTimeMillis();
                btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);

                // Thực hiện tìm kiếm với độ trễ
                final String searchText = s.toString();
                etSearch.postDelayed(() -> {
                    if (System.currentTimeMillis() - lastTextEdit >= DEBOUNCE_DELAY) {
                        currentQuery = searchText;
                        if (searchText.length() > 0) {
                            searchRecipes();
                        } else {
                            // Xóa kết quả nếu không có gì được nhập
                            recipeAdapter.clearData();
                            currentRecipes.clear();
                            tvNoResults.setVisibility(View.VISIBLE);
                            tvNoResults.setText(R.string.search_prompt);
                        }
                    }
                }, DEBOUNCE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        btnClear.setOnClickListener(v -> {
            etSearch.setText("");
            currentQuery = "";
            currentRecipes.clear();
            recipeAdapter.clearData();
            tvNoResults.setVisibility(View.VISIBLE);
            tvNoResults.setText(R.string.search_prompt);
        });

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedChipId = checkedId;

            if (checkedId == View.NO_ID) {
                // Không có chip nào được chọn
            } else {
                // Lọc theo danh mục được chọn
                Chip chip = getView().findViewById(checkedId);
                if (chip != null) {
                    // Tạm thời sử dụng văn bản của chip làm từ khóa tìm kiếm
                    etSearch.setText(chip.getText());
                }
            }
        });
    }

    // Thực hiện tìm kiếm công thức dựa trên từ khóa.
    private void searchRecipes() {
        // Không thực hiện tìm kiếm nếu query trống
        if (currentQuery.isEmpty()) {
            recipeAdapter.clearData();
            currentRecipes.clear();
            tvNoResults.setVisibility(View.VISIBLE);
            tvNoResults.setText(R.string.search_prompt);
            return;
        }

        showLoading(true);

        recipeRepository.searchRecipes(currentQuery, new FirebaseRecipeRepository.OnRecipesLoadedListener() {
            @Override
            public void onRecipesLoaded(List<Recipe> recipes) {
                if (!isAdded())
                    return;

                showLoading(false);
                updateRecipeList(recipes);
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded())
                    return;

                showLoading(false);
                Toast.makeText(requireContext(),
                        getString(R.string.search_error) + ": " + errorMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cập nhật danh sách công thức hiển thị.
    private void updateRecipeList(List<Recipe> recipes) {
        currentRecipes.clear();
        currentRecipes.addAll(recipes);
        recipeAdapter.updateData(recipes);

        if (recipes.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
            tvNoResults.setText(R.string.no_results);
        } else {
            tvNoResults.setVisibility(View.GONE);
        }
    }

    // Hiển thị hoặc ẩn ProgressBar khi tải dữ liệu.
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            tvNoResults.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe, int position) {
        callback.onRecipeSelected(recipe);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    // Khôi phục trạng thái tìm kiếm.
    public void restoreSearchState() {
        Log.d("RecipeSearchFragment", "Restoring search state");
        if (!currentQuery.isEmpty()) {
            Log.d("RecipeSearchFragment", "Restoring search state with query: " + currentQuery);
            etSearch.setText(currentQuery);
            if (!currentRecipes.isEmpty()) {
                Log.d("RecipeSearchFragment", "Restoring search state with recipes");
                recipeAdapter.updateData(currentRecipes);
                tvNoResults.setVisibility(View.GONE);
            } else {
                searchRecipes();
            }
        } else if (selectedChipId != View.NO_ID) {
            chipGroup.check(selectedChipId);
        }
    }
}
