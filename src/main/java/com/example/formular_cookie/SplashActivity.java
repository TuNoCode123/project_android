package com.example.formular_cookie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            boolean isFirstTime = prefs.getBoolean("first_time", true);
            if (isFirstTime) {
                // Nếu là lần đầu tiên mở app, chuyển đến IntroActivity
                startActivity(new Intent(SplashActivity.this, IntroActivity.class));
            } else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d("SplashActivity", "User Loggin" );
                if (user != null) {
                    String uid = user.getUid();
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                    Log.d("SplashActivity", "User role: " + 1);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String role = snapshot.child("role").getValue(String.class);
                                // In ra Logcat để kiểm tra giá trị của role
                                Log.d("SplashActivity", "User role: " + role);
                                // Nếu user có role admin, chuyển tới AdminActivity
                                if ("admin".equals(role)) {
                                    Intent intent = new Intent(SplashActivity.this, AdminActivity.class);
                                    intent.putExtra("fragment", "admin");
                                    startActivity(intent);
                                } else {
                                    // Nếu không phải admin, chuyển tới MainActivity
                                    startActivity(new Intent(SplashActivity.this, AdminActivity.class));
                                }
                            } else {
                                // Nếu không tìm thấy user trong DB, chuyển tới LoginActivity
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            }
                            finish(); // Đóng SplashActivity sau khi đã chuyển hướng
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý lỗi nếu Firebase query bị lỗi
                            Toast.makeText(SplashActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            finish(); // Đóng SplashActivity
                        }
                    });
                } else {
                    // Nếu user chưa đăng nhập, chuyển tới LoginActivity
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish(); // Đóng SplashActivity
                }
            }
        }, 2000); // Delay 2000ms (2 giây) trước khi chuyển hướng
    }

}
