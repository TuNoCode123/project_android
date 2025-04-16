package com.example.formular_cookie;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class LoginActivity extends AppCompatActivity {

    private TextView tvLogin, tvRegister;
    private View underlineLogin;
    private View underlineRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvLogin = findViewById(R.id.tvLogin);
        tvRegister = findViewById(R.id.tvRegister);
        underlineLogin = findViewById(R.id.underlineLogin);
        underlineRegister = findViewById(R.id.underlineRegister);
        switchToFragment(new login());

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLogin.setTextColor(Color.parseColor("#FF9800"));
                tvRegister.setTextColor(Color.parseColor("#B0B0B0"));
                underlineLogin.setVisibility(View.VISIBLE);
                underlineRegister.setVisibility(View.INVISIBLE);
                switchToFragment(new login());
                animateTextColor(tvLogin, tvRegister);
            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLogin.setTextColor(Color.parseColor("#B0B0B0"));
                tvRegister.setTextColor(Color.parseColor("#FF9800"));
                underlineLogin.setVisibility(View.INVISIBLE);
                underlineRegister.setVisibility(View.VISIBLE);
                switchToFragment(new signin());
                animateTextColor(tvRegister, tvLogin);
            }
        });
    }

    private void switchToFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    private void animateTextColor(final TextView active, final TextView inactive) {
        int colorActive = Color.parseColor("#FF9800");
        int colorInactive = Color.parseColor("#B0B0B0");

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorInactive, colorActive);
        colorAnimation.setDuration(300);
        colorAnimation.addUpdateListener(animator -> active.setTextColor((int) animator.getAnimatedValue()));
        colorAnimation.start();

        ValueAnimator colorAnimationReverse = ValueAnimator.ofObject(new ArgbEvaluator(), colorActive, colorInactive);
        colorAnimationReverse.setDuration(300);
        colorAnimationReverse.addUpdateListener(animator -> inactive.setTextColor((int) animator.getAnimatedValue()));
        colorAnimationReverse.start();
    }
}
