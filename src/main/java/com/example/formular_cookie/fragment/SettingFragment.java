package com.example.formular_cookie.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.formular_cookie.LoginActivity;
import com.example.formular_cookie.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class SettingFragment extends AppCompatActivity {
    private Button signout;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Spinner language;
    private Button back;
    private String[] languageArray = {"Tiếng Việt", "English", "French"};
    private String[] languageCodes = {"vi", "en", "fr"};

    // SharedPreferences constants
    private static final String LANGUAGE_PREFERENCE = "language_preference";
    private static final String LANGUAGE_KEY = "language_key";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize SharedPreferences BEFORE setContentView
        sharedPreferences = getSharedPreferences(LANGUAGE_PREFERENCE, MODE_PRIVATE);

        // Apply saved language preference
        setAppLanguage();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_fragment);

        signout = findViewById(R.id.signout);
        language = findViewById(R.id.Language);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> finish());

        // Set up language spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, languageArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language.setAdapter(adapter);

        // Set current language selection
        String currentLang = sharedPreferences.getString(LANGUAGE_KEY, Locale.getDefault().getLanguage());
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(currentLang)) {
                language.setSelection(i);
                break;
            }
        }

        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = languageCodes[position];
                if (!selectedLanguage.equals(sharedPreferences.getString(LANGUAGE_KEY, ""))) {
                    changeLanguage(selectedLanguage);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        signout.setOnClickListener(v -> {
            mAuth.signOut();
            finish();
            startActivity(new Intent(SettingFragment.this, LoginActivity.class));
        });
    }

    private void setAppLanguage() {
        try {
            String lang = sharedPreferences.getString(LANGUAGE_KEY, Locale.getDefault().getLanguage());
            Locale myLocale = new Locale(lang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.setLocale(myLocale);
            res.updateConfiguration(conf, dm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeLanguage(String languageCode) {
        try {
            // Save preference
            sharedPreferences.edit().putString(LANGUAGE_KEY, languageCode).apply();

            // Apply language change
            setAppLanguage();

            // Restart activity
            Intent refresh = new Intent(this, SettingFragment.class);
            startActivity(refresh);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to change language", Toast.LENGTH_SHORT).show();
        }
    }
}