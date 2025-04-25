package com.example.formular_cookie.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Import Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.formular_cookie.AdminActivity; // Import AdminActivity
import com.example.formular_cookie.MainActivity;
import com.example.formular_cookie.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot; // Import DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore; // Import FirebaseFirestore

public class LoginFragment extends Fragment {
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private CheckBox rememberUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Add Firestore instance
    public static final String SHARED_PREFS = "sharedPrefs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Initialize Firestore
        editTextUsername = view.findViewById(R.id.iemail);
        editTextPassword = view.findViewById(R.id.password);
        buttonLogin = view.findViewById(R.id.btnLogin);
        rememberUser = view.findViewById(R.id.rememberuser);

        // Remove the automatic redirect logic based on rememberUser or existing user
        // This check is now handled in SplashActivity or after successful login

        buttonLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String usermail = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (usermail.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please enter both username and password", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(usermail, password)
                    .addOnCompleteListener(requireActivity(), task -> { // Use requireActivity() for context safety
                        if (task.isSuccessful()) {
                            // Login successful, now check the role
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                checkUserRole(user.getUid());
                            } else {
                                // Should not happen if login was successful, but handle defensively
                                Toast.makeText(getContext(), "Login successful but user data unavailable.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Login failed
                            Toast.makeText(getContext(), "Login failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void checkUserRole(String uid) {
        db.collection("users").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String role = document.getString("role");
                            Log.d("LoginFragment", "User role: " + role);

                            Intent intent;
                            if ("admin".equals(role)) {
                                intent = new Intent(getContext(), AdminActivity.class);
                            } else if ("user".equals(role)) {
                                intent = new Intent(getContext(), MainActivity.class);
                            } else {
                                // Unknown role, default to user or show error
                                Toast.makeText(getContext(), "Unknown user role: " + role, Toast.LENGTH_SHORT).show();
                                // Optionally, sign out the user if the role is invalid
                                // mAuth.signOut();
                                return; // Don't proceed to start activity
                            }
                            // Clear previous activities from the stack and start the new one
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            requireActivity().finish(); // Finish LoginActivity

                        } else {
                            Log.d("LoginFragment", "User document does not exist in Firestore.");
                            Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                            // Optionally sign out if data is missing
                            // mAuth.signOut();
                        }
                    } else {
                        Log.e("LoginFragment", "Error fetching user document: ", task.getException());
                        Toast.makeText(getContext(), "Error fetching user data.", Toast.LENGTH_SHORT).show();
                        // Optionally sign out on error
                        // mAuth.signOut();
                    }
                });
    }
}