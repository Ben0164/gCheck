package com.example.myapplication.feature.auth.ui.signup;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.myapplication.core.common.FeatureNavigationHost;
import com.example.myapplication.core.common.SessionManager;
import com.example.myapplication.feature.auth.R;
import com.example.myapplication.feature.auth.repository.AuthRepository;

public class SignupFragment extends Fragment {

    private AuthRepository authRepository;
    private EditText etEmail, etPassword, etConfirm;
    private LinearLayout layoutFarmer, layoutBuyer;
    private ImageView ivFarmerCheck, ivBuyerCheck;
    private ImageView ivPasswordToggle, ivConfirmPasswordToggle;
    private Button btnSignup;
    private ProgressBar progress;
    private String selectedRole = "farmer"; // Default selection

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        authRepository = new AuthRepository(requireContext());

        etEmail = view.findViewById(R.id.et_signup_email);
        etPassword = view.findViewById(R.id.et_signup_password);
        etConfirm = view.findViewById(R.id.et_signup_confirm);
        layoutFarmer = view.findViewById(R.id.layout_farmer);
        layoutBuyer = view.findViewById(R.id.layout_buyer);
        ivFarmerCheck = view.findViewById(R.id.iv_farmer_check);
        ivBuyerCheck = view.findViewById(R.id.iv_buyer_check);
        ivPasswordToggle = view.findViewById(R.id.iv_password_toggle_signup);
        ivConfirmPasswordToggle = view.findViewById(R.id.iv_confirm_password_toggle);
        btnSignup = view.findViewById(R.id.btn_signup);
        progress = view.findViewById(R.id.progress_signup);

        setupRoleSelection();
        setupRealTimeValidation();

        btnSignup.setOnClickListener(v -> handleSignup());

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
            etPassword.setSelection(etPassword.getText().length());
        });

        // Handle confirm password visibility toggle
        ivConfirmPasswordToggle.setOnClickListener(v -> {
            if (etConfirm.getInputType() == (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Show password
                etConfirm.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivConfirmPasswordToggle.setImageResource(R.drawable.ic_eye_open);
            } else {
                // Hide password
                etConfirm.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivConfirmPasswordToggle.setImageResource(R.drawable.ic_eye_closed);
            }
            etConfirm.setSelection(etConfirm.getText().length());
        });

        // Handle login link click
        TextView btnSwitchToLogin = view.findViewById(R.id.btn_switch_to_login);
        btnSwitchToLogin.setOnClickListener(v -> {
            if (getActivity() instanceof FeatureNavigationHost) {
                ((FeatureNavigationHost) getActivity()).openFragment(new com.example.myapplication.feature.auth.ui.login.LoginFragment());
            }
        });

        return view;
    }

    private void setupRoleSelection() {
        layoutFarmer.setOnClickListener(v -> {
            selectedRole = "farmer";
            ivFarmerCheck.setImageResource(R.drawable.ic_radio_selected);
            ivBuyerCheck.setImageResource(R.drawable.ic_radio_unselected);
        });

        layoutBuyer.setOnClickListener(v -> {
            selectedRole = "buyer";
            ivFarmerCheck.setImageResource(R.drawable.ic_radio_unselected);
            ivBuyerCheck.setImageResource(R.drawable.ic_radio_selected);
        });
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
            if (forceShowError) etEmail.setError("Email is required");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email (e.g. example@gmail.com)");
            return false;
        }

        etEmail.setError(null);
        return true;
    }

    private boolean validatePassword(String pass, boolean forceShowError) {
        if (pass.isEmpty()) {
            if (forceShowError) etPassword.setError("Password is required");
            return false;
        }

        if (pass.length() < 8) {
            etPassword.setError("At least 8 characters required");
            return false;
        }

        if (!pass.matches(".*[A-Z].*")) {
            etPassword.setError("Must include 1 uppercase letter");
            return false;
        }

        if (!pass.matches(".*[a-z].*")) {
            etPassword.setError("Must include 1 lowercase letter");
            return false;
        }

        if (!pass.matches(".*[0-9].*")) {
            etPassword.setError("Must include 1 number");
            return false;
        }

        etPassword.setError(null);
        return true;
    }

    private boolean validateConfirmPassword(String confirm, String pass, boolean forceShowError) {
        if (confirm.isEmpty()) {
            if (forceShowError) etConfirm.setError("Please confirm your password");
            return false;
        }

        if (!confirm.equals(pass)) {
            etConfirm.setError("Passwords do not match");
            return false;
        }

        etConfirm.setError(null);
        return true;
    }

    private void handleSignup() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString();
        String confirm = etConfirm.getText().toString();

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
        authRepository.signup("", email, pass, selectedRole, user -> {
            try {
                setLoading(false);
                if (user != null) {
                    SessionManager.setCurrentUser(user);
                    Toast.makeText(requireContext(), "Signup successful!", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null && getActivity() instanceof FeatureNavigationHost) {
                        ((FeatureNavigationHost) getActivity()).selectHomeTab();
                    }
                } else {
                    Toast.makeText(requireContext(), "Signup failed. Email may already exist.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                setLoading(false);
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        btnSignup.setEnabled(!isLoading);
        progress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSignup.setText(isLoading ? "" : "Create Account");
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}
