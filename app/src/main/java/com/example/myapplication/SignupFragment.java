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

public class SignupFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        TextInputLayout tilName = view.findViewById(R.id.til_signup_name);
        TextInputLayout tilEmail = view.findViewById(R.id.til_signup_email);
        TextInputLayout tilPass = view.findViewById(R.id.til_signup_password);
        TextInputLayout tilConfirm = view.findViewById(R.id.til_signup_confirm);

        view.findViewById(R.id.btn_signup).setOnClickListener(v -> {
            String name = text(tilName);
            String email = text(tilEmail);
            String pass = text(tilPass);
            String confirm = text(tilConfirm);

            clearErrors(tilName, tilEmail, tilPass, tilConfirm);

            boolean ok = true;
            if (TextUtils.isEmpty(name.trim())) {
                tilName.setError(getString(R.string.error_field_required));
                ok = false;
            }
            if (TextUtils.isEmpty(email.trim())) {
                tilEmail.setError(getString(R.string.error_field_required));
                ok = false;
            }
            if (TextUtils.isEmpty(pass)) {
                tilPass.setError(getString(R.string.error_field_required));
                ok = false;
            }
            if (!TextUtils.isEmpty(pass) && !pass.equals(confirm)) {
                tilConfirm.setError(getString(R.string.password_mismatch));
                ok = false;
            }
            if (!ok) {
                return;
            }

            ((MainActivity) requireActivity()).loadFragment(new HomeFragment());
            ((MainActivity) requireActivity()).setBottomNavSelection(R.id.navigation_home);
        });

        view.findViewById(R.id.btn_switch_to_login).setOnClickListener(v ->
                ((MainActivity) requireActivity()).loadFragment(new LoginFragment()));

        return view;
    }

    private static String text(TextInputLayout til) {
        if (til.getEditText() == null) {
            return "";
        }
        return String.valueOf(til.getEditText().getText());
    }

    private static void clearErrors(TextInputLayout... layouts) {
        for (TextInputLayout l : layouts) {
            l.setError(null);
        }
    }
}
