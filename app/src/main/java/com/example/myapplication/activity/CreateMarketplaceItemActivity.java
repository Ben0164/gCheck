package com.example.myapplication.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.repository.MarketplaceRepository;
import com.example.myapplication.model.User;

import java.util.Calendar;
import java.util.Date;

public class CreateMarketplaceItemActivity extends AppCompatActivity {
    private EditText editTitle;
    private EditText editDescription;
    private EditText editStartingPrice;
    private EditText editQuantity;
    private EditText editLocation;
    private Spinner spinnerCategory;
    private Spinner spinnerUnit;
    private ImageView imageItem;
    private Button buttonSave;
    private Button buttonCancel;
    private Button buttonSelectImage;
    private Button buttonSelectEndDate;
    
    private MarketplaceRepository marketplaceRepository;
    private String currentItemId;
    private Date selectedEndDate;
    private String selectedImageUrl;
    
    private static final int PICK_IMAGE_REQUEST = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_marketplace_item);
        
        initViews();
        setupSpinners();
        setupClickListeners();
        
        marketplaceRepository = MarketplaceRepository.getInstance();
        
        // Set default end date to 7 days from now
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        selectedEndDate = calendar.getTime();
        
        // Check if editing existing item
        handleIntent();
    }
    
    private void initViews() {
        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editStartingPrice = findViewById(R.id.editStartingPrice);
        editQuantity = findViewById(R.id.editQuantity);
        editLocation = findViewById(R.id.editLocation);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        imageItem = findViewById(R.id.imageItem);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonSelectEndDate = findViewById(R.id.buttonSelectEndDate);
    }
    
    private void setupSpinners() {
        String[] categories = {"Crops", "Livestock", "Equipment", "Seeds", "Fertilizer", "Pesticide", "Other"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        
        String[] units = {"kg", "tons", "pieces", "bags", "liters", "boxes", "bunches"};
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);
    }
    
    private void setupClickListeners() {
        buttonSave.setOnClickListener(v -> saveItem());
        buttonCancel.setOnClickListener(v -> finish());
        buttonSelectImage.setOnClickListener(v -> selectImage());
        buttonSelectEndDate.setOnClickListener(v -> showDatePicker());
    }
    
    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            currentItemId = intent.getStringExtra("itemId");
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            double startingPrice = intent.getDoubleExtra("startingPrice", 0);
            String category = intent.getStringExtra("category");
            String location = intent.getStringExtra("location");
            int quantity = intent.getIntExtra("quantity", 0);
            String unit = intent.getStringExtra("unit");
            
            if (title != null) editTitle.setText(title);
            if (description != null) editDescription.setText(description);
            if (startingPrice > 0) editStartingPrice.setText(String.valueOf(startingPrice));
            if (category != null) {
                // Set spinner selection
                for (int i = 0; i < spinnerCategory.getCount(); i++) {
                    if (spinnerCategory.getItemAtPosition(i).toString().equals(category)) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
            }
            if (location != null) editLocation.setText(location);
            if (quantity > 0) editQuantity.setText(String.valueOf(quantity));
            if (unit != null) {
                // Set spinner selection
                for (int i = 0; i < spinnerUnit.getCount(); i++) {
                    if (spinnerUnit.getItemAtPosition(i).toString().equals(unit)) {
                        spinnerUnit.setSelection(i);
                        break;
                    }
                }
            }
        }
    }
    
    private void saveItem() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String startingPriceStr = editStartingPrice.getText().toString().trim();
        String quantityStr = editQuantity.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String unit = spinnerUnit.getSelectedItem().toString();
        
        if (title.isEmpty()) {
            editTitle.setError("Title is required");
            editTitle.requestFocus();
            return;
        }
        
        if (description.isEmpty()) {
            editDescription.setError("Description is required");
            editDescription.requestFocus();
            return;
        }
        
        double startingPrice;
        try {
            startingPrice = Double.parseDouble(startingPriceStr);
            if (startingPrice <= 0) {
                editStartingPrice.setError("Starting price must be greater than 0");
                editStartingPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            editStartingPrice.setError("Valid starting price is required");
            editStartingPrice.requestFocus();
            return;
        }
        
        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                editQuantity.setError("Quantity must be greater than 0");
                editQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            quantity = 1; // Default to 1 if not specified
        }
        
        if (currentItemId != null) {
            // Update existing item
            marketplaceRepository.updateItem(currentItemId, title, description, startingPrice, 
                selectedEndDate, category, location, quantity, unit);
            Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Create new item
            marketplaceRepository.createItem(title, description, startingPrice, 
                selectedEndDate, category, location, quantity, unit);
            Toast.makeText(this, "Item created successfully", Toast.LENGTH_SHORT).show();
        }
        
        finish();
    }
    
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }
    
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedEndDate);
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                selectedEndDate = selectedCalendar.getTime();
                
                // Update button text to show selected date
                buttonSelectEndDate.setText("End Date: " + 
                    java.text.SimpleDateFormat.getDateInstance().format(selectedEndDate));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to tomorrow
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, 1);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        
        datePickerDialog.show();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Handle image selection
            // selectedImageUrl = data.getData().toString();
            // Load image into ImageView
            // Picasso.get().load(selectedImageUrl).into(imageItem);
            
            Toast.makeText(this, "Image selected (implementation needed)", Toast.LENGTH_SHORT).show();
        }
    }
}
