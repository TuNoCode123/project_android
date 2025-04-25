package com.example.formular_cookie;

import static android.content.ContentValues.TAG;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReference;

public class Account extends Fragment {
    private ImageButton Setting;
    private TextView username,Share,like;
    private View underlineShare,underlineLike;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.account, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Setting=view.findViewById(R.id.Setting);
        Share = view.findViewById(R.id.tabShare);
        like = view.findViewById(R.id.tabLike);
        AtomicReference<Fragment> selectedFragment = new AtomicReference<>();
        underlineShare = view.findViewById(R.id.underlineShare);
        underlineLike = view.findViewById(R.id.underlineLike);
        switchToFragment(new Sharefrag());
        Share.setOnClickListener(v -> {
            switchToFragment(new Sharefrag());
            animateTextColor(Share, like);
            underlineShare.setVisibility(View.VISIBLE);
            underlineLike.setVisibility(View.INVISIBLE);
        });

        like.setOnClickListener(v -> {
            switchToFragment(new Likefrag());
            animateTextColor(like, Share);
            underlineLike.setVisibility(View.VISIBLE);
            underlineShare.setVisibility(View.INVISIBLE);
        });
        Setting.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingFragment.class);
            getActivity().finish();
            startActivity(intent);
        });

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        username = view.findViewById(R.id.Username);
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        username.setText(document.getString("name"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }
    private void switchToFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }
    private void animateTextColor(final TextView active, final TextView inactive) {
        int colorActive = Color.parseColor("#00AFFF");
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