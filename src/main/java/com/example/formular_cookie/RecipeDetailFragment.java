package com.example.formular_cookie;


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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;

public class RecipeDetailFragment extends Fragment {

    private TextView tvDescription, tvToolbarTitle;
    private ImageView ivImage, btnBack;
    private Button btnEdit, btnDelete;
    Recipe recipe;
    List<Recipe> recipeList = null;
    private boolean isEditMode;
    private boolean isApproveMode;
    private Bundle args;
    private LinearLayout llIngredients, llSteps;


    public RecipeDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Lắng nghe kết quả cập nhật từ PostRecipeFragment
        getParentFragmentManager().setFragmentResultListener("recipe_update_result", this, (requestKey, result) -> {
            boolean isUpdated = result.getBoolean("isUpdated", false);
            if (isUpdated) {
                // Cập nhật lại dữ liệu từ Firestore
                fetchRecipeFromFirestore(recipe.getId());
            }
        });
    }

    // Hàm load dữ liệu từ Firestore
    private void fetchRecipeFromFirestore(String recipeId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipes").document(recipeId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Recipe updatedRecipe = documentSnapshot.toObject(Recipe.class);
                        // Cập nhật lại UI với dữ liệu mới
                        updateUI(updatedRecipe);
                    }
                });
    }

    private void updateUI(Recipe updatedRecipe) {
        // Cập nhật lại thông tin UI với dữ liệu mới
        tvToolbarTitle.setText(updatedRecipe.getTitle());
        tvDescription.setText(updatedRecipe.getDescription());
        // Cập nhật nguyên liệu
        llIngredients.removeAllViews();
        for (Ingredient ingredient : updatedRecipe.getIngredients()) {
            TextView ingredientTextView = new TextView(getContext());
            ingredientTextView.setText(ingredient.getName() + ": " + ingredient.getAmount() + " " + ingredient.getUnit());
            ingredientTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            ingredientTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            llIngredients.addView(ingredientTextView);
        }

        // Cập nhật các bước
        llSteps.removeAllViews();
        for (String step : updatedRecipe.getSteps()) {
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
        String imageUrl = updatedRecipe.getImageUrl();
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
            recipe = (Recipe) args.getSerializable("recipe");
            isEditMode = args.getBoolean("isEditMode", false);
            isApproveMode = args.getBoolean("isApproveMode", false);
            recipeList = (List<Recipe>) args.getSerializable("recipeList");
            if (isEditMode) {
                btnEdit.setText("Chỉnh Sửa");
                btnDelete.setText("Xóa");
            } else if(isApproveMode) {
                btnEdit.setText("Xác Nhận");
                btnDelete.setText("Hủy");
            }
            tvToolbarTitle.setText(recipe.getTitle());
            tvDescription.setText(recipe.getDescription());
            //hien thi nguyen lieu tren tung dong
            for (Ingredient ingredient : recipe.getIngredients()) {
                TextView ingredientTextView = new TextView(getContext());
                ingredientTextView.setText(ingredient.getName() + ": " + ingredient.getAmount() + " " + ingredient.getUnit());
                ingredientTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                ingredientTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                llIngredients.addView(ingredientTextView);
            }

            //hien thi cac tung tren tung dong
            for (String step : recipe.getSteps()) {
                TextView stepTextView = new TextView(getContext());
                stepTextView.setText(step);
                stepTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                stepTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                llSteps.addView(stepTextView);
            }


            String imageUrl = recipe.getImageUrl();
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
            if (recipe != null && isEditMode) {
                Toast.makeText(getContext(), "Chức năng sửa công thức: " + recipe.getTitle(), Toast.LENGTH_SHORT).show();
                Log.d("RecipeDetail", "Chuyển sang chế độ sửa: " + recipe.getTitle());

                PostRecipeFragment editRecipe = new PostRecipeFragment();
                if (args != null) {
                    editRecipe.setArguments(args);
                } else {
                    Log.w("RecipeDetail", "Arguments bị null khi chuyển sang PostRecipeFragment");
                }

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.admin_fragment_container, editRecipe)
                        .addToBackStack(null)
                        .commit();

            } else if (recipe != null && isApproveMode) {
                //TODO: Đưa công thức chờ duyệt sang công thức hoàn chỉnh trong database
                recipe.setStatus(true);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference recipeRef = db.collection("recipes").document(recipe.getId());

                recipeRef.update("status", recipe.getStatus())
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Đã chấp nhận công thức: " + recipe.getTitle(), Toast.LENGTH_SHORT).show();
                            Log.d("RecipeDetail", "Chế độ duyệt: Đã chấp nhận " + recipe.getTitle());
                            requireActivity().getSupportFragmentManager().popBackStack();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Lỗi khi cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                            Log.e("RecipeDetail", "Lỗi cập nhật trạng thái", e);
                        });

            } else {
                Toast.makeText(getContext(), "Chế độ không rõ", Toast.LENGTH_SHORT).show();
                Log.w("RecipeDetail", "Không xác định được chế độ hiện tại");
            }
        });


        btnDelete.setOnClickListener(v -> {
            if (recipe != null) {
                // Xóa công thức khỏi Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("recipes").document(recipe.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Đã xóa công thức: " + recipe.getTitle(), Toast.LENGTH_SHORT).show();
                            Log.d("RecipeDetail", "Đã xóa công thức khỏi Firestore");

                            // Quay về Fragment trước sau khi xóa thành công
                            requireActivity().getSupportFragmentManager().popBackStack();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Lỗi khi xóa công thức", Toast.LENGTH_SHORT).show();
                            Log.e("RecipeDetail", "Lỗi xóa công thức", e);
                        });
            } else {
                Toast.makeText(getContext(), "Không thể xóa công thức", Toast.LENGTH_SHORT).show();
                Log.w("RecipeDetail", "recipe bị null");
            }
        });

    }
}
