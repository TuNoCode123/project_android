package com.example.formular_cookie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private OnDetailClickListener onDetailClickListener;

    public interface OnDetailClickListener {
        void onDetailClick(User user);
    }

    public UserAdapter(Context context, List<User> userList, OnDetailClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.onDetailClickListener = listener;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, emailText;
        Button detailButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_user_name);
            emailText = itemView.findViewById(R.id.text_user_email);
            detailButton = itemView.findViewById(R.id.btn_view_user_detail);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameText.setText(user.getName());
        holder.emailText.setText(user.getEmail());

        holder.detailButton.setOnClickListener(v -> {
            if (onDetailClickListener != null) {
                onDetailClickListener.onDetailClick(user);
            } else {
                Toast.makeText(context, "Chi tiết: " + user.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}

