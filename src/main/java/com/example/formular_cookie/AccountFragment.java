package com.example.formular_cookie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment {

    private TextView tvUserName, tvUserEmail;
    private Button btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Giả lập thông tin người dùng - bạn có thể thay thế bằng dữ liệu thực tế
        String userName = "Nguyễn Văn A";
        String userEmail = "nguyenvana@example.com";

        tvUserName.setText("Tên: " + userName);
        tvUserEmail.setText("Email: " + userEmail);

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            // TODO: đăng xuất tài khoản
        });

        return view;
    }
}