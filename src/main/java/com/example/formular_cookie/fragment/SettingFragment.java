package com.example.formular_cookie.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Process;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.formular_cookie.BaseActivity;
import com.example.formular_cookie.LoginActivity;
import com.example.formular_cookie.R;
import com.example.formular_cookie.SplashActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class SettingFragment extends BaseActivity {
    private Button signout;
    private Button btnBack;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Spinner language;
    private String[] languageArray = { "Tiếng Việt", "English", "French" };
    private String[] languageCodes = { "vi", "en", "fr" };

    // SharedPreferences constants
    private static final String LANGUAGE_PREFERENCE = "language_preference";
    private static final String LANGUAGE_KEY = "language_key";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Khởi tạo SharedPreferences trước setContentView
        sharedPreferences = getSharedPreferences(LANGUAGE_PREFERENCE, MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_fragment);

        signout = findViewById(R.id.signout);
        btnBack = findViewById(R.id.back);
        language = findViewById(R.id.Language);

        // Thiết lập nút Back
        btnBack.setOnClickListener(v -> {
            Toast.makeText(this, "Đang quay lại...", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Thiết lập spinner ngôn ngữ
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, languageArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language.setAdapter(adapter);

        // Thiết lập ngôn ngữ hiện tại trong spinner
        String currentLang = sharedPreferences.getString(LANGUAGE_KEY, Locale.getDefault().getLanguage());
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(currentLang)) {
                language.setSelection(i);
                break;
            }
        }

        // Xử lý sự kiện khi chọn ngôn ngữ từ spinner
        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = languageCodes[position];
                String currentLanguage = sharedPreferences.getString(LANGUAGE_KEY, Locale.getDefault().getLanguage());

                // Chỉ đổi ngôn ngữ khi chọn ngôn ngữ khác với hiện tại
                if (!selectedLanguage.equals(currentLanguage)) {
                    changeAppLanguage(selectedLanguage);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });

        // Xử lý sự kiện đăng xuất
        signout.setOnClickListener(v -> {
            mAuth.signOut();
            finish();
            Intent intent = new Intent(SettingFragment.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Thay đổi ngôn ngữ cho toàn bộ ứng dụng
     * 
     * @param languageCode Mã ngôn ngữ (vi, en, fr)
     */
    private void changeAppLanguage(String languageCode) {
        try {
            // Lưu ngôn ngữ đã chọn vào SharedPreferences
            sharedPreferences.edit().putString(LANGUAGE_KEY, languageCode).apply();

            // Hiển thị thông báo thay đổi ngôn ngữ thành công
            Toast.makeText(this, "Đổi ngôn ngữ thành công, đang khởi động lại...", Toast.LENGTH_SHORT).show();

            // Sử dụng cách khởi động lại ứng dụng đáng tin cậy hơn
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Kết thúc tiến trình hiện tại để đảm bảo tạo mới hoàn toàn
            Runtime.getRuntime().exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể thay đổi ngôn ngữ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}