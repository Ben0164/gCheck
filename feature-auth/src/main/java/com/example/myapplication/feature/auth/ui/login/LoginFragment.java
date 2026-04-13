package com.example.myapplication.feature.auth.ui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.feature.auth.repository.AuthRepository;
import com.example.myapplication.palay.data.repository.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {

    private AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        authRepository = new AuthRepository(requireContext());

        TextInputLayout tilEmail = view.findViewById(R.id.til_login_email);
        TextInputLayout tilPassword = view.findViewById(R.id.til_login_password);
        TextInputEditText etEmail = view.findViewById(R.id.et_login_email);
        TextInputEditText etPassword = view.findViewById(R.id.et_login_password);
        MaterialButton btnLogin = view.findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            if (TextUtils.isEmpty(email)) {
                tilEmail.setError("Email is required");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                tilPassword.setError("Password is required");
                return;
            }

            tilEmail.setError(null);
            tilPassword.setError(null);
            btnLogin.setEnabled(false);

            authRepository.login(email, password, user -> {
                if (user != null) {
                    SessionManager.setCurrentUser(user);
                    Toast.makeText(requireContext(), R.string.login_success, Toast.LENGTH_SHORT).show();
                    // Navigation will be handled via Activity
                } else {
                    btnLogin.setEnabled(true);
                    Toast.makeText(requireContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }
}
