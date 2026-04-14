package com.example.myapplication.feature.collaboration.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.myapplication.feature.collaboration.R;
import com.example.myapplication.core.common.SessionManager;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.feature.collaboration.data.RoomCommunityDataSource;
import com.example.myapplication.core.data.entity.PostEntity;
import com.example.myapplication.core.data.entity.UserEntity;
import com.example.myapplication.feature.collaboration.repository.CommunityRepository;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CreatePostActivity - Handles creating new community posts with images, captions, and metadata.
 * Follows clean architecture pattern using CommunityRepository.
 */
public class CreatePostActivity extends AppCompatActivity {
    
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    
    // UI Components
    private ImageView btnBack;
    private ImageView imagePreview;
    private ImageView buttonRemoveImage;
    private LinearLayout layoutUploadPlaceholder;
    private EditText editCaption;
    private TextView textCaptionCount;
    private ChipGroup chipGroupPhase;
    private LinearLayout layoutPublic;
    private LinearLayout layoutAdvice;
    private ImageView ivPublicCheck;
    private ImageView ivAdviceCheck;
    private View buttonPost;
    private View layoutLoading;
    
    // Data
    private CommunityRepository repository;
    private UserEntity currentUser;
    private String selectedImagePath;
    private String selectedPhase = "Land Preparation";
    private String selectedAudience = "public";
    private List<String> farmingPhases;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        
        initializeRepository();
        initializeUI();
        setupListeners();
        loadFarmingPhases();
        updatePostButtonState();
    }
    
    private void initializeRepository() {
        AppDatabase database = AppDatabase.getInstance(this);
        RoomCommunityDataSource dataSource = new RoomCommunityDataSource(
            this,
            database.postDao(),
            database.commentDao(),
            database.likeDao(),
            database.userDao()
        );
        repository = CommunityRepository.getInstance(dataSource);
        
        // Get current user
        currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            // Create a default user for demo purposes
            currentUser = new UserEntity();
            currentUser.setName("Demo Farmer");
            currentUser.setEmail("demo@farm.com");
            currentUser.setRole("farmer");
            repository.createUser(currentUser);
            SessionManager.setCurrentUser(currentUser);
        }
    }
    
    private void initializeUI() {
        // Toolbar
        btnBack = findViewById(R.id.btn_back);
        
        // Image section
        imagePreview = findViewById(R.id.imagePreview);
        buttonRemoveImage = findViewById(R.id.buttonRemoveImage);
        layoutUploadPlaceholder = findViewById(R.id.layout_upload_placeholder);
        
        // Caption section
        editCaption = findViewById(R.id.editCaption);
        textCaptionCount = findViewById(R.id.textCaptionCount);
        
        // Phase selection
        chipGroupPhase = findViewById(R.id.chipGroupPhase);
        
        // Audience selection
        layoutPublic = findViewById(R.id.layout_public);
        layoutAdvice = findViewById(R.id.layout_advice);
        ivPublicCheck = findViewById(R.id.iv_public_check);
        ivAdviceCheck = findViewById(R.id.iv_advice_check);
        
        // Post button
        buttonPost = findViewById(R.id.buttonPost);
        
        // Loading overlay
        layoutLoading = findViewById(R.id.layoutLoading);
        
        // Initialize farming phases
        farmingPhases = new ArrayList<>();
        farmingPhases.add("Land Preparation");
        farmingPhases.add("Planting");
        farmingPhases.add("Growing");
        farmingPhases.add("Harvesting");
        farmingPhases.add("Post-Harvest");
        farmingPhases.add("Storage");
        farmingPhases.add("Marketing");
    }
    
    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());
        
        // Image upload
        layoutUploadPlaceholder.setOnClickListener(v -> showImagePickerDialog());
        
        // Remove image
        buttonRemoveImage.setOnClickListener(v -> removeSelectedImage());
        
        // Caption text watcher
        editCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                updateCaptionCount();
                updatePostButtonState();
            }
        });
        
        // Audience selection
        layoutPublic.setOnClickListener(v -> selectAudience("public"));
        layoutAdvice.setOnClickListener(v -> selectAudience("advice"));
        
        // Post button
        buttonPost.setOnClickListener(v -> createPost());
        
        // Phase selection
        chipGroupPhase.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != View.NO_ID) {
                Chip selectedChip = group.findViewById(checkedId);
                selectedPhase = selectedChip.getText().toString();
                updatePostButtonState();
            }
        });
    }
    
    private void loadFarmingPhases() {
        chipGroupPhase.removeAllViews();
        for (String phase : farmingPhases) {
            Chip chip = new Chip(this);
            chip.setText(phase);
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setChipBackgroundColorResource(android.R.color.transparent);
            chip.setChipStrokeColorResource(android.R.color.holo_green_dark);
            chip.setChipStrokeWidth(1);
            
            // Select first phase by default
            if (phase.equals(selectedPhase)) {
                chip.setChecked(true);
            }
            
            chipGroupPhase.addView(chip);
        }
    }
    
    private void showImagePickerDialog() {
        // Create a simple dialog to choose between camera and gallery
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    dispatchTakePictureIntent();
                    break;
                case 1:
                    dispatchPickPictureIntent();
                    break;
            }
        });
        builder.show();
    }
    
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    
    private void dispatchPickPictureIntent() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null) {
                    // For demo purposes, we'll use a placeholder path
                    // In a real app, you'd save the bitmap to a file
                    selectedImagePath = "captured_image_" + System.currentTimeMillis() + ".jpg";
                    displaySelectedImage(imageBitmap);
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        selectedImagePath = selectedImage.toString();
                        displaySelectedImage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    
    private void displaySelectedImage(Bitmap bitmap) {
        imagePreview.setImageBitmap(bitmap);
        imagePreview.setVisibility(View.VISIBLE);
        buttonRemoveImage.setVisibility(View.VISIBLE);
        layoutUploadPlaceholder.setVisibility(View.GONE);
        updatePostButtonState();
    }
    
    private void removeSelectedImage() {
        selectedImagePath = null;
        imagePreview.setVisibility(View.GONE);
        buttonRemoveImage.setVisibility(View.GONE);
        layoutUploadPlaceholder.setVisibility(View.VISIBLE);
        updatePostButtonState();
    }
    
    private void selectAudience(String audience) {
        selectedAudience = audience;
        
        if ("public".equals(audience)) {
            ivPublicCheck.setImageResource(R.drawable.ic_radio_selected);
            ivAdviceCheck.setImageResource(R.drawable.ic_radio_unselected);
        } else {
            ivPublicCheck.setImageResource(R.drawable.ic_radio_unselected);
            ivAdviceCheck.setImageResource(R.drawable.ic_radio_selected);
        }
        
        updatePostButtonState();
    }
    
    private void updateCaptionCount() {
        String caption = editCaption.getText().toString();
        int count = caption.length();
        textCaptionCount.setText(count + "/1000");
        
        // Change color when approaching limit
        if (count > 900) {
            textCaptionCount.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            textCaptionCount.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }
    
    private void updatePostButtonState() {
        boolean isValid = validatePostInput();
        buttonPost.setEnabled(isValid);
    }
    
    private boolean validatePostInput() {
        String caption = editCaption.getText().toString().trim();
        
        // Check if at least caption or image is provided
        boolean hasContent = !caption.isEmpty() || selectedImagePath != null;
        
        if (!hasContent) {
            return false;
        }
        
        // Validate caption if provided
        if (!caption.isEmpty() && caption.length() > 1000) {
            return false;
        }
        
        // Validate image if provided
        if (selectedImagePath != null && !repository.isValidImageFile(selectedImagePath)) {
            return false;
        }
        
        return true;
    }
    
    private void createPost() {
        String caption = editCaption.getText().toString().trim();
        
        // Validate using repository
        CommunityRepository.PostValidationResult validation = repository.validatePostForCreation(
            caption.isEmpty() ? null : caption,
            selectedImagePath,
            selectedAudience
        );
        
        if (!validation.isValid) {
            Toast.makeText(this, validation.errorMessage, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading
        layoutLoading.setVisibility(View.VISIBLE);
        
        // Create post entity
        PostEntity post = new PostEntity();
        post.setAuthorId(currentUser.getId());
        post.setCaption(caption.isEmpty() ? null : caption);
        post.setImagePath(selectedImagePath);
        post.setPhase(selectedPhase);
        post.setAudience(selectedAudience);
        post.setVerified(false); // Could be set based on CNN verification
        
        // Create post in background thread
        new Thread(() -> {
            try {
                long postId = repository.createPost(post);
                
                runOnUiThread(() -> {
                    layoutLoading.setVisibility(View.GONE);
                    
                    if (postId != -1) {
                        Toast.makeText(this, "Post created successfully!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to create post", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    layoutLoading.setVisibility(View.GONE);
                    Toast.makeText(this, "Error creating post", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    @Override
    public void onBackPressed() {
        // Check if there are unsaved changes
        String caption = editCaption.getText().toString().trim();
        boolean hasChanges = !caption.isEmpty() || selectedImagePath != null;
        
        if (hasChanges) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("Discard Changes?");
            builder.setMessage("You have unsaved changes. Are you sure you want to discard them?");
            builder.setPositiveButton("Discard", (dialog, which) -> super.onBackPressed());
            builder.setNegativeButton("Cancel", null);
            builder.show();
        } else {
            super.onBackPressed();
        }
    }
}
