package com.example.formular_cookie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            boolean isFirstTime = prefs.getBoolean("first_time", true);
            if (isFirstTime) {
                // Nếu lần đầu tiên mở ứng dụng, chuyển đến IntroActivity
                startActivity(new Intent(SplashActivity.this, IntroActivity.class));
            } else {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Log.d("SplashActivity", "Cleared Firestore persistence");
                // Lấy thông tin người dùng từ Firebase
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    db.collection("users").document(uid).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String role = document.getString("role");
                                Log.d("SplashActivity", "User role: " + role);

                                if ("admin".equals(role)) {
                                    Intent intent = new Intent(SplashActivity.this, AdminActivity.class);
                                    intent.putExtra("fragment", "admin");
                                    startActivity(intent);
                                } else if ("user".equals(role)) {
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                } else {
                                    Toast.makeText(SplashActivity.this, "Unknown role: " + role, Toast.LENGTH_SHORT)
                                            .show();
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                }
                            } else {
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            }
                        } else {
                            Log.e("SplashActivity", "Error fetching user document: ", task.getException());
                            Toast.makeText(SplashActivity.this, "Error fetching user data.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        }
                        finish();
                    });
                } else {
                    Log.d("SplashActivity", "No user logged in");
                    // Nếu người dùng chưa đăng nhập, chuyển đến LoginActivity
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 2000); // Thời gian chờ 2 giây trước khi chuyển
    }

}
