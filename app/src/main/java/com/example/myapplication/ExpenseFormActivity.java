package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.ExpenseEntity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseFormActivity extends AppCompatActivity {

    public static final String EXTRA_EXPENSE_ID = "extra_expense_id";
    public static final String EXTRA_PHASE = "extra_phase";
    public static final String EXTRA_BATCH_ID = "BATCH_ID";

    private AutoCompleteTextView spPhase, spCategory, spProduct, spUnit;
    private TextInputLayout layoutPhase;
    private TextInputEditText etQuantity, etUnitPrice;
    private TextView tvTotalCost;
    private MaterialButton btnSave;
    private AppDatabase db;
    private ExpenseEntity existingExpense;
    private String preSelectedPhase;
    private long currentBatchId = -1;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final String[] PHASES = {
            "Land Preparation", "Crop Establishment", "Crop Management",
            "Reproductive/Ripening Monitoring", "Harvesting", "Post-Harvest"
    };

    private final String[] CATEGORIES = {"Seeds", "Fertilizer", "Pesticide", "Labor", "Equipment", "Others"};

    private final Map<String, String[]> PRODUCT_MAP = new HashMap<String, String[]>() {{
        put("Seeds", new String[]{"NSIC Rc222", "NSIC Rc160", "NSIC Rc216", "NSIC Rc300 series", "Hybrid rice seeds", "Other / Custom"});
        put("Fertilizer", new String[]{"Urea (46-0-0)", "Complete fertilizer (14-14-14)", "Ammonium sulfate (21-0-0)", "Muriate of potash (0-0-60)", "Organic fertilizer", "Other / Custom"});
        put("Pesticide", new String[]{"Insecticides", "Herbicides", "Fungicides", "Other / Custom"});
        put("Labor", new String[]{"Land Prep Labor", "Planting Labor", "Harvesting Labor", "Other / Custom"});
        put("Equipment", new String[]{"Tractor Rental", "Thresher Rental", "Other / Custom"});
        put("Others", new String[]{"Fuel", "Transport", "Other / Custom"});
    }};

    private final String[] UNITS = {"bag", "kg", "liter", "can", "day", "ha", "unit"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_form);

        db = AppDatabase.getInstance(this);
        currentBatchId = getIntent().getLongExtra(EXTRA_BATCH_ID, -1);
        
        if (currentBatchId <= 0) {
            Toast.makeText(this, R.string.error_select_batch, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setupAdapters();
        setupListeners();

        preSelectedPhase = getIntent().getStringExtra(EXTRA_PHASE);
        if (preSelectedPhase != null) {
            spPhase.setText(preSelectedPhase, false);
            layoutPhase.setEnabled(false);
        }

        long expenseId = getIntent().getLongExtra(EXTRA_EXPENSE_ID, -1);
        if (expenseId != -1) {
            loadExistingExpense(expenseId);
        }
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }

        layoutPhase = findViewById(R.id.layout_phase);
        spPhase = findViewById(R.id.sp_phase);
        spCategory = findViewById(R.id.sp_category);
        spProduct = findViewById(R.id.sp_product);
        spUnit = findViewById(R.id.sp_unit);
        etQuantity = findViewById(R.id.et_quantity);
        etUnitPrice = findViewById(R.id.et_unit_price);
        tvTotalCost = findViewById(R.id.tv_total_cost);
        btnSave = findViewById(R.id.btn_save);
    }

    private void setupAdapters() {
        spPhase.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, PHASES));
        spCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, CATEGORIES));
        spUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, UNITS));
    }

    private void setupListeners() {
        spCategory.setOnItemClickListener((parent, view, position, id) -> {
            String category = (String) parent.getItemAtPosition(position);
            updateProductAdapter(category);
        });

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { calculateTotal(); }
            @Override public void afterTextChanged(Editable s) {}
        };

        etQuantity.addTextChangedListener(watcher);
        etUnitPrice.addTextChangedListener(watcher);

        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void updateProductAdapter(String category) {
        String[] products = PRODUCT_MAP.get(category);
        if (products != null) {
            spProduct.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, products));
            spProduct.setText("", false);
        }
    }

    private void calculateTotal() {
        try {
            String qtyStr = etQuantity.getText() != null ? etQuantity.getText().toString() : "";
            String priceStr = etUnitPrice.getText() != null ? etUnitPrice.getText().toString() : "";
            if (!qtyStr.isEmpty() && !priceStr.isEmpty()) {
                double qty = Double.parseDouble(qtyStr);
                double price = Double.parseDouble(priceStr);
                tvTotalCost.setText(String.format(Locale.getDefault(), "₱%.2f", qty * price));
            } else {
                tvTotalCost.setText("₱0.00");
            }
        } catch (Exception e) {
            tvTotalCost.setText("₱0.00");
        }
    }

    private void loadExistingExpense(long id) {
        executor.execute(() -> {
            existingExpense = db.expenseDao().getExpenseById(id);
            if (existingExpense != null) {
                runOnUiThread(() -> {
                    if (isFinishing()) return;
                    spPhase.setText(existingExpense.getPhase(), false);
                    spCategory.setText(existingExpense.getCategory(), false);
                    updateProductAdapter(existingExpense.getCategory());
                    spProduct.setText(existingExpense.getProductName(), false);
                    spUnit.setText(existingExpense.getUnit(), false);
                    etQuantity.setText(String.valueOf(existingExpense.getQuantity()));
                    etUnitPrice.setText(String.valueOf(existingExpense.getUnitPrice()));
                    calculateTotal();
                    btnSave.setText("Update Expense");
                    layoutPhase.setEnabled(false);
                });
            }
        });
    }

    private void saveExpense() {
        String phase = spPhase.getText().toString();
        String category = spCategory.getText().toString();
        String product = spProduct.getText().toString();
        String unit = spUnit.getText().toString();
        String qtyStr = etQuantity.getText() != null ? etQuantity.getText().toString() : "";
        String priceStr = etUnitPrice.getText() != null ? etUnitPrice.getText().toString() : "";

        if (phase.isEmpty() || category.isEmpty() || product.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double qty = Double.parseDouble(qtyStr);
        double price = Double.parseDouble(priceStr);

        btnSave.setEnabled(false);
        executor.execute(() -> {
            try {
                if (existingExpense == null) {
                    ExpenseEntity newExpense = new ExpenseEntity(1, currentBatchId, phase, category, product, qty, unit, price);
                    db.expenseDao().insert(newExpense);
                } else {
                    existingExpense.setPhase(phase);
                    existingExpense.setCategory(category);
                    existingExpense.setProductName(product);
                    existingExpense.setQuantity(qty);
                    existingExpense.setUnit(unit);
                    existingExpense.setUnitPrice(price);
                    existingExpense.setTotalCost(qty * price);
                    db.expenseDao().update(existingExpense);
                }
                runOnUiThread(() -> {
                    if (!isFinishing()) {
                        Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (!isFinishing()) {
                        btnSave.setEnabled(true);
                        Toast.makeText(this, "Error saving expense", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
