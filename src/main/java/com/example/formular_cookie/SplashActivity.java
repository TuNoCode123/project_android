package com.example.formular_cookie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            boolean isFirstTime = prefs.getBoolean("first_time", true);

            if (isFirstTime) {
                startActivity(new Intent(SplashActivity.this, IntroActivity.class));
            } else {
                // Lấy thông tin người dùng hiện tại từ Firebase (nếu đã đăng nhập trước đó và chưa bị đăng xuất)
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
                if (user!=null) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
            }
            finish();
        }, 2000);
    }
}
