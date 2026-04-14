package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.feature.expense.ExpenseAdapter;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.ExpenseEntity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseLedgerActivity extends AppCompatActivity {

    private AutoCompleteTextView spActivity, spMethod, spExpenseType, spUnit, spLaborType, spInternalHauling;
    private TextInputEditText etProduct, etQuantity, etPrice, etOtherMethod, etDailyWage, etDays;
    private TextInputLayout tilOtherMethod, tilProduct, tilLaborType, tilInternalHauling;
    private View layoutStandardFields, layoutImplicitFields;
    private MaterialButton btnSave, btnDelete;
    private TextView tvTotalLedger, tvFormTotal, tvRecentEntriesTitle, tvCostLabel;
    private RecyclerView rvExpenses;
    private ExpenseAdapter adapter;
    private AppDatabase db;
    private ExpenseEntity editingExpense = null;

    private String currentPhase = "Land Preparation";
    private long currentBatchId = -1;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final Map<String, Map<String, Map<String, List<String>>>> MASTER_MAP = new HashMap<>();

    static {
        // --- LAND PREPARATION ---
        Map<String, Map<String, List<String>>> landPrep = new HashMap<>();
        
        Map<String, List<String>> clearing = new HashMap<>();
        clearing.put("Manual", Arrays.asList("Labor"));
        clearing.put("Mechanical", Arrays.asList("Equipment Rental", "Fuel", "Labor"));
        clearing.put("Chemical", Arrays.asList("Herbicide", "Sprayer", "Labor"));
        landPrep.put("Field Clearing", clearing);

        Map<String, List<String>> plowing = new HashMap<>();
        plowing.put("Tractor", Arrays.asList("Tractor Rental", "Fuel", "Labor"));
        plowing.put("Hand Tractor", Arrays.asList("Rental", "Fuel", "Labor"));
        plowing.put("Carabao", Arrays.asList("Animal Rental", "Feed", "Labor"));
        landPrep.put("Plowing", plowing);

        Map<String, List<String>> harrowing = new HashMap<>();
        harrowing.put("Harrow", Arrays.asList("Equipment Rental", "Fuel", "Labor"));
        harrowing.put("Rotavator", Arrays.asList("Rental", "Fuel", "Labor"));
        landPrep.put("Harrowing", harrowing);

        Map<String, List<String>> leveling = new HashMap<>();
        leveling.put("Manual", Arrays.asList("Labor"));
        leveling.put("Mechanical", Arrays.asList("Equipment Rental", "Fuel", "Labor"));
        landPrep.put("Leveling", leveling);

        Map<String, List<String>> puddling = new HashMap<>();
        puddling.put("Irrigation", Arrays.asList("Irrigation Fee", "Labor"));
        puddling.put("Water Pump", Arrays.asList("Pump Rental", "Fuel", "Labor"));
        landPrep.put("Puddling", puddling);

        Map<String, List<String>> dike = new HashMap<>();
        dike.put("Manual", Arrays.asList("Labor"));
        dike.put("With Materials", Arrays.asList("Materials", "Tools", "Labor"));
        landPrep.put("Dike Repair", dike);

        MASTER_MAP.put("Land Preparation", landPrep);

        // --- PLANTING (Crop Establishment) ---
        Map<String, Map<String, List<String>>> planting = new HashMap<>();
        planting.put("Seed Selection", createSimpleMap(Arrays.asList("Seeds Purchase", "Transport")));
        planting.put("Seed Soaking", new HashMap<String, List<String>>() {{
            put("Water", Arrays.asList("Labor"));
            put("Chemical", Arrays.asList("Chemicals", "Labor"));
            put("Salt Method", Arrays.asList("Salt", "Labor"));
        }});
        planting.put("Nursery Preparation", createSimpleMap(Arrays.asList("Seeds", "Soil Media", "Labor", "Water")));
        planting.put("Sowing", new HashMap<String, List<String>>() {{
            put("Manual", Arrays.asList("Labor"));
            put("Machine", Arrays.asList("Machine Rental", "Fuel", "Labor"));
        }});
        planting.put("Transplanting", new HashMap<String, List<String>>() {{
            put("Manual", Arrays.asList("Labor", "Food Allowance"));
            put("Mechanical", Arrays.asList("Transplanter Rental", "Fuel", "Labor"));
        }});
        planting.put("Direct Seeding", createSimpleMap(Arrays.asList("Seeds", "Labor", "Equipment Rental")));
        MASTER_MAP.put("Crop Establishment", planting);

        // --- CROP MANAGEMENT ---
        Map<String, Map<String, List<String>>> mgmt = new HashMap<>();
        mgmt.put("Fertilizer Application", new HashMap<String, List<String>>() {{
            put("Manual", Arrays.asList("Fertilizer", "Labor"));
            put("Machine", Arrays.asList("Equipment Rental", "Fuel", "Labor"));
            put("Foliar", Arrays.asList("Foliar Fertilizer", "Sprayer", "Labor"));
        }});
        mgmt.put("Irrigation", createSimpleMap(Arrays.asList("Water Fee", "Fuel", "Labor")));
        mgmt.put("Weed Control", new HashMap<String, List<String>>() {{
            put("Manual", Arrays.asList("Labor"));
            put("Mechanical", Arrays.asList("Equipment Rental", "Fuel", "Labor"));
            put("Chemical", Arrays.asList("Herbicide", "Sprayer", "Labor"));
        }});
        mgmt.put("Pest Control", new HashMap<String, List<String>>() {{
            put("Chemical", Arrays.asList("Insecticide", "Sprayer", "Labor"));
            put("Biological", Arrays.asList("Bio Agents", "Labor"));
            put("IPM", Arrays.asList("Materials", "Labor"));
        }});
        mgmt.put("Disease Control", createSimpleMap(Arrays.asList("Fungicide", "Sprayer", "Labor")));
        mgmt.put("Monitoring", createSimpleMap(Arrays.asList("Labor")));
        MASTER_MAP.put("Crop Management", mgmt);

        // --- REPRODUCTIVE / RIPENING ---
        Map<String, Map<String, List<String>>> repro = new HashMap<>();
        repro.put("Panicle Monitoring", createSimpleMap(Arrays.asList("Labor")));
        repro.put("Water Adjustment", createSimpleMap(Arrays.asList("Labor")));
        repro.put("Final Fertilization", createSimpleMap(Arrays.asList("Fertilizer", "Labor")));
        repro.put("Pest Monitoring", createSimpleMap(Arrays.asList("Chemicals", "Labor")));
        MASTER_MAP.put("Reproductive / Ripening Monitoring", repro);

        // --- HARVESTING ---
        Map<String, Map<String, List<String>>> harvest = new HashMap<>();
        harvest.put("Harvesting", new HashMap<String, List<String>>() {{
            put("Manual", Arrays.asList("Labor", "Food Allowance"));
            put("Combine", Arrays.asList("Machine Rental", "Fuel", "Operator Fee"));
        }});
        harvest.put("Cutting", createSimpleMap(Arrays.asList("Labor")));
        harvest.put("Threshing", new HashMap<String, List<String>>() {{
            put("Manual", Arrays.asList("Labor"));
            put("Mechanical", Arrays.asList("Thresher Rental", "Fuel", "Labor"));
        }});
        MASTER_MAP.put("Harvesting", harvest);

        // --- POST-HARVEST ---
        Map<String, Map<String, List<String>>> post = new HashMap<>();
        post.put("Drying", new HashMap<String, List<String>>() {{
            put("Sun", Arrays.asList("Labor"));
            put("Mechanical", Arrays.asList("Dryer Rental", "Fuel", "Labor"));
        }});
        post.put("Cleaning", createSimpleMap(Arrays.asList("Labor")));
        post.put("Milling", createSimpleMap(Arrays.asList("Milling Fee", "Transport")));
        post.put("Storage", createSimpleMap(Arrays.asList("Sack", "Warehouse Fee", "Pest Control")));
        post.put("Transport", createSimpleMap(Arrays.asList("Fuel", "Labor", "Hauling Fee")));
        MASTER_MAP.put("Post-Harvest", post);
    }

    private static Map<String, List<String>> createSimpleMap(List<String> items) {
        Map<String, List<String>> map = new HashMap<>();
        map.put("Default", items);
        map.put("Other", items);
        return map;
    }

    private final String[] UNITS = {"kg", "liter", "hectare", "hour", "day", "laborer", "unit", "bag", "sack"};
    private final String[] LABOR_TYPES = {"Paid Labor", "Owner Labor (unpaid)"};
    private final String[] HAULING_TYPES = {"Standard Expense", "Internal Hauling (Farm to Storage)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_ledger);

        String phaseFromIntent = getIntent().getStringExtra("PHASE");
        if (phaseFromIntent != null) currentPhase = phaseFromIntent;
        
        currentBatchId = getIntent().getLongExtra("BATCH_ID", -1);
        if (currentBatchId <= 0) {
            Toast.makeText(this, "Select a valid batch to see accurate profit", Toast.LENGTH_LONG).show();
        }

        db = AppDatabase.getInstance(this);
        initViews();
        setupAdapters();
        loadExpenses();

        long editId = getIntent().getLongExtra("EXPENSE_ID", -1);
        if (editId != -1) {
            executor.execute(() -> {
                ExpenseEntity expense = db.expenseDao().getExpenseById(editId);
                if (expense != null) {
                    runOnUiThread(() -> {
                        if (!isFinishing()) editExpense(expense);
                    });
                }
            });
        }
    }

    private void initViews() {
        try {
            MaterialToolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setTitle(currentPhase + " Ledger");
                    toolbar.setNavigationOnClickListener(v -> finish());
                }
            }

            spActivity = findViewById(R.id.sp_activity);
            spMethod = findViewById(R.id.sp_method);
            spExpenseType = findViewById(R.id.sp_expense_type);
            spLaborType = findViewById(R.id.sp_labor_type);
            spInternalHauling = findViewById(R.id.sp_internal_hauling); 
            spUnit = findViewById(R.id.sp_unit);
            
            etProduct = findViewById(R.id.et_product);
            etQuantity = findViewById(R.id.et_quantity);
            etPrice = findViewById(R.id.et_price);
            etDailyWage = findViewById(R.id.et_daily_wage);
            etDays = findViewById(R.id.et_days);
            etOtherMethod = findViewById(R.id.et_other_method);
            
            tilOtherMethod = findViewById(R.id.til_other_method);
            tilProduct = findViewById(R.id.til_product);
            tilLaborType = findViewById(R.id.til_labor_type);
            tilInternalHauling = findViewById(R.id.til_internal_hauling); 
            
            layoutStandardFields = findViewById(R.id.layout_standard_fields);
            layoutImplicitFields = findViewById(R.id.layout_implicit_fields);

            btnSave = findViewById(R.id.btn_save);
            btnDelete = findViewById(R.id.btn_delete);
            tvTotalLedger = findViewById(R.id.tv_total_ledger);
            tvFormTotal = findViewById(R.id.tv_form_total);
            tvRecentEntriesTitle = findViewById(R.id.tv_recent_entries_title);
            tvCostLabel = findViewById(R.id.tv_cost_label);
            rvExpenses = findViewById(R.id.rv_expenses);

            if (tvRecentEntriesTitle != null) tvRecentEntriesTitle.setText("Recent " + currentPhase + " Entries");

            if (rvExpenses != null) {
                rvExpenses.setLayoutManager(new LinearLayoutManager(this));
                adapter = new ExpenseAdapter(new ArrayList<>());
                adapter.setOnExpenseClickListener(this::editExpense);
                rvExpenses.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing views", Toast.LENGTH_SHORT).show();
        }

        if (spActivity != null) {
            spActivity.setOnItemClickListener((parent, view, position, id) -> updateMethodAdapter((String) parent.getItemAtPosition(position)));
        }

        if (spMethod != null) {
            spMethod.setOnItemClickListener((parent, view, position, id) -> {
                String selectedMethod = (String) parent.getItemAtPosition(position);
                if (tilOtherMethod != null) {
                    tilOtherMethod.setVisibility("Other".equals(selectedMethod) ? View.VISIBLE : View.GONE);
                }
                updateExpenseTypeAdapter(spActivity.getText().toString(), selectedMethod);
            });
        }

        if (spExpenseType != null) {
            spExpenseType.setOnItemClickListener((parent, view, position, id) -> {
                String selectedType = (String) parent.getItemAtPosition(position);
                boolean isHauling = selectedType.toLowerCase().contains("transport") || selectedType.toLowerCase().contains("hauling");
                if (tilInternalHauling != null) {
                    tilInternalHauling.setVisibility(isHauling ? View.VISIBLE : View.GONE);
                }
                if (isHauling) {
                    Toast.makeText(this, "Use Internal Transport for farm logistics only. Buyer transport is calculated automatically.", Toast.LENGTH_SHORT).show();
                }

                boolean isLabor = selectedType.toLowerCase().contains("labor");
                if (tilLaborType != null) {
                    tilLaborType.setVisibility(isLabor ? View.VISIBLE : View.GONE);
                }
                
                boolean isOther = "Other".equals(selectedType);
                if (tilProduct != null) {
                    tilProduct.setVisibility(isOther ? View.VISIBLE : View.GONE);
                    if (!isOther && etProduct != null) etProduct.setText(selectedType); 
                    else if (etProduct != null) etProduct.setText("");
                }
                
                if (!isLabor) resetLaborView();
            });
        }

        if (spLaborType != null) {
            spLaborType.setOnItemClickListener((parent, view, position, id) -> {
                boolean isOwner = position == 1; // Owner Labor
                if (layoutStandardFields != null) layoutStandardFields.setVisibility(isOwner ? View.GONE : View.VISIBLE);
                if (layoutImplicitFields != null) layoutImplicitFields.setVisibility(isOwner ? View.VISIBLE : View.GONE);
                if (tvCostLabel != null) tvCostLabel.setText(isOwner ? "Est. Labor Cost" : "Actual Cost");
                calculateTotal();
            });
        }

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { calculateTotal(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        if (etQuantity != null) etQuantity.addTextChangedListener(watcher);
        if (etPrice != null) etPrice.addTextChangedListener(watcher);
        if (etDailyWage != null) etDailyWage.addTextChangedListener(watcher);
        if (etDays != null) etDays.addTextChangedListener(watcher);

        if (btnSave != null) btnSave.setOnClickListener(v -> saveExpense());
        if (btnDelete != null) btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void resetLaborView() {
        if (tilLaborType != null) tilLaborType.setVisibility(View.GONE);
        if (layoutStandardFields != null) layoutStandardFields.setVisibility(View.VISIBLE);
        if (layoutImplicitFields != null) layoutImplicitFields.setVisibility(View.GONE);
        if (tvCostLabel != null) tvCostLabel.setText("Actual Cost");
        if (spLaborType != null) spLaborType.setText("", false);
    }

    private void setupAdapters() {
        Map<String, Map<String, List<String>>> activitiesMap = MASTER_MAP.get(currentPhase);
        String[] activities = (activitiesMap != null) ? activitiesMap.keySet().toArray(new String[0]) : new String[]{};
        if (spActivity != null) spActivity.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, activities));
        if (spUnit != null) spUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, UNITS));
        if (spLaborType != null) spLaborType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, LABOR_TYPES));
        if (spInternalHauling != null) spInternalHauling.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, HAULING_TYPES));
    }

    private void updateMethodAdapter(String activity) {
        Map<String, Map<String, List<String>>> activitiesMap = MASTER_MAP.get(currentPhase);
        if (activitiesMap != null) {
            Map<String, List<String>> methodsMap = activitiesMap.get(activity);
            if (methodsMap != null && spMethod != null) {
                List<String> methods = new ArrayList<>(methodsMap.keySet());
                if (!methods.contains("Other")) methods.add("Other");
                spMethod.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, methods));
            }
        }
        if (spMethod != null) spMethod.setText("", false);
        if (spExpenseType != null) spExpenseType.setText("", false);
        resetLaborView();
    }

    private void updateExpenseTypeAdapter(String activity, String method) {
        List<String> types = new ArrayList<>();
        Map<String, Map<String, List<String>>> activitiesMap = MASTER_MAP.get(currentPhase);
        if (activitiesMap != null) {
            Map<String, List<String>> methodsMap = activitiesMap.get(activity);
            if (methodsMap != null) {
                List<String> list = methodsMap.get(method);
                if (list != null) types.addAll(list);
            }
        }
        if (!types.contains("Other")) types.add("Other");
        if (spExpenseType != null) {
            spExpenseType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, types));
            spExpenseType.setText("", false);
        }
        resetLaborView();
    }

    private void calculateTotal() {
        try {
            double total = 0;
            if (layoutImplicitFields != null && layoutImplicitFields.getVisibility() == View.VISIBLE) {
                String wageS = etDailyWage != null ? etDailyWage.getText().toString() : "";
                String daysS = etDays != null ? etDays.getText().toString() : "";
                if (!wageS.isEmpty() && !daysS.isEmpty()) {
                    total = Double.parseDouble(wageS) * Double.parseDouble(daysS);
                }
            } else {
                String qS = etQuantity != null ? etQuantity.getText().toString() : "";
                String pS = etPrice != null ? etPrice.getText().toString() : "";
                if (!qS.isEmpty() && !pS.isEmpty()) {
                    total = Double.parseDouble(qS) * Double.parseDouble(pS);
                }
            }
            if (tvFormTotal != null) tvFormTotal.setText(String.format(Locale.getDefault(), "₱%.2f", total));
        } catch (Exception e) {
            if (tvFormTotal != null) tvFormTotal.setText("₱0.00");
        }
    }

    private void loadExpenses() {
        db.expenseDao().getExpensesByBatch(currentBatchId).observe(this, expenses -> {
            if (expenses != null && adapter != null) {
                adapter.setExpenses(expenses);
                double total = 0;
                for (ExpenseEntity e : expenses) {
                    total += (e.getLaborType() != null && e.getLaborType().equals("owner")) ? e.getImplicitCost() : e.getTotalCost();
                }
                if (tvTotalLedger != null) tvTotalLedger.setText(String.format(Locale.getDefault(), "₱%.2f", total));
            }
        });
    }

    private void editExpense(ExpenseEntity expense) {
        editingExpense = expense;
        currentPhase = expense.getPhase();
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Edit Expense");
        if (tvRecentEntriesTitle != null) tvRecentEntriesTitle.setText("Recent " + currentPhase + " Entries");
        
        setupAdapters();
        spActivity.setText(expense.getCategory(), false);
        updateMethodAdapter(expense.getCategory());
        
        String notes = expense.getNotes() != null ? expense.getNotes() : "";
        String method = notes.contains("Method: ") ? notes.substring(notes.indexOf("Method: ") + 8, notes.contains("|") ? notes.indexOf("|") : notes.length()).trim() : "Other";
        spMethod.setText(method, false);
        updateExpenseTypeAdapter(expense.getCategory(), method);
        
        String type = notes.contains("Type: ") ? notes.substring(notes.indexOf("Type: ") + 6).trim() : "Other";
        spExpenseType.setText(type, false);
        
        boolean isLabor = type.toLowerCase().contains("labor");
        tilLaborType.setVisibility(isLabor ? View.VISIBLE : View.GONE);
        
        if (isLabor && expense.getLaborType() != null) {
            boolean isOwner = "owner".equals(expense.getLaborType());
            spLaborType.setText(isOwner ? LABOR_TYPES[1] : LABOR_TYPES[0], false);
            layoutStandardFields.setVisibility(isOwner ? View.GONE : View.VISIBLE);
            layoutImplicitFields.setVisibility(isOwner ? View.VISIBLE : View.GONE);
            tvCostLabel.setText(isOwner ? "Est. Labor Cost" : "Actual Cost");
            if (isOwner) {
                etDailyWage.setText(String.valueOf(expense.getUnitPrice()));
                etDays.setText(String.valueOf((int)expense.getQuantity()));
            }
        }

        spUnit.setText(expense.getUnit(), false);
        etProduct.setText(expense.getProductName());
        etQuantity.setText(String.valueOf(expense.getQuantity()));
        etPrice.setText(String.valueOf(expense.getUnitPrice()));
        
        btnSave.setText("Update Expense");
        btnDelete.setVisibility(View.VISIBLE);
        calculateTotal();
    }

    private void saveExpense() {
        String activity = spActivity.getText().toString();
        String method = spMethod.getText().toString();
        String type = spExpenseType.getText().toString();
        String product = etProduct.getText().toString();
        String laborType = spLaborType.getText().toString();
        String haulingType = spInternalHauling.getText().toString();
        
        boolean isOwnerLabor = laborType.equals(LABOR_TYPES[1]);
        boolean isInternalHauling = haulingType.equals(HAULING_TYPES[1]);
        
        String qtyS = isOwnerLabor ? etDays.getText().toString() : etQuantity.getText().toString();
        String priceS = isOwnerLabor ? etDailyWage.getText().toString() : etPrice.getText().toString();

        if (activity.isEmpty() || method.isEmpty() || type.isEmpty() || product.isEmpty() || qtyS.isEmpty() || priceS.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double qty = Double.parseDouble(qtyS);
            double price = Double.parseDouble(priceS);
            double cost = qty * price;

            btnSave.setEnabled(false);

            executor.execute(() -> {
                try {
                    String notes = "Method: " + method + " | Type: " + type;
                    ExpenseEntity e = editingExpense != null ? editingExpense : new ExpenseEntity(1, currentBatchId, currentPhase, activity, product, qty, "", price);
                    
                    if (editingExpense != null) {
                        e.setCategory(activity); e.setProductName(product); e.setQuantity(qty); e.setUnitPrice(price);
                    }
                    
                    e.setNotes(notes);
                    e.setExpenseType(isInternalHauling ? ExpenseEntity.TYPE_HAULING_INTERNAL : ExpenseEntity.TYPE_GENERAL);

                    if (isOwnerLabor) {
                        e.setLaborType("owner"); e.setImplicitCost(cost); e.setTotalCost(0);
                    } else {
                        e.setLaborType("paid"); e.setImplicitCost(0); e.setTotalCost(cost);
                    }

                    if (editingExpense == null) db.expenseDao().insert(e); else db.expenseDao().update(e);
                    
                    runOnUiThread(() -> {
                        if (!isFinishing()) {
                            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                            if (editingExpense != null) finish(); else {
                                clearForm();
                                btnSave.setEnabled(true);
                            }
                        }
                    });
                } catch (Exception ex) {
                    runOnUiThread(() -> {
                        if (!isFinishing()) {
                            btnSave.setEnabled(true);
                            Toast.makeText(this, "Error saving expense", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception e) { Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show(); }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this).setTitle("Delete").setMessage("Are you sure?")
                .setPositiveButton("Delete", (d, w) -> deleteExpense()).setNegativeButton("Cancel", null).show();
    }

    private void deleteExpense() {
        btnDelete.setEnabled(false);
        executor.execute(() -> {
            try {
                db.expenseDao().delete(editingExpense);
                runOnUiThread(() -> {
                    if (!isFinishing()) {
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (!isFinishing()) {
                        btnDelete.setEnabled(true);
                        Toast.makeText(this, "Error deleting", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void clearForm() {
        editingExpense = null;
        spActivity.setText("", false); spMethod.setText("", false); spExpenseType.setText("", false);
        resetLaborView();
        etProduct.setText(""); etQuantity.setText(""); etPrice.setText("");
        etDailyWage.setText(""); etDays.setText("");
        tvFormTotal.setText("₱0.00");
        btnSave.setText("Add to Ledger"); btnDelete.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
