package com.example.formular_cookie.Login_Signup;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.formular_cookie.R;


public class login_signup extends AppCompatActivity {

    private TextView tvLogin, tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.login_signup);

        tvLogin = findViewById(R.id.tvLogin);
        tvRegister = findViewById(R.id.tvRegister);
        switchToFragment(new login());

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(new login());
                animateTextColor(tvLogin, tvRegister);
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
