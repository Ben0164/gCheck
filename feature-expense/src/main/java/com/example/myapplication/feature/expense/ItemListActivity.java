package com.example.myapplication.feature.expense;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.ExpenseEntity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemListActivity extends AppCompatActivity {
    public static final String EXTRA_PHASE = "extra_phase";
    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String EXTRA_BATCH_ID = "extra_batch_id";
    private String phaseName;
    private String categoryName;
    private long batchId;
    private AppDatabase db;
    private ExpenseAdapter adapter;
    private TextView tvCategoryTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        phaseName = getIntent().getStringExtra(EXTRA_PHASE);
        categoryName = getIntent().getStringExtra(EXTRA_CATEGORY);
        batchId = getIntent().getLongExtra(EXTRA_BATCH_ID, 1);
        db = AppDatabase.getInstance(this);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
            toolbar.setTitle(categoryName);
        }
        tvCategoryTotal = findViewById(R.id.tv_category_total);
        ((TextView) findViewById(R.id.tv_category_label)).setText(categoryName);
        RecyclerView rv = findViewById(R.id.rv_items);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExpenseAdapter(new ArrayList<>());
        adapter.setOnExpenseClickListener(this::showItemFormDialog);
        rv.setAdapter(adapter);
        ExtendedFloatingActionButton fabAdd = findViewById(R.id.fab_add_item);
        fabAdd.setOnClickListener(v -> showItemFormDialog(null));
        loadItems();
    }

    private void loadItems() {
        db.expenseDao().getExpensesByBatch(batchId).observe(this, expenses -> {
            if (expenses != null) {
                List<ExpenseEntity> filtered = new ArrayList<>();
                double total = 0;
                for (ExpenseEntity e : expenses) {
                    if (categoryName.equals(e.getCategory()) && (phaseName == null || phaseName.equals(e.getPhase()))) {
                        filtered.add(e);
                        total += e.getTotalCost();
                    }
                }
                adapter.setExpenses(filtered);
                tvCategoryTotal.setText(String.format(Locale.getDefault(), "₱%.2f", total));
            }
        });
    }

    private void showItemFormDialog(ExpenseEntity expense) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_structured_item_form, null);
        TextInputEditText etItemName = view.findViewById(R.id.et_item_name);
        TextInputEditText etCost = view.findViewById(R.id.et_cost);
        AutoCompleteTextView spUnit = view.findViewById(R.id.sp_unit);
        TextInputEditText etNotes = view.findViewById(R.id.et_notes);
        MaterialButton btnSave = view.findViewById(R.id.btn_save_item);
        LinearLayout layoutLabor = view.findViewById(R.id.layout_labor_fields);
        TextInputEditText etWage = view.findViewById(R.id.et_labor_wage);
        TextInputEditText etWorkers = view.findViewById(R.id.et_labor_workers);
        TextInputEditText etDays = view.findViewById(R.id.et_labor_days);
        boolean isLabor = "Labor".equalsIgnoreCase(categoryName);
        layoutLabor.setVisibility(isLabor ? View.VISIBLE : View.GONE);
        String[] units = {"hectare", "hour", "day", "bag", "kg", "unit", "trip"};
        spUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, units));
        if (expense != null) {
            etItemName.setText(expense.getProductName());
            etCost.setText(String.valueOf(expense.getUnitPrice()));
            spUnit.setText(expense.getUnit(), false);
            etNotes.setText(expense.getNotes());
            etWage.setText(String.valueOf(expense.getWage()));
            etWorkers.setText(String.valueOf(expense.getWorkers()));
            etDays.setText(String.valueOf(expense.getDays()));
        }
        btnSave.setOnClickListener(v -> {
            String name = etItemName.getText() != null ? etItemName.getText().toString() : "";
            String costStr = etCost.getText() != null ? etCost.getText().toString() : "";
            if (name.isEmpty() || costStr.isEmpty()) {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                return;
            }
            double cost = Double.parseDouble(costStr);
            String unit = spUnit.getText().toString();
            String notes = etNotes.getText() != null ? etNotes.getText().toString() : "";
            new Thread(() -> {
                if (expense == null) {
                    ExpenseEntity ne = new ExpenseEntity(1, batchId, phaseName, categoryName, name, 1, unit, cost);
                    ne.setNotes(notes);
                    if (isLabor) {
                        ne.setWage(safeParseDouble(etWage.getText().toString()));
                        ne.setWorkers(safeParseInt(etWorkers.getText().toString()));
                        ne.setDays(safeParseInt(etDays.getText().toString()));
                    }
                    db.expenseDao().insert(ne);
                } else {
                    expense.setProductName(name);
                    expense.setUnitPrice(cost);
                    expense.setUnit(unit);
                    expense.setTotalCost(cost);
                    expense.setNotes(notes);
                    db.expenseDao().update(expense);
                }
                runOnUiThread(() -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });
        dialog.setContentView(view);
        dialog.show();
    }

    private double safeParseDouble(String val) { try { return Double.parseDouble(val); } catch (Exception e) { return 0.0; } }
    private int safeParseInt(String val) { try { return Integer.parseInt(val); } catch (Exception e) { return 0; } }
}
