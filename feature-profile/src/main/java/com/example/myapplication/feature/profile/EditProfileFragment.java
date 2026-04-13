package com.example.myapplication.feature.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.UserEntity;
import com.example.myapplication.palay.data.repository.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.concurrent.Executors;

public class EditProfileFragment extends Fragment {

    private TextInputLayout tilName, tilUsername, tilLocation;
    private TextInputEditText etName, etUsername, etPronouns, etBio, etLocation, etPhone, etSpecialty;
    private AutoCompleteTextView actGender;
    private AppDatabase db;
    private UserEntity currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        db = AppDatabase.getInstance(requireContext());
        currentUser = SessionManager.getCurrentUser();

        initViews(view);
        populateFields();

        return view;
    }

    private void initViews(View view) {
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_edit_profile);
        toolbar.setNavigationOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        tilName = view.findViewById(R.id.til_edit_name);
        tilUsername = view.findViewById(R.id.til_edit_username);
        tilLocation = view.findViewById(R.id.til_edit_location);

        etName = view.findViewById(R.id.et_edit_name);
        etUsername = view.findViewById(R.id.et_edit_username);
        etPronouns = view.findViewById(R.id.et_edit_pronouns);
        etBio = view.findViewById(R.id.et_edit_bio);
        etLocation = view.findViewById(R.id.et_edit_location);
        etPhone = view.findViewById(R.id.et_edit_phone);
        etSpecialty = view.findViewById(R.id.et_edit_specialty);
        actGender = view.findViewById(R.id.act_edit_gender);

        String[] genders = {"Male", "Female", "Prefer not to say", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, genders);
        actGender.setAdapter(adapter);

        MaterialButton btnSave = view.findViewById(R.id.btn_save_profile);
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void populateFields() {
        if (currentUser != null) {
            etName.setText(currentUser.getName());
            etUsername.setText(currentUser.getUsername());
            etPronouns.setText(currentUser.getPronouns());
            etBio.setText(currentUser.getBio());
            etLocation.setText(currentUser.getLocation());
            etPhone.setText(currentUser.getPhone());
            etSpecialty.setText(currentUser.getSpecialty());
            actGender.setText(currentUser.getGender(), false);
        }
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            tilName.setError("Name is required");
            isValid = false;
        } else tilName.setError(null);

        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Username is required");
            isValid = false;
        } else tilUsername.setError(null);

        if (TextUtils.isEmpty(location)) {
            tilLocation.setError("Location is required");
            isValid = false;
        } else tilLocation.setError(null);

        if (!isValid) return;

        currentUser.setName(name);
        currentUser.setUsername(username);
        currentUser.setPronouns(etPronouns.getText().toString().trim());
        currentUser.setBio(etBio.getText().toString().trim());
        currentUser.setLocation(location);
        currentUser.setPhone(etPhone.getText().toString().trim());
        currentUser.setSpecialty(etSpecialty.getText().toString().trim());
        currentUser.setGender(actGender.getText().toString());

        Executors.newSingleThreadExecutor().execute(() -> {
            db.userDao().update(currentUser);
            requireActivity().runOnUiThread(() -> {
                SessionManager.setCurrentUser(currentUser);
                Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            });
        });
    }
}
