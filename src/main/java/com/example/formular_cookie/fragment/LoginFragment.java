package com.example.formular_cookie.fragment;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.formular_cookie.MainActivity;
import com.example.formular_cookie.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private CheckBox rememberUser;
    private FirebaseAuth mAuth;
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
        editTextUsername = view.findViewById(R.id.iemail);
        editTextPassword = view.findViewById(R.id.password);
        buttonLogin = view.findViewById(R.id.btnLogin);
        rememberUser = view.findViewById(R.id.rememberuser);
        // Lấy thông tin người dùng hiện tại từ Firebase (nếu đã đăng nhập trước đó và chưa bị đăng xuất)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        ||rememberUser.isChecked()
        if(user!=null||rememberUser.isChecked()){
            Intent intent = new Intent(getContext(), MainActivity.class);
            // Bắt đầu hoạt động mới
            startActivity(intent);
            // Kết thúc activity hiện tại để tránh quay lại màn đăng nhập khi nhấn nút Back
            requireActivity().finish();
        }
        buttonLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String usermail = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (usermail.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please enter both username and password", Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.signInWithEmailAndPassword(usermail, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}