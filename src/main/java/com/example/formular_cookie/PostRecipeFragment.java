package com.example.formular_cookie;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.widget.Toast;

import android.util.Log;

public class PostRecipeFragment extends Fragment {
    private static final String TAG = "PostRecipeFragment";
    private EditText etTitle, etIngredients, etSteps;
    private ImageView ivPreview;
    private Uri selectedImageUri;
    private Bundle args;
    private Recipe recipe;
    private Boolean isEditMode;
    private Button btnSelectImage;
    private Button btnSubmit;

    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_recipe, container, false);

        etTitle = view.findViewById(R.id.et_recipe_title);
        etIngredients = view.findViewById(R.id.et_recipe_ingredients);
        etSteps = view.findViewById(R.id.et_recipe_steps);
        ivPreview = view.findViewById(R.id.img_recipe);
        btnSelectImage = view.findViewById(R.id.btn_pick_image);
        btnSubmit = view.findViewById(R.id.btn_submit_recipe);

        // Đăng ký permission launcher
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(getContext(), "Bạn cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Đăng ký image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        ivPreview.setImageURI(selectedImageUri);
                    }
                }
        );

        args = getArguments();
        showRecipeInfoIfEdit();

        btnSelectImage.setOnClickListener(v -> checkAndRequestPermission());
        btnSubmit.setOnClickListener(v -> submitRecipe());

        return view;
    }

    private void showRecipeInfoIfEdit() {
        if (args != null) {
            recipe = (Recipe) args.getSerializable("recipe");
            isEditMode = args.getBoolean("isEditMode", false);
            Log.d(TAG, "editMode: " + isEditMode);
            if (isEditMode) {
                etTitle.setText(recipe.getTitle());
                etIngredients.setText(recipe.getIngredients());
                etSteps.setText(recipe.getSteps());
                btnSubmit.setText("Lưu");

                if (recipe.getSelectedImageUri() != null) {
                    selectedImageUri = recipe.getSelectedImageUri();
                    ivPreview.setImageURI(selectedImageUri);
                }
            }
        } else {
            Log.w(TAG, "Arguments bị null khi chuyển sang PostRecipeFragment");
        }
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
        String ingredients = etIngredients.getText().toString().trim();
        String steps = etSteps.getText().toString().trim();

        if (title.isEmpty() || ingredients.isEmpty() || steps.isEmpty() || selectedImageUri == null) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isEditMode){
            recipe.setTitle(title);
            recipe.setIngredients(ingredients);
            recipe.setSteps(steps);
            recipe.setSelectedImageUri(selectedImageUri);
            Log.d(TAG, "Chỉnh Sửa Công Thức");
            Log.d(TAG, "Tên: " + title);
            Log.d(TAG, "Nguyên liệu: " + ingredients);
            Log.d(TAG, "Cách làm: " + steps);
            Log.d(TAG, "Ảnh URI: " + selectedImageUri.toString());
            //TODO: Lưu công thức đã chỉnh sửa vào database
            Toast.makeText(getContext(), "Đã chỉnh sửa thức thành công!", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        }else{
            Log.d(TAG, "Đăng công thức:");
            Log.d(TAG, "Tên: " + title);
            Log.d(TAG, "Nguyên liệu: " + ingredients);
            Log.d(TAG, "Cách làm: " + steps);
            Log.d(TAG, "Ảnh URI: " + selectedImageUri.toString());
            //TODO: Thêm công thức mới vào database
            Toast.makeText(getContext(), "Đã đăng công thức thành công!", Toast.LENGTH_SHORT).show();
        }
        etTitle.setText("");
        etIngredients.setText("");
        etSteps.setText("");
        ivPreview.setImageResource(R.drawable.ic_launcher_foreground);
        selectedImageUri = null;
    }
}
