package com.example.formular_cookie.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.formular_cookie.R;
import com.example.formular_cookie.model.AdminRecipe;

import java.util.List;

public class AdminRecipeAdapter extends RecyclerView.Adapter<AdminRecipeAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(AdminRecipe adminRecipe);
    }

    private List<AdminRecipe> adminRecipeList;
    private OnItemClickListener listener;

    public AdminRecipeAdapter(List<AdminRecipe> adminRecipeList, OnItemClickListener listener) {
        this.adminRecipeList = adminRecipeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminRecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminRecipeAdapter.ViewHolder holder, int position) {
        AdminRecipe adminRecipe = adminRecipeList.get(position);
        holder.title.setText(adminRecipe.getTitle());
        holder.viewDetail.setOnClickListener(v -> listener.onItemClick(adminRecipe));
    }

    @Override
    public int getItemCount() {
        return adminRecipeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        Button viewDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_recipe_title);
            viewDetail = itemView.findViewById(R.id.btn_view_detail);
        }
    }
}