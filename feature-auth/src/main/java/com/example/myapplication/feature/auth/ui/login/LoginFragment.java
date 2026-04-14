package com.example.myapplication.feature.auth.ui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.feature.auth.R;
import com.example.myapplication.feature.auth.repository.AuthRepository;
import com.example.myapplication.core.common.SessionManager;
import com.example.myapplication.core.common.FeatureNavigationHost;

public class LoginFragment extends Fragment {

    private AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        authRepository = new AuthRepository(requireContext());

        EditText etEmail = view.findViewById(R.id.et_login_email);
        EditText etPassword = view.findViewById(R.id.et_login_password);
        Button btnLogin = view.findViewById(R.id.btn_login);
        TextView tvSignupLink = view.findViewById(R.id.tv_signup_link);
        ImageView ivPasswordToggle = view.findViewById(R.id.iv_password_toggle);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email is required");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password is required");
                return;
            }

            etEmail.setError(null);
            etPassword.setError(null);
            btnLogin.setEnabled(false);

            authRepository.login(email, password, user -> {
                if (user != null) {
                    SessionManager.setCurrentUser(user);
                    Toast.makeText(requireContext(), R.string.login_success, Toast.LENGTH_SHORT).show();
                    // Navigate to MainActivity on successful login
                    if (getActivity() != null && getActivity() instanceof FeatureNavigationHost) {
                        ((FeatureNavigationHost) getActivity()).selectHomeTab();
                    }
                } else {
                    btnLogin.setEnabled(true);
                    Toast.makeText(requireContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Handle signup link click
        tvSignupLink.setOnClickListener(v -> {
            if (getActivity() instanceof FeatureNavigationHost) {
                ((FeatureNavigationHost) getActivity()).openFragment(new com.example.myapplication.feature.auth.ui.signup.SignupFragment());
            }
        });

        // Handle password visibility toggle
        ivPasswordToggle.setOnClickListener(v -> {
            if (etPassword.getInputType() == (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Show password
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivPasswordToggle.setImageResource(R.drawable.ic_eye_open);
            } else {
                // Hide password
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivPasswordToggle.setImageResource(R.drawable.ic_eye_closed);
            }
            // Move cursor to the end
            etPassword.setSelection(etPassword.getText().length());
        });

        return view;
    }
}
