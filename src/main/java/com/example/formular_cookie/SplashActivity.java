package com.example.formular_cookie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("first_time", true); // thay đổi giá trị
            editor.putBoolean("is_logged_in", false); // hoặc thêm mới
            editor.apply(); // hoặc .commit()
            boolean isFirstTime = prefs.getBoolean("first_time", true);

            if (isFirstTime) {
                startActivity(new Intent(SplashActivity.this, IntroActivity.class));
            } else {
                boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
                if (isLoggedIn) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
            }
            finish();
        }, 2000);
    }
}
