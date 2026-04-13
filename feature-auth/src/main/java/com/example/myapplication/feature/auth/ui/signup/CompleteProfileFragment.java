package com.example.myapplication.feature.auth.ui.signup;

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
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.UserEntity;
import com.example.myapplication.palay.data.repository.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.concurrent.Executors;

public class CompleteProfileFragment extends Fragment {

    private TextInputLayout tilName, tilLocation;
    private TextInputEditText etName, etLocation, etUsername, etSpecialty, etPhone, etBio;
    private MaterialButton btnSave;
    private AppDatabase db;
    private UserEntity currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complete_profile, container, false);

        db = AppDatabase.getInstance(requireContext());
        currentUser = SessionManager.getCurrentUser();

        initViews(view);

        return view;
    }

    private void initViews(View view) {
        tilName = view.findViewById(R.id.til_complete_name);
        tilLocation = view.findViewById(R.id.til_complete_location);
        
        etName = view.findViewById(R.id.et_complete_name);
        etLocation = view.findViewById(R.id.et_complete_location);
        etUsername = view.findViewById(R.id.et_complete_username);
        etSpecialty = view.findViewById(R.id.et_complete_specialty);
        etPhone = view.findViewById(R.id.et_complete_phone);
        etBio = view.findViewById(R.id.et_complete_bio);
        
        btnSave = view.findViewById(R.id.btn_complete_save);
        btnSave.setOnClickListener(v -> handleSave());
    }

    private void handleSave() {
        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (!validate(name, location)) return;

        if (currentUser != null) {
            currentUser.setName(name);
            currentUser.setLocation(location);
            currentUser.setUsername(etUsername.getText().toString().trim());
            currentUser.setSpecialty(etSpecialty.getText().toString().trim());
            currentUser.setPhone(etPhone.getText().toString().trim());
            currentUser.setBio(etBio.getText().toString().trim());

            Executors.newSingleThreadExecutor().execute(() -> {
                db.userDao().update(currentUser);
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        SessionManager.setCurrentUser(currentUser);
                        Toast.makeText(requireContext(), "Profile completed!", Toast.LENGTH_SHORT).show();
                        // Navigation logic will be handled by the Activity
                    });
                }
            });
        }
    }

    private boolean validate(String name, String location) {
        boolean isValid = true;
        if (TextUtils.isEmpty(name)) {
            tilName.setError("Full Name is required");
            isValid = false;
        } else tilName.setError(null);

        if (TextUtils.isEmpty(location)) {
            tilLocation.setError("Location is required");
            isValid = false;
        } else tilLocation.setError(null);

        return isValid;
    }
}
