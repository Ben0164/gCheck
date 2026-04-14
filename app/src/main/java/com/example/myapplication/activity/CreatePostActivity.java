package com.example.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.repository.PostRepository;
import com.example.myapplication.model.User;

public class CreatePostActivity extends AppCompatActivity {
    private EditText editTitle;
    private EditText editContent;
    private Spinner spinnerCategory;
    private ImageView imagePost;
    private Button buttonSave;
    private Button buttonCancel;
    private Button buttonSelectImage;
    
    private PostRepository postRepository;
    private String currentPostId;
    private String selectedImageUrl;
    
    private static final int PICK_IMAGE_REQUEST = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        
        initViews();
        setupSpinner();
        setupClickListeners();
        
        postRepository = PostRepository.getInstance();
        
        // Check if editing existing post
        handleIntent();
    }
    
    private void initViews() {
        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editCaption);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        imagePost = findViewById(R.id.imagePost);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
    }
    
    private void setupSpinner() {
        String[] categories = {"General", "Farming Tips", "Market Updates", "Equipment", "Crops", "Livestock", "Weather"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }
    
    private void setupClickListeners() {
        buttonSave.setOnClickListener(v -> savePost());
        buttonCancel.setOnClickListener(v -> finish());
        buttonSelectImage.setOnClickListener(v -> selectImage());
    }
    
    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            currentPostId = intent.getStringExtra("postId");
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            String category = intent.getStringExtra("category");
            String imageUrl = intent.getStringExtra("imageUrl");
            
            if (title != null) editTitle.setText(title);
            if (content != null) editContent.setText(content);
            if (category != null) {
                // Set spinner selection
                for (int i = 0; i < spinnerCategory.getCount(); i++) {
                    if (spinnerCategory.getItemAtPosition(i).toString().equals(category)) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
            }
            if (imageUrl != null) {
                selectedImageUrl = imageUrl;
                // Load image using Picasso or similar
                // Picasso.get().load(imageUrl).into(imagePost);
            }
        }
    }
    
    private void savePost() {
        String title = editTitle.getText().toString().trim();
        String content = editContent.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        
        if (title.isEmpty()) {
            editTitle.setError("Title is required");
            editTitle.requestFocus();
            return;
        }
        
        if (content.isEmpty()) {
            editContent.setError("Content is required");
            editContent.requestFocus();
            return;
        }
        
        if (currentPostId != null) {
            // Update existing post
            postRepository.updatePost(currentPostId, title, content, category, selectedImageUrl);
            Toast.makeText(this, "Post updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Create new post
            postRepository.createPost(title, content, category, selectedImageUrl);
            Toast.makeText(this, "Post created successfully", Toast.LENGTH_SHORT).show();
        }
        
        finish();
    }
    
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Handle image selection
            // selectedImageUrl = data.getData().toString();
            // Load image into ImageView
            // Picasso.get().load(selectedImageUrl).into(imagePost);
            
            Toast.makeText(this, "Image selected (implementation needed)", Toast.LENGTH_SHORT).show();
        }
    }
}
