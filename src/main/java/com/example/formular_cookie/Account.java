package com.example.formular_cookie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.atomic.AtomicReference;

public class Account extends Fragment {
    private Button signout;
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
        signout = view.findViewById(R.id.signout);
        AtomicReference<Fragment> selectedFragment = new AtomicReference<>();
        signout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            requireActivity().finish();
            selectedFragment.set(new login());
        });
    }
}
