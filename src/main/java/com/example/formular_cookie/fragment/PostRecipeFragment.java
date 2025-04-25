package com.example.formular_cookie.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.example.formular_cookie.model.AdminIngredient;
import com.example.formular_cookie.utils.CloudinaryUploader;
import com.example.formular_cookie.R;
import com.example.formular_cookie.model.AdminRecipe;
import com.example.formular_cookie.utils.UploadCallback;
import com.google.firebase.auth.FirebaseAuth; // Import FirebaseAuth
import com.google.firebase.auth.FirebaseUser; // Import FirebaseUser
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostRecipeFragment extends Fragment {
    private LinearLayout ingredientsContainer, stepsContainer;
    private Button btnAddIngredient, btnAddStep;
    private EditText etTitle, etDescription;
    private ImageView ivPreview;
    private Uri selectedImageUri = null;
    private String imageUrl;
    private Button btnSelectImage, btnSubmit;
    private static final String TAG = "PostRecipeFragment";
    private Bundle args;
    private AdminRecipe adminRecipe;
    private Boolean isEditMode = false;
    private String authorID; // Remove default initialization

    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_recipe, container, false);

        etTitle = view.findViewById(R.id.et_recipe_title);
        etDescription = view.findViewById(R.id.et_description);
        ivPreview = view.findViewById(R.id.img_recipe);
        btnSelectImage = view.findViewById(R.id.btn_pick_image);
        btnSubmit = view.findViewById(R.id.btn_submit_recipe);

        ingredientsContainer = view.findViewById(R.id.ingredients_container);
        btnAddIngredient = view.findViewById(R.id.btn_add_ingredient);
        stepsContainer = view.findViewById(R.id.steps_container);
        btnAddStep = view.findViewById(R.id.btn_add_step);

        // Get current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            authorID = currentUser.getUid();
        } else {
            // Handle the case where the user is not logged in (optional, depends on app
            // flow)
            Log.e(TAG, "User not logged in!");
            Toast.makeText(getContext(), "Lỗi: Người dùng chưa đăng nhập.", Toast.LENGTH_SHORT).show();
            // Optionally, navigate back or disable functionality
        }

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(getContext(), "Bạn cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
                    }
                });

        // Đăng ký image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData(); // Ảnh mới chọn
                        ivPreview.setImageURI(selectedImageUri); // Hiển thị ảnh mới chọn

                        // 🧵 UPLOAD ẢNH SAU KHI CHỌN
                        new Thread(() -> {
                            new CloudinaryUploader().uploadImage(requireContext(), selectedImageUri,
                                    new UploadCallback() {
                                        @Override
                                        public void onUploadSuccess(String uploadedUrl) {
                                            imageUrl = uploadedUrl;
                                            requireActivity().runOnUiThread(() -> Toast
                                                    .makeText(getContext(), "Tải ảnh thành công", Toast.LENGTH_SHORT)
                                                    .show());
                                        }

                                        @Override
                                        public void onUploadFailure(String errorMessage) {
                                            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                                                    "Lỗi upload ảnh: " + errorMessage, Toast.LENGTH_SHORT).show());
                                        }
                                    });
                        }).start();
                    }
                });

        args = getArguments();
        if (args != null) {
            showRecipeInfoIfEdit();
        } else {
            // Add default one row
            addIngredientRow(null, null, null);
            addStepRow(null);
        }

        btnAddIngredient.setOnClickListener(v -> addIngredientRow(null, null, null));
        btnAddStep.setOnClickListener(v -> addStepRow(null));

        btnSelectImage.setOnClickListener(v -> checkAndRequestPermission());
        btnSubmit.setOnClickListener(v -> submitRecipe());

        return view;
    }

    private void showRecipeInfoIfEdit() {
        adminRecipe = (AdminRecipe) args.getSerializable("recipe");
        isEditMode = args.getBoolean("isEditMode", false);
        Log.d(TAG, "editMode: " + isEditMode);
        if (isEditMode) {
            etTitle.setText(adminRecipe.getTitle());
            etDescription.setText(adminRecipe.getDescription());
            // authorID is already set from the current user, no need to overwrite unless
            // you specifically want to keep the original author in edit mode.
            // If you want to keep the original author:
            // authorID = adminRecipe.getAuthorID();
            for (AdminIngredient adminIngredient : adminRecipe.getIngredients()) {
                addIngredientRow(adminIngredient.getName(), adminIngredient.getAmount(), adminIngredient.getUnit());
            }
            for (String step : adminRecipe.getSteps()) {
                addStepRow(step);
            }
            imageUrl = adminRecipe.getImageUrl(); // Giữ lại ảnh cũ

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(ivPreview);
            }

            btnSubmit.setText("Lưu");
        }
    }

    private void addIngredientRow(String name, String amount, String unit) {
        View row = LayoutInflater.from(getContext()).inflate(R.layout.ingredient_row, ingredientsContainer, false);

        EditText etName = row.findViewById(R.id.et_ingredient_name);
        EditText etAmount = row.findViewById(R.id.et_ingredient_amount);
        EditText etUnit = row.findViewById(R.id.et_ingredient_unit);
        Button btnRemove = row.findViewById(R.id.btn_remove_ingredient);

        if (name != null)
            etName.setText(name);
        if (amount != null)
            etAmount.setText(amount);
        if (unit != null)
            etUnit.setText(unit);

        btnRemove.setOnClickListener(v -> ingredientsContainer.removeView(row));

        ingredientsContainer.addView(row);
    }

    private void addStepRow(String stepText) {
        View row = LayoutInflater.from(getContext()).inflate(R.layout.step_row, stepsContainer, false);
        EditText etStep = row.findViewById(R.id.et_step);
        Button btnRemove = row.findViewById(R.id.btn_remove_step);
        if (stepText != null)
            etStep.setText(stepText);
        btnRemove.setOnClickListener(v -> stepsContainer.removeView(row));
        stepsContainer.addView(row);
    }

    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void submitRecipe() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || (!isEditMode && selectedImageUri == null)) {
            Toast.makeText(getContext(), "Vui lòng nhập tiêu đề và chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        List<AdminIngredient> ingredientsList = new ArrayList<>();
        for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
            View row = ingredientsContainer.getChildAt(i);
            EditText etName = row.findViewById(R.id.et_ingredient_name);
            EditText etAmount = row.findViewById(R.id.et_ingredient_amount);
            EditText etUnit = row.findViewById(R.id.et_ingredient_unit);

            String name = etName.getText().toString().trim();
            String amount = etAmount.getText().toString().trim();
            String unit = etUnit.getText().toString().trim();

            if (!name.isEmpty() && !amount.isEmpty()) {
                ingredientsList.add(new AdminIngredient(name, amount, unit));
            }
        }

        List<String> stepsList = new ArrayList<>();
        for (int i = 0; i < stepsContainer.getChildCount(); i++) {
            View row = stepsContainer.getChildAt(i);
            EditText etStep = row.findViewById(R.id.et_step);
            String step = etStep.getText().toString().trim();
            if (!step.isEmpty()) {
                stepsList.add(step);
            }
        }
        if (imageUrl != null) {
            saveRecipeToFirebase(title, ingredientsList, stepsList, imageUrl, description, authorID);
        }
    }

    private void saveRecipeToFirebase(String title, List<AdminIngredient> adminIngredients, List<String> steps,
                                      String imageUrl, String description, String authorID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference recipeRef;

        // Ensure authorID is not null before saving
        if (authorID == null) {
            Log.e(TAG, "Author ID is null, cannot save recipe.");
            Toast.makeText(getContext(), "Lỗi: Không thể lưu công thức do thiếu ID người dùng.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (isEditMode && adminRecipe != null && adminRecipe.getId() != null) {
            recipeRef = db.collection("recipes").document(adminRecipe.getId());
        } else {
            recipeRef = db.collection("recipes").document();
        }

        Map<String, Object> recipeData = new HashMap<>();
        recipeData.put("title", title);
        recipeData.put("imageUrl", imageUrl);
        recipeData.put("status", true);
        recipeData.put("steps", steps);
        recipeData.put("description", description);
        recipeData.put("authorID", authorID);

        List<Map<String, Object>> ingredientMaps = new ArrayList<>();
        for (AdminIngredient adminIngredient : adminIngredients) {
            Map<String, Object> ingMap = new HashMap<>();
            ingMap.put("name", adminIngredient.getName());
            ingMap.put("amount", adminIngredient.getAmount());
            ingMap.put("unit", adminIngredient.getUnit());
            ingredientMaps.add(ingMap);
        }
        recipeData.put("ingredients", ingredientMaps);

        recipeRef.set(recipeData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Lưu công thức thành công!", Toast.LENGTH_SHORT).show();
                    if (!isEditMode)
                        clearFields();
                    else {
                        requireActivity().getSupportFragmentManager().popBackStack();
                        // Gửi kết quả về để load lại dữ liệu trong RecipeDetailFragment
                        Bundle result = new Bundle();
                        result.putBoolean("isUpdated", true); // Chỉ báo rằng công thức đã được cập nhật
                        getParentFragmentManager().setFragmentResult("recipe_update_result", result);
                    }

                })
                .addOnFailureListener(
                        e -> Toast.makeText(getContext(), "Lỗi lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        etTitle.setText("");
        ivPreview.setImageResource(R.drawable.ic_launcher_foreground);
        ingredientsContainer.removeAllViews();
        stepsContainer.removeAllViews();
        addIngredientRow(null, null, null);
        addStepRow(null);
        imageUrl = null;
    }
}