package com.example.formular_cookie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.formular_cookie.R;
import com.example.formular_cookie.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {

    private final Context context;
    private final List<Ingredient> ingredients;

    public IngredientsAdapter(Context context) {
        this.context = context;
        this.ingredients = new ArrayList<>();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);

        // Đặt tên nguyên liệu
        holder.tvIngredientName.setText(ingredient.getName());

        // Đặt số lượng và đơn vị
//        String amount = String.format("%.1f %s", ingredient.getAmount(), ingredient.getUnit());
//        holder.tvIngredientAmount.setText(amount);

        // Tải hình ảnh nguyên liệu nếu có
//        if (ingredient.getImage() != null && !ingredient.getImage().isEmpty()) {
//            String imageUrl = "https://spoonacular.com/cdn/ingredients_100x100/" + ingredient.getImage();
//            Glide.with(context)
//                    .load(imageUrl)
//                    .placeholder(R.drawable.placeholder_ingredient)
//                    .error(R.drawable.error_ingredient)
//                    .centerCrop()
//                    .into(holder.ivIngredient);
//        } else {
//            holder.ivIngredient.setImageResource(R.drawable.placeholder_ingredient);
//        }
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void updateData(List<Ingredient> newIngredients) {
        ingredients.clear();
        if (newIngredients != null) {
            ingredients.addAll(newIngredients);
        }
        notifyDataSetChanged();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIngredient;
        TextView tvIngredientName, tvIngredientAmount;

        IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIngredient = itemView.findViewById(R.id.iv_ingredient);
            tvIngredientName = itemView.findViewById(R.id.tv_ingredient_name);
            tvIngredientAmount = itemView.findViewById(R.id.tv_ingredient_amount);
        }
    }
}
