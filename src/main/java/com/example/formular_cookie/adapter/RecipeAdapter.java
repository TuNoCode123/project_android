package com.example.formular_cookie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.formular_cookie.R;
import com.example.formular_cookie.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private final Context context;
    private List<Recipe> recipeList;
    private OnRecipeClickListener listener;
    private boolean isLoadingMore = false;

    // Interface để xử lý sự kiện click vào công thức
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
            return new RecipeViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecipeViewHolder) {
            bindRecipeViewHolder((RecipeViewHolder) holder, position);
        }
    }

    private void bindRecipeViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.tvRecipeTitle.setText(recipe.getTitle());

        // Định dạng thời gian nấu thành "Sẵn sàng trong X phút"
        String readyTime = "Ready in " + recipe.getReadyInMinutes() + " min";
        holder.tvTime.setText(readyTime);

        // Định dạng lượt thích
        holder.tvLikes.setText(formatNumber(recipe.getLikes()));

        // Đặt tên tác giả
        if (recipe.getSourceName() != null && !recipe.getSourceName().isEmpty()) {
            holder.tvAuthorName.setText(recipe.getSourceName());
        } else {
            holder.tvAuthorName.setText("Spoonacular");
        }

        // Đặt người theo dõi nếu có, nếu không thì hiển thị danh mục
        if (recipe.getFollowers() > 0) {
            holder.tvFollowers.setText(formatNumber(recipe.getFollowers()) + " Followers");
        } else if (recipe.getCategory() != null && !recipe.getCategory().isEmpty()) {
            holder.tvFollowers.setText(recipe.getCategory());
        } else {
            holder.tvFollowers.setText("Recipe");
        }

        // Tải hình ảnh công thức
        Glide.with(context)
                .load(recipe.getFullImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(holder.ivRecipe);

        // Load author image if available, otherwise use placeholder
        // if (recipe.getAuthorImageUrl() != null &&
        // !recipe.getAuthorImageUrl().isEmpty()) {
        // Glide.with(context)
        // .load(recipe.getAuthorImageUrl())
        // .placeholder(R.drawable.placeholder_avatar)
        // .error(R.drawable.error_avatar)
        // .circleCrop()
        // .into(holder.ivAuthor);
        // } else {
        // Glide.with(context)
        // .load(R.drawable.chef_avatar)
        // .circleCrop()
        // .into(holder.ivAuthor);
        // }
    }

    @Override
    public int getItemCount() {
        return recipeList.size() + (isLoadingMore ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return (isLoadingMore && position == recipeList.size()) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    // Thêm công thức mới vào danh sách
    public void addData(List<Recipe> newRecipes) {
        if (newRecipes != null) {
            int startPosition = recipeList.size();
            recipeList.addAll(newRecipes);
            notifyItemRangeInserted(startPosition, newRecipes.size());
        }
    }

    // Xóa tất cả công thức
    public void clearData() {
        recipeList.clear();
        notifyDataSetChanged();
    }

    // Đặt trạng thái đang tải
    public void setLoadingMore(boolean isLoadingMore) {
        if (this.isLoadingMore != isLoadingMore) {
            this.isLoadingMore = isLoadingMore;
            if (isLoadingMore) {
                notifyItemInserted(recipeList.size());
            } else {
                notifyItemRemoved(recipeList.size());
            }
        }
    }

    // Phương thức hỗ trợ định dạng số
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

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
