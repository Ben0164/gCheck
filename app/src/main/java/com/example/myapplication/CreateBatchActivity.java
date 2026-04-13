package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.BatchEntity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateBatchActivity extends AppCompatActivity {

    private TextInputLayout tilBatchName, tilStartDate, tilHarvestDate;
    private TextInputEditText etBatchName, etStartDate, etHarvestDate;
    private MaterialButton btnSaveBatch;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar harvestCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_batch);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tilBatchName = findViewById(R.id.til_batch_name);
        tilStartDate = findViewById(R.id.til_start_date);
        tilHarvestDate = findViewById(R.id.til_harvest_date);

        etBatchName = findViewById(R.id.et_batch_name);
        etStartDate = findViewById(R.id.et_start_date);
        etHarvestDate = findViewById(R.id.et_harvest_date);
        btnSaveBatch = findViewById(R.id.btn_save_batch);

        // Default: Harvest is 120 days after start
        harvestCalendar.add(Calendar.DAY_OF_YEAR, 120);
        updateDateLabels();

        etStartDate.setOnClickListener(v -> showDatePicker(true));
        etHarvestDate.setOnClickListener(v -> showDatePicker(false));

        btnSaveBatch.setOnClickListener(v -> saveBatch());
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar cal = isStartDate ? startCalendar : harvestCalendar;
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            
            if (isStartDate) {
                // Auto-update harvest date to +120 days when start date changes
                harvestCalendar.setTimeInMillis(startCalendar.getTimeInMillis());
                harvestCalendar.add(Calendar.DAY_OF_YEAR, 120);
            }
            updateDateLabels();
            
            // Clear errors when user picks a date
            tilStartDate.setError(null);
            tilHarvestDate.setError(null);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateLabels() {
        etStartDate.setText(dateFormat.format(startCalendar.getTime()));
        etHarvestDate.setText(dateFormat.format(harvestCalendar.getTime()));
    }

    private void saveBatch() {
        // Reset errors
        tilBatchName.setError(null);
        tilStartDate.setError(null);
        tilHarvestDate.setError(null);

        String name = etBatchName.getText().toString().trim();
        if (name.isEmpty()) {
            tilBatchName.setError(getString(R.string.error_field_required));
            return;
        }

        long startMillis = startCalendar.getTimeInMillis();
        long harvestMillis = harvestCalendar.getTimeInMillis();

        // VALIDATION: Harvest date must be after start date
        if (harvestMillis <= startMillis) {
            tilHarvestDate.setError("Harvest date must be after start date");
            return;
        }

        // PREVENT DOUBLE SUBMISSION
        btnSaveBatch.setEnabled(false);

        executor.execute(() -> {
            try {
                // userId is hardcoded to 1 for now as per current project pattern
                BatchEntity batch = new BatchEntity(1, name, startMillis);
                batch.setExpectedHarvestDate(harvestMillis);
                
                AppDatabase.getInstance(this).batchDao().insert(batch);
                
                runOnUiThread(() -> {
                    if (!isFinishing()) {
                        Toast.makeText(this, "Batch created successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (!isFinishing()) {
                        btnSaveBatch.setEnabled(true);
                        Toast.makeText(this, "Error saving batch", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown(); // Cleanup
    }
}
