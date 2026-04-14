package com.example.myapplication.feature.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.feature.profile.R;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.UserEntity;
import com.example.myapplication.core.common.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.concurrent.Executors;

public class EditProfileFragment extends Fragment {

    private TextInputEditText etName, etUsername, etBio, etLocation, etPhone, etPronouns, etSpecialty;
    private AutoCompleteTextView actGender;
    private CircleImageView ivProfileImage;
    private TextView btnChooseImage;
    private AppDatabase db;
    private UserEntity currentUser;
    private Uri selectedImageUri;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 100;

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

        etName = view.findViewById(R.id.et_edit_name);
        etUsername = view.findViewById(R.id.et_edit_username);
        etPronouns = view.findViewById(R.id.et_edit_pronouns);
        etBio = view.findViewById(R.id.et_edit_bio);
        etLocation = view.findViewById(R.id.et_edit_location);
        etPhone = view.findViewById(R.id.et_edit_phone);
        etSpecialty = view.findViewById(R.id.et_edit_specialty);
        actGender = view.findViewById(R.id.act_edit_gender);

        ivProfileImage = view.findViewById(R.id.iv_edit_profile_image);
        btnChooseImage = view.findViewById(R.id.btn_choose_image);

        String[] genders = {"Male", "Female", "Prefer not to say", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, genders);
        actGender.setAdapter(adapter);

        MaterialButton btnSave = view.findViewById(R.id.btn_save_profile);
        btnSave.setOnClickListener(v -> saveProfile());

        btnChooseImage.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        // Check for runtime permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(getContext(), "Permission denied to read external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                ivProfileImage.setImageURI(selectedImageUri);
            }
        }
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

            // Load existing profile image if available
            if (currentUser.getProfileImageUrl() != null) {
                try {
                    ivProfileImage.setImageURI(Uri.parse(currentUser.getProfileImageUrl()));
                    selectedImageUri = Uri.parse(currentUser.getProfileImageUrl());
                } catch (Exception e) {
                    // Keep default image if loading fails
                }
            }
        }
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            isValid = false;
        } else etName.setError(null);

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username is required");
            isValid = false;
        } else etUsername.setError(null);

        if (TextUtils.isEmpty(location)) {
            etLocation.setError("Location is required");
            isValid = false;
        } else etLocation.setError(null);

        if (!isValid) return;

        currentUser.setName(name);
        currentUser.setUsername(username);
        currentUser.setPronouns(etPronouns.getText().toString().trim());
        currentUser.setBio(etBio.getText().toString().trim());
        currentUser.setLocation(location);
        currentUser.setPhone(etPhone.getText().toString().trim());
        currentUser.setSpecialty(etSpecialty.getText().toString().trim());
        currentUser.setGender(actGender.getText().toString());

        // Save profile image URI if selected
        if (selectedImageUri != null) {
            currentUser.setProfileImageUrl(selectedImageUri.toString());
        }

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
