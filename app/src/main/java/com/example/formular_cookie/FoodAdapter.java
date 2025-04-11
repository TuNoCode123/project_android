package com.example.formular_cookie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;


// Adapter dùng để đổ dữ liệu ra RecyclerView
public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    // Danh sách các món ăn
    private List<FoodItem> foodList;
    // Context để sử dụng LayoutInflater hoặc Glide
    private Context context;
    // Constructor để truyền context và danh sách dữ liệu vào Adapter
    public FoodAdapter(Context context, List<FoodItem> foodList) {
        this.context = context;
        this.foodList = foodList;
    }
    // ViewHolder chứa các View con của 1 item trong RecyclerView
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage, userAvatar;
        TextView foodTitle, foodDescription, userName, userFollowers;
        // Constructor nhận vào itemView và ánh xạ các view bằng findViewById
        public FoodViewHolder(View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodTitle = itemView.findViewById(R.id.foodTitle);
            foodDescription = itemView.findViewById(R.id.foodDescription);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            userName = itemView.findViewById(R.id.userName);
            userFollowers = itemView.findViewById(R.id.userFollowers);
        }
    }
    // Tạo view mới cho item khi RecyclerView cần
    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate layout item_food.xml để tạo View cho từng item
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view); // Trả về ViewHolder chứa view vừa tạo
    }
    // Gán dữ liệu vào các View trong ViewHolder
    @Override
    public void onBindViewHolder(FoodViewHolder holder, int position) {
        // Lấy phần tử tại vị trí position trong danh sách
        FoodItem item = foodList.get(position);
        // Gán dữ liệu cho các view trong layout
        holder.foodTitle.setText(item.title);
        holder.foodDescription.setText(item.description);
        holder.userName.setText(item.userName);
        holder.userFollowers.setText(item.userFollowers);

        // Load image (dùng Glide hoặc Picasso)
        Glide.with(context).load(item.imageResId).transform(new RoundedCorners(54)).into(holder.foodImage);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }
}
