package com.example.formular_cookie;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;


import java.util.List;

public class RecipeDetailFragment extends Fragment {

    private TextView tvIngredients, tvSteps, tvToolbarTitle;
    private ImageView ivImage, btnBack;
    private Button btnEdit, btnDelete;
    Recipe recipe;
    List<Recipe> recipeList = null;
    private boolean isEditMode;
    private boolean isApproveMode;
    private Bundle args;


    public RecipeDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các view
        tvToolbarTitle = view.findViewById(R.id.tv_detail_title);
        tvIngredients = view.findViewById(R.id.tv_detail_ingredients);
        tvSteps = view.findViewById(R.id.tv_detail_steps);
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
            tvIngredients.setText(recipe.getIngredients());
            tvSteps.setText(recipe.getSteps());


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
                Toast.makeText(getContext(), "Đã chấp nhận công thức", Toast.LENGTH_SHORT).show();
                Log.d("RecipeDetail", "Chế độ duyệt: Đã chấp nhận " + recipe.getTitle());
            } else {
                Toast.makeText(getContext(), "Chế độ không rõ", Toast.LENGTH_SHORT).show();
                Log.w("RecipeDetail", "Không xác định được chế độ hiện tại");
            }
        });


        btnDelete.setOnClickListener(v -> {
            if (recipe != null && recipeList != null) {
                if (recipeList.remove(recipe)) {
                    //TODO: xóa công thức (hoàn chỉnh hoặc chờ duyệt) trong database
                    Toast.makeText(getContext(), "Đã xóa công thức: " + recipe.getTitle(), Toast.LENGTH_SHORT).show();
                    Log.d("RecipeDetail", "Đã xóa công thức khỏi danh sách");

                    // Quay về Fragment trước
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy công thức để xóa", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Không thể xóa công thức", Toast.LENGTH_SHORT).show();
                Log.w("RecipeDetail", "recipe hoặc recipeList bị null");
            }
        });
    }
}
