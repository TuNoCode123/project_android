package com.example.formular_cookie.fragment;


import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.formular_cookie.model.AdminIngredient;
import com.example.formular_cookie.R;
import com.example.formular_cookie.model.AdminRecipe;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;

public class AdminRecipeDetailFragment extends Fragment {

    private TextView tvDescription, tvToolbarTitle;
    private ImageView ivImage, btnBack;
    private Button btnEdit, btnDelete;
    AdminRecipe adminRecipe;
    List<AdminRecipe> adminRecipeList = null;
    private boolean isEditMode;
    private boolean isApproveMode;
    private Bundle args;
    private LinearLayout llIngredients, llSteps;


    public AdminRecipeDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_detail_admin, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Lắng nghe kết quả cập nhật từ PostRecipeFragment
        getParentFragmentManager().setFragmentResultListener("recipe_update_result", this, (requestKey, result) -> {
            boolean isUpdated = result.getBoolean("isUpdated", false);
            if (isUpdated) {
                // Cập nhật lại dữ liệu từ Firestore
                fetchRecipeFromFirestore(adminRecipe.getId());
            }
        });
    }

    // Hàm load dữ liệu từ Firestore
    private void fetchRecipeFromFirestore(String recipeId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipes").document(recipeId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        AdminRecipe updatedAdminRecipe = documentSnapshot.toObject(AdminRecipe.class);
                        // Cập nhật lại UI với dữ liệu mới
                        updateUI(updatedAdminRecipe);
                    }
                });
    }

    private void updateUI(AdminRecipe updatedAdminRecipe) {
        // Cập nhật lại thông tin UI với dữ liệu mới
        tvToolbarTitle.setText(updatedAdminRecipe.getTitle());
        tvDescription.setText(updatedAdminRecipe.getDescription());
        // Cập nhật nguyên liệu
        llIngredients.removeAllViews();
        for (AdminIngredient adminIngredient : updatedAdminRecipe.getIngredients()) {
            TextView ingredientTextView = new TextView(getContext());
            ingredientTextView.setText(adminIngredient.getName() + ": " + adminIngredient.getAmount() + " " + adminIngredient.getUnit());
            ingredientTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            ingredientTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            llIngredients.addView(ingredientTextView);
        }

        // Cập nhật các bước
        llSteps.removeAllViews();
        for (String step : updatedAdminRecipe.getSteps()) {
            TextView stepTextView = new TextView(getContext());
            stepTextView.setText(step);
            stepTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            stepTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            llSteps.addView(stepTextView);
        }

        // Cập nhật hình ảnh
        String imageUrl = updatedAdminRecipe.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(ivImage);
        } else {
            ivImage.setImageResource(R.drawable.ic_launcher_foreground); // Hình ảnh mặc định
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các view
        tvToolbarTitle = view.findViewById(R.id.tv_detail_title);
        tvDescription = view.findViewById(R.id.tv_description);
        llIngredients = view.findViewById(R.id.ll_ingredients);
        llSteps = view.findViewById(R.id.ll_steps);
        ivImage = view.findViewById(R.id.iv_detail_image);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnBack = view.findViewById(R.id.btn_back);

        args = getArguments();
        if (args != null) {
            adminRecipe = (AdminRecipe) args.getSerializable("recipe");
            isEditMode = args.getBoolean("isEditMode", false);
            isApproveMode = args.getBoolean("isApproveMode", false);
            adminRecipeList = (List<AdminRecipe>) args.getSerializable("recipeList");
            if (isEditMode) {
                btnEdit.setText(getString(R.string.text_edit));
                btnDelete.setText(getString(R.string.text_delete));
            } else if(isApproveMode) {
                btnEdit.setText(getString(R.string.text_confirm));
                btnDelete.setText(getString(R.string.text_cancel));
            }
            tvToolbarTitle.setText(adminRecipe.getTitle());
            tvDescription.setText(adminRecipe.getDescription());
            //hien thi nguyen lieu tren tung dong
            for (AdminIngredient adminIngredient : adminRecipe.getIngredients()) {
                TextView ingredientTextView = new TextView(getContext());
                ingredientTextView.setText(adminIngredient.getName() + ": " + adminIngredient.getAmount() + " " + adminIngredient.getUnit());
                ingredientTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                ingredientTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                llIngredients.addView(ingredientTextView);
            }

            //hien thi cac tung tren tung dong
            for (String step : adminRecipe.getSteps()) {
                TextView stepTextView = new TextView(getContext());
                stepTextView.setText(step);
                stepTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                stepTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                llSteps.addView(stepTextView);
            }


            String imageUrl = adminRecipe.getImageUrl();
            Log.d("Recipe", "Image URL: " + imageUrl);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(ivImage);
            } else {
                Log.d("recipe", "URL hình ảnh không hợp lệ hoặc null");
                ivImage.setImageResource(R.drawable.ic_launcher_foreground); // Hình ảnh mặc định nếu URL null
            }

        }

        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        btnEdit.setOnClickListener(v -> {
            if (adminRecipe != null && isEditMode) {
                PostRecipeFragment editRecipe = new PostRecipeFragment();
                if (args != null) {
                    editRecipe.setArguments(args);
                }

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.admin_fragment_container, editRecipe)
                        .addToBackStack(null)
                        .commit();

            } else if (adminRecipe != null && isApproveMode) {
                adminRecipe.setStatus(true);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference recipeRef = db.collection("recipes").document(adminRecipe.getId());

                recipeRef.update("status", adminRecipe.getStatus())
                        .addOnSuccessListener(aVoid -> {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("RecipeDetail", "Lỗi cập nhật trạng thái", e);
                        });

            }
        });


        btnDelete.setOnClickListener(v -> {
            if (adminRecipe != null) {
                // Xóa công thức khỏi Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("recipes").document(adminRecipe.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d("RecipeDetail", "Đã xóa công thức khỏi Firestore");

                            // Quay về Fragment trước sau khi xóa thành công
                            requireActivity().getSupportFragmentManager().popBackStack();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("RecipeDetail", "Lỗi xóa công thức", e);
                        });
            } else {
                Log.w("RecipeDetail", "recipe bị null");
            }
        });

    }
}