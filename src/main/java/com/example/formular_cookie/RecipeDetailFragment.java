package com.example.formular_cookie;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.formular_cookie.R;
import com.example.formular_cookie.adapter.IngredientsAdapter;
import com.example.formular_cookie.adapter.InstructionsAdapter;
import com.example.formular_cookie.api.ApiClient;
import com.example.formular_cookie.model.Recipe;
import com.example.formular_cookie.model.RecipeDetailResponse;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailFragment extends Fragment {

    private static final String API_KEY = BuildConfig.SPOONACULAR_API_KEY;
    private static final String ARG_RECIPE = "recipe";

    private ImageView ivRecipeImage;
    private TextView tvSummary, tvReadyTime, tvServings, tvHealthScore;
    private RecyclerView rvIngredients, rvInstructions;
    private ProgressBar progressBar;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;

    private IngredientsAdapter ingredientsAdapter;
    private InstructionsAdapter instructionsAdapter;

    private Recipe recipe;
    private Call<RecipeDetailResponse> currentCall;

    public static RecipeDetailFragment newInstance(Recipe recipe) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            recipe = getArguments().getParcelable(ARG_RECIPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        initViews(view);
        setupToolbar();

        if (recipe != null) {
            displayBasicRecipeInfo();

            // Lấy toàn bộ thông tin công thức nếu chưa có
            if (recipe.getIngredients() == null || recipe.getIngredients().isEmpty() ||
                    recipe.getInstructions() == null || recipe.getInstructions().isEmpty()) {
                fetchRecipeDetails(recipe.getId());
            }
        } else {
            Toast.makeText(requireContext(), "Recipe not found", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hủy gọi API nếu có
        if (currentCall != null) {
            currentCall.cancel();
        }
    }

    private void initViews(View view) {
        collapsingToolbar = view.findViewById(R.id.collapsing_toolbar);
        toolbar = view.findViewById(R.id.toolbar);
        ivRecipeImage = view.findViewById(R.id.iv_recipe_image);
        tvSummary = view.findViewById(R.id.tv_summary);
        tvReadyTime = view.findViewById(R.id.tv_ready_time);
        tvServings = view.findViewById(R.id.tv_servings);
        tvHealthScore = view.findViewById(R.id.tv_health_score);
        rvIngredients = view.findViewById(R.id.rv_ingredients);
        rvInstructions = view.findViewById(R.id.rv_instructions);
        progressBar = view.findViewById(R.id.progress_bar);

        rvIngredients.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvInstructions.setLayoutManager(new LinearLayoutManager(requireContext()));

        ingredientsAdapter = new IngredientsAdapter(requireContext());
        instructionsAdapter = new InstructionsAdapter(requireContext());

        rvIngredients.setAdapter(ingredientsAdapter);
        rvInstructions.setAdapter(instructionsAdapter);
    }

    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void displayBasicRecipeInfo() {
        collapsingToolbar.setTitle(recipe.getTitle());

        // Tải hình ảnh công thức
        Glide.with(this)
                .load(recipe.getFullImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(ivRecipeImage);

        // Đặt thời gian nấu, số phần ăn và điểm sức khỏe
        if (recipe.getReadyInMinutes() > 0) {
            tvReadyTime.setText("Ready in " + recipe.getReadyInMinutes() + " minutes");
        }

        if (recipe.getServings() > 0) {
            tvServings.setText(recipe.getServings() + " servings");
        }

        if (recipe.getHealthScore() > 0) {
            tvHealthScore.setText("Health score: " + recipe.getHealthScore());
        }

        // Đặt tóm tắt công thức nếu có
        if (recipe.getSummary() != null && !recipe.getSummary().isEmpty()) {
            tvSummary.setText(Html.fromHtml(recipe.getSummary()));
        }

        // Đặt hình ảnh tác giả nếu có
        if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
            ingredientsAdapter.updateData(recipe.getIngredients());
        }

        // Đặt hướng dẫn nếu có
        if (recipe.getInstructions() != null && !recipe.getInstructions().isEmpty()) {
            instructionsAdapter.updateData(recipe.getInstructions());
        }
    }

    private void fetchRecipeDetails(int recipeId) {
        progressBar.setVisibility(View.VISIBLE);

        currentCall = ApiClient.getSpoonacularApi().getRecipeDetails(recipeId, API_KEY, true);
        currentCall.enqueue(new Callback<RecipeDetailResponse>() {
            @Override
            public void onResponse(Call<RecipeDetailResponse> call, Response<RecipeDetailResponse> response) {
                if (!isAdded())
                    return; // Fragment not attached to activity

                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    recipe = response.body();
                    displayBasicRecipeInfo();
                } else {
                    Toast.makeText(requireContext(),
                            "Failed to load recipe details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecipeDetailResponse> call, Throwable t) {
                if (!isAdded())
                    return; // Fragment not attached to activity

                if (!call.isCanceled()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(),
                            "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
