package com.example.myapplication.feature.auth.ui.signup;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.feature.auth.repository.AuthRepository;
import com.example.myapplication.palay.data.repository.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignupFragment extends Fragment {

    private AuthRepository authRepository;
    private TextInputLayout tilEmail, tilPassword, tilConfirm;
    private TextInputEditText etEmail, etPassword, etConfirm;
    private RadioGroup rgRole;
    private MaterialButton btnSignup;
    private CircularProgressIndicator progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        authRepository = new AuthRepository(requireContext());

        initViews(view);
        setupRealTimeValidation();

        btnSignup.setOnClickListener(v -> handleSignup());

        return view;
    }

    private void initViews(View view) {
        tilEmail = view.findViewById(R.id.til_signup_email);
        tilPassword = view.findViewById(R.id.til_signup_password);
        tilConfirm = view.findViewById(R.id.til_signup_confirm);

        etEmail = view.findViewById(R.id.et_signup_email);
        etPassword = view.findViewById(R.id.et_signup_password);
        etConfirm = view.findViewById(R.id.et_signup_confirm);
        rgRole = view.findViewById(R.id.rg_role);

        btnSignup = view.findViewById(R.id.btn_signup);
        progress = view.findViewById(R.id.progress_signup);
    }

    private void setupRealTimeValidation() {
        etEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail(s.toString(), false);
            }
        });

        etPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword(s.toString(), false);
                // Also re-validate confirm password when password changes
                validateConfirmPassword(etConfirm.getText().toString(), s.toString(), false);
            }
        });

        etConfirm.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateConfirmPassword(s.toString(), etPassword.getText().toString(), false);
            }
        });
    }

    private boolean validateEmail(String email, boolean forceShowError) {
        if (email.isEmpty()) {
            tilEmail.setError(forceShowError ? "Email is required" : null);
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email (e.g. example@gmail.com)");
            return false;
        }

        tilEmail.setError(null);
        return true;
    }

    private boolean validatePassword(String pass, boolean forceShowError) {
        if (pass.isEmpty()) {
            tilPassword.setError(forceShowError ? "Password is required" : null);
            return false;
        }

        if (pass.length() < 8) {
            tilPassword.setError("At least 8 characters required");
            return false;
        }

        if (!pass.matches(".*[A-Z].*")) {
            tilPassword.setError("Must include 1 uppercase letter");
            return false;
        }

        if (!pass.matches(".*[a-z].*")) {
            tilPassword.setError("Must include 1 lowercase letter");
            return false;
        }

        if (!pass.matches(".*[0-9].*")) {
            tilPassword.setError("Must include 1 number");
            return false;
        }

        tilPassword.setError(null);
        return true;
    }

    private boolean validateConfirmPassword(String confirm, String pass, boolean forceShowError) {
        if (confirm.isEmpty()) {
            tilConfirm.setError(forceShowError ? "Please confirm your password" : null);
            return false;
        }

        if (!confirm.equals(pass)) {
            tilConfirm.setError("Passwords do not match");
            return false;
        }

        tilConfirm.setError(null);
        return true;
    }

    private void handleSignup() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString();
        String confirm = etConfirm.getText().toString();
        String role = rgRole.getCheckedRadioButtonId() == R.id.rb_buyer ? "buyer" : "farmer";

        // Validate all fields on submit
        boolean isEmailValid = validateEmail(email, true);
        boolean isPassValid = validatePassword(pass, true);
        boolean isConfirmValid = validateConfirmPassword(confirm, pass, true);

        if (!isEmailValid) {
            etEmail.requestFocus();
            return;
        }
        if (!isPassValid) {
            etPassword.requestFocus();
            return;
        }
        if (!isConfirmValid) {
            etConfirm.requestFocus();
            return;
        }

        setLoading(true);
        authRepository.signup("", email, pass, role, user -> {
            if (user != null) {
                SessionManager.setCurrentUser(user);
                // Profile completion navigation handled via Activity callback or local fragment transition
            } else {
                setLoading(false);
                Toast.makeText(requireContext(), "Signup failed. Email may already exist.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        btnSignup.setEnabled(!isLoading);
        progress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSignup.setText(isLoading ? "" : getString(R.string.sign_up));
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}
