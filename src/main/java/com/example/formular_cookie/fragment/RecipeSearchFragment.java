package com.example.formular_cookie.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private boolean isLoading = false;
    private List<Recipe> currentRecipes = new ArrayList<>();
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

        // Khởi t tạo repository
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
            // Show search prompt nếu không có trạng thái được lưu
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

        // Dùng GridLayoutManager để hiển thị các món ăn theo dạng lưới
        final GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        rvRecipes.setLayoutManager(layoutManager);
        rvRecipes.setAdapter(recipeAdapter);

        // Khôi phục dữ liệu nếu có
        if (!currentRecipes.isEmpty()) {
            recipeAdapter.updateData(currentRecipes);
            tvNoResults.setVisibility(View.GONE);
        }
    }

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
                // No chip selected
                // currentCategory = "";
            } else {
                // Filter by selected category
                Chip chip = getView().findViewById(checkedId);
                if (chip != null) {
                    // currentCategory = chip.getText().toString();
                    // For now, just use the chip text as a search term
                    etSearch.setText(chip.getText());
                }
            }
        });
    }

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
                        "Error searching recipes: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

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

    private void showLoading(boolean show) {
        isLoading = show;
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
    // Phương thức để khôi phục trạng thái tìm kiếm
    public void restoreSearchState() {
        if (!currentQuery.isEmpty()) {
            etSearch.setText(currentQuery);
            if (!currentRecipes.isEmpty()) {
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
