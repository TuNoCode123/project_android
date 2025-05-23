package com.example.formular_cookie.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import com.example.formular_cookie.model.Recipe;

import java.util.ArrayList;
import java.util.List;

// Adapter for displaying a list of recipes in a RecyclerView.
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context context;
    private List<Recipe> recipeList;
    private OnRecipeClickListener listener;

    // Interface for handling recipe click events.
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe, int position);
    }

    // Constructor to initialize the adapter with a context.
    public RecipeAdapter(Context context) {
        this.context = context;
        this.recipeList = new ArrayList<>();
    }

    // Sets a listener for recipe click events.
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

        holder.tvAuthorName.setText(recipe.getAuthorName());
        // Set followers if available, otherwise show category
        // if (recipe.getAuthorFollowers() > 0) {
        // holder.tvFollowers.setText(formatNumber(recipe.getAuthorFollowers()) + "
        // Followers");
        // } else if (recipe.getCategory() != null && !recipe.getCategory().isEmpty()) {
        // holder.tvFollowers.setText(recipe.getCategory());
        // } else {
        // holder.tvFollowers.setText("Recipe");
        // }

        // Tải ảnh công thức
        holder.progressImage.setVisibility(View.VISIBLE);

        Glide.with(context)
                .load(recipe.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .listener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
                                                boolean isFirstResource) {
                        holder.progressImage.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model,
                                                   Target<android.graphics.drawable.Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        holder.progressImage.setVisibility(View.GONE);
                        return false;
                    }
                })
                .centerCrop()
                .into(holder.ivRecipe);

        // Tải ảnh tác giả, nếu không có thì dùng ảnh mặc định
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

    // Updates the data in the adapter and refreshes the view.
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

    // ViewHolder class for holding recipe views.
    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecipe, ivAuthor;
        TextView tvRecipeTitle, tvAuthorName, tvFollowers, tvLikes, tvTime;
        ProgressBar progressImage;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            ivRecipe = itemView.findViewById(R.id.iv_recipe);
            ivAuthor = itemView.findViewById(R.id.iv_author);
            tvRecipeTitle = itemView.findViewById(R.id.tv_recipe_title);
            tvAuthorName = itemView.findViewById(R.id.tv_author_name);
            // tvFollowers = itemView.findViewById(R.id.tv_followers);
            tvLikes = itemView.findViewById(R.id.tv_likes);
            tvTime = itemView.findViewById(R.id.tv_time);
            progressImage = itemView.findViewById(R.id.progress_image);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRecipeClick(recipeList.get(position), position);
                }
            });
        }
    }
}