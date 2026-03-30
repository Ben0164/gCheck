package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        TextInputLayout tilEmail = view.findViewById(R.id.til_login_email);
        TextInputLayout tilPassword = view.findViewById(R.id.til_login_password);

        view.findViewById(R.id.btn_login).setOnClickListener(v -> {
            String email = String.valueOf(tilEmail.getEditText() != null ? tilEmail.getEditText().getText() : "");
            String password = String.valueOf(tilPassword.getEditText() != null ? tilPassword.getEditText().getText() : "");

            tilEmail.setError(null);
            tilPassword.setError(null);

            boolean ok = true;
            if (TextUtils.isEmpty(email.trim())) {
                tilEmail.setError(getString(R.string.error_field_required));
                ok = false;
            }
            if (TextUtils.isEmpty(password)) {
                tilPassword.setError(getString(R.string.error_field_required));
                ok = false;
            }
            if (!ok) {
                return;
            }

            ((MainActivity) requireActivity()).loadFragment(new HomeFragment());
            ((MainActivity) requireActivity()).setBottomNavSelection(R.id.navigation_home);
        });

        view.findViewById(R.id.btn_switch_to_signup).setOnClickListener(v ->
                ((MainActivity) requireActivity()).loadFragment(new SignupFragment()));

        return view;
    }
}
