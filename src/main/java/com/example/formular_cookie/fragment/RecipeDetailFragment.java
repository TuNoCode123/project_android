package com.example.formular_cookie.fragment;

import android.os.Bundle;
import android.util.Log;
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
import com.example.formular_cookie.model.Recipe;
import com.example.formular_cookie.repository.FirebaseRecipeRepository;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

// Fragment để hiển thị chi tiết công thức nấu ăn.
public class RecipeDetailFragment extends Fragment {

    private static final String ARG_RECIPE = "recipe";

    private ImageView ivRecipeImage, ivLikeButton; // Add ivLikeButton
    private TextView tvSummary, tvAuthorName;
    private ProgressBar progressBar;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;

    private IngredientsAdapter ingredientsAdapter;
    private InstructionsAdapter instructionsAdapter;

    private Recipe recipe;
    private FirebaseRecipeRepository recipeRepository;
    private boolean isFullyLoaded = false;
    private boolean isLiked = false; // Track like state
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

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

        db = FirebaseFirestore.getInstance(); // Initialize Firestore
        mAuth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth
        currentUser = mAuth.getCurrentUser(); // Get current user

        if (getArguments() != null) {
            recipe = getArguments().getParcelable(ARG_RECIPE);
        } else if (savedInstanceState != null) {
            // Khôi phục từ savedInstanceState nếu có
            recipe = savedInstanceState.getParcelable(ARG_RECIPE);
            isFullyLoaded = savedInstanceState.getBoolean("isFullyLoaded", false);
            isLiked = savedInstanceState.getBoolean("isLiked", false); // Restore like state
        }

        // Khởi tạo FirebaseRecipeRepository
        recipeRepository = FirebaseRecipeRepository.getInstance(requireContext());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Lưu trạng thái
        if (recipe != null) {
            outState.putParcelable(ARG_RECIPE, recipe);
        }
        outState.putBoolean("isFullyLoaded", isFullyLoaded);
        outState.putBoolean("isLiked", isLiked); // Save like state
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        initViews(view);
        setupToolbar();
        setupLikeButton(); // Setup the like button listener

        if (recipe != null) {
            displayBasicRecipeInfo();
            checkInitialLikeState(); // Check if the user already liked this recipe

            // Lấy thông tin chi tiết công thức nếu chưa có
            if (!isFullyLoaded && (recipe.getIngredients() == null || recipe.getIngredients().isEmpty() ||
                    recipe.getSteps() == null || recipe.getSteps().isEmpty())) {
                fetchRecipeDetails(recipe.getId());
            }
        } else {
            Toast.makeText(requireContext(), "Recipe not found", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }

        return view;
    }

    // Khởi tạo các view trong fragment.
    private void initViews(View view) {
        collapsingToolbar = view.findViewById(R.id.collapsing_toolbar);
        toolbar = view.findViewById(R.id.toolbar);
        ivRecipeImage = view.findViewById(R.id.iv_recipe_image);
        tvAuthorName = view.findViewById(R.id.tv_author_name);
        tvSummary = view.findViewById(R.id.tv_summary);
        progressBar = view.findViewById(R.id.progress_bar);
        ivLikeButton = view.findViewById(R.id.iv_like_button); // Initialize like button
        RecyclerView rvIngredients = view.findViewById(R.id.rv_ingredients);
        RecyclerView rvInstructions = view.findViewById(R.id.rv_instructions);

        rvIngredients.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvInstructions.setLayoutManager(new LinearLayoutManager(requireContext()));

        ingredientsAdapter = new IngredientsAdapter(requireContext());
        instructionsAdapter = new InstructionsAdapter(requireContext());

        rvIngredients.setAdapter(ingredientsAdapter);
        rvInstructions.setAdapter(instructionsAdapter);
    }

    // Thiết lập Toolbar cho fragment.
    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    // Hiển thị thông tin cơ bản của công thức.
    private void displayBasicRecipeInfo() {
        collapsingToolbar.setTitle(recipe.getTitle());

        // Tải hình ảnh công thức
        Glide.with(this)
                .load(recipe.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(ivRecipeImage);

        // Set thông tin cơ bản
        // Set tên tác giả nếu có
        if (recipe.getAuthorID() != null && !recipe.getAuthorID().isEmpty()) {
            tvAuthorName.setText(recipe.getAuthorName());
        }
        // Set tóm tắt công thức nếu có
        if (recipe.getSummary() != null && !recipe.getSummary().isEmpty()) {
            tvSummary.setText(recipe.getSummary());
        }

        // Set nguyên liệu nếu có
        if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
            ingredientsAdapter.updateData(recipe.getIngredients());
        }

        // Set hướng dẫn nếu có
        if (recipe.getSteps() != null && !recipe.getSteps().isEmpty()) {
            instructionsAdapter.updateData(recipe.getSteps());
        }
        updateLikeButtonUI(); // Update like button based on isLiked state
    }

    // Lấy thông tin chi tiết của công thức từ Firestore.
    private void fetchRecipeDetails(String recipeId) {
        progressBar.setVisibility(View.VISIBLE);

        recipeRepository.getRecipeById(recipeId, new FirebaseRecipeRepository.OnRecipeLoadedListener() {
            @Override
            public void onRecipeLoaded(Recipe loadedRecipe) {
                if (!isAdded())
                    return;

                progressBar.setVisibility(View.GONE);
                recipe = loadedRecipe;
                isFullyLoaded = true;
                displayBasicRecipeInfo(); // Refresh UI including like button state if needed
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded())
                    return;

                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(),
                        "Error loading recipe details: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Setup listener for the like button
    private void setupLikeButton() {
        ivLikeButton.setOnClickListener(v -> {
            if (currentUser == null) {
                Toast.makeText(getContext(), "Bạn cần đăng nhập để thích công thức", Toast.LENGTH_SHORT).show();
                return;
            }
            if (recipe == null || recipe.getId() == null) {
                Toast.makeText(getContext(), "Lỗi: Không tìm thấy công thức", Toast.LENGTH_SHORT).show();
                return;
            }
            toggleLikeStatus();
        });
    }

    // Check the initial like state from Firestore
    private void checkInitialLikeState() {
        if (currentUser != null && recipe != null && recipe.getId() != null) {
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    List<String> likedRecipes = (List<String>) documentSnapshot.get("likedRecipies");
                    if (likedRecipes != null && likedRecipes.contains(recipe.getId())) {
                        isLiked = true;
                    } else {
                        isLiked = false;
                    }
                } else {
                    isLiked = false; // User document doesn't exist yet
                }
                updateLikeButtonUI();
            }).addOnFailureListener(e -> {
                Log.e("RecipeDetail", "Error checking like state", e);
                isLiked = false; // Assume not liked on error
                updateLikeButtonUI();
            });
        } else {
            isLiked = false; // Not logged in or recipe invalid
            updateLikeButtonUI();
        }
    }

    // Toggle like status in Firestore
    private void toggleLikeStatus() {
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());
        String recipeId = recipe.getId();

        if (isLiked) {
            // Unlike the recipe
            userRef.update("likedRecipies", FieldValue.arrayRemove(recipeId))
                    .addOnSuccessListener(aVoid -> {
                        isLiked = false;
                        updateLikeButtonUI();
                        Toast.makeText(getContext(), "Đã bỏ thích", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("RecipeDetail", "Error unliking recipe", e);
                        Toast.makeText(getContext(), "Lỗi khi bỏ thích", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Like the recipe
            userRef.update("likedRecipies", FieldValue.arrayUnion(recipeId))
                    .addOnSuccessListener(aVoid -> {
                        isLiked = true;
                        updateLikeButtonUI();
                        Toast.makeText(getContext(), "Đã thích", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Check if the field 'likedRecipies' doesn't exist, then create it
                        if (e.getMessage() != null && e.getMessage().contains("No document to update")) {
                            List<String> likedList = new ArrayList<>();
                            likedList.add(recipeId);
                            userRef.set(Map.of("likedRecipies", likedList)) // Use set to create if not exists
                                    .addOnSuccessListener(aVoid2 -> {
                                        isLiked = true;
                                        updateLikeButtonUI();
                                        Toast.makeText(getContext(), "Đã thích", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e2 -> {
                                        Log.e("RecipeDetail", "Error liking recipe (set)", e2);
                                        Toast.makeText(getContext(), "Lỗi khi thích công thức", Toast.LENGTH_SHORT)
                                                .show();
                                    });
                        } else {
                            Log.e("RecipeDetail", "Error liking recipe (update)", e);
                            Toast.makeText(getContext(), "Lỗi khi thích công thức", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Update the like button icon based on the isLiked state
    private void updateLikeButtonUI() {
        if (isLiked) {
            ivLikeButton.setImageResource(R.drawable.ic_favorite); // Filled heart icon
        } else {
            ivLikeButton.setImageResource(R.drawable.ic_favorite_border); // Border heart icon
        }
    }
}