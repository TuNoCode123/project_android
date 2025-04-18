package com.example.formular_cookie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

//add this to manifest
public class SettingFragment extends AppCompatActivity {
    private Button signout;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Spinner language;
    private String[] languageArray={"Tiếng Việt","English","French"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_fragment);
        signout = findViewById(R.id.signout);
        language = findViewById(R.id.Language);
        language.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languageArray));
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                Intent intent = new Intent(SettingFragment.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
