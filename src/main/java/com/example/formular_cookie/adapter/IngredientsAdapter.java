package com.example.formular_cookie.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.formular_cookie.R;
import com.example.formular_cookie.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {

    private Context context;
    private List<Ingredient> ingredients;

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

        // Set ingredient name
        holder.tvIngredientName.setText(ingredient.getName());

        // Set amount and unit
        holder.tvIngredientAmount.setText(ingredient.getAmount());

        // Set unit
        holder.tvIngredientUnit.setText(ingredient.getUnit());
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
        TextView tvIngredientName, tvIngredientAmount, tvIngredientUnit;

        IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredientName = itemView.findViewById(R.id.tv_ingredient_name);
            tvIngredientAmount = itemView.findViewById(R.id.tv_ingredient_amount);
            tvIngredientUnit = itemView.findViewById(R.id.tv_ingredient_unit);
        }
    }
}
