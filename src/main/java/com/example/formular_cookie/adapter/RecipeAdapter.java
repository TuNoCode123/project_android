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
import com.example.formular_cookie.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context context;
    private List<Recipe> recipeList;
    private OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe, int position);
    }

    public RecipeAdapter(Context context) {
        this.context = context;
        this.recipeList = new ArrayList<>();
    }

    public void setOnRecipeClickListener(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.tvRecipeTitle.setText(recipe.getTitle());

        // Format ready time as "Ready in X minutes"
        String readyTime = "Ready in " + recipe.getReadyInMinutes() + " min";
        holder.tvTime.setText(readyTime);

        // Format likes
        holder.tvLikes.setText(formatNumber(recipe.getLikes()));

        // Set source name as author
        if (recipe.getSourceName() != null && !recipe.getSourceName().isEmpty()) {
            holder.tvAuthorName.setText(recipe.getSourceName());
        } else {
            holder.tvAuthorName.setText("Unknown");
        }

        // Set followers if available, otherwise show category
        if (recipe.getFollowers() > 0) {
            holder.tvFollowers.setText(formatNumber(recipe.getFollowers()) + " Followers");
        } else if (recipe.getCategory() != null && !recipe.getCategory().isEmpty()) {
            holder.tvFollowers.setText(recipe.getCategory());
        } else {
            holder.tvFollowers.setText("Recipe");
        }

        // Load recipe image
        Glide.with(context)
                .load(recipe.getFullImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(holder.ivRecipe);

        // Load author image if available, otherwise use placeholder
        if (recipe.getAuthorImageUrl() != null && !recipe.getAuthorImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(recipe.getAuthorImageUrl())
                    .placeholder(R.drawable.placeholder_avatar)
                    .error(R.drawable.error_avatar)
                    .circleCrop()
                    .into(holder.ivAuthor);
        } else {
            Glide.with(context)
                    .load(R.drawable.chef_avatar)
                    .circleCrop()
                    .into(holder.ivAuthor);
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    // Update the dataset
    public void updateData(List<Recipe> newRecipes) {
        recipeList.clear();
        if (newRecipes != null) {
            recipeList.addAll(newRecipes);
        }
        notifyDataSetChanged();
    }

    // Clear all data
    public void clearData() {
        recipeList.clear();
        notifyDataSetChanged();
    }

    // Helper method for formatting numbers
    private String formatNumber(int number) {
        if (number < 1000) {
            return String.valueOf(number);
        } else {
            float num = number / 1000f;
            return String.format("%.1fK", num);
        }
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecipe, ivAuthor;
        TextView tvRecipeTitle, tvAuthorName, tvFollowers, tvLikes, tvTime;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            ivRecipe = itemView.findViewById(R.id.iv_recipe);
            ivAuthor = itemView.findViewById(R.id.iv_author);
            tvRecipeTitle = itemView.findViewById(R.id.tv_recipe_title);
            tvAuthorName = itemView.findViewById(R.id.tv_author_name);
            tvFollowers = itemView.findViewById(R.id.tv_followers);
            tvLikes = itemView.findViewById(R.id.tv_likes);
            tvTime = itemView.findViewById(R.id.tv_time);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRecipeClick(recipeList.get(position), position);
                }
            });
        }
    }
}
