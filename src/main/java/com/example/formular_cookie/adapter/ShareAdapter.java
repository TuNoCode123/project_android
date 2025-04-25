package com.example.formular_cookie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.example.formular_cookie.R;
import com.example.formular_cookie.model.ShareItem;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
//tao adapter de hien thi du lieu cua cac thuoc tinh trong recyclerview
public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ItemHolder> {
    Context context;
    List<ShareItem> items;

    public ShareAdapter(Context context, List<ShareItem> items){
        this.items = items;
        this.context = context;
    }
    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.foodsharefrag, parent, false);
        return new ItemHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        ShareItem item = items.get(position);
        holder.itemName.setText(item.getName());
        Glide.with(context).load(item.getImg_url()).into(holder.itemImage);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        TextView itemName;
        ImageView itemImage;
        View view;
        ConstraintLayout constraintLayout;
        public ItemHolder(@NonNull View itemview){
            super(itemview);
            view = itemview;
            constraintLayout = view.findViewById(R.id.item);
            itemName = view.findViewById(R.id.itemview);
            itemImage = view.findViewById(R.id.item_img);
        }
    }
}