package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.core.common.ExpenseSummaryHelper;
import com.example.myapplication.feature.expense.ExpenseAdapter;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.ExpenseEntity;
import com.example.myapplication.core.data.entity.ProductEntity;
import com.example.myapplication.core.common.ProfitCalculator;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PhaseExpenseActivity extends AppCompatActivity {

    private String phaseName;
    private long batchId;
    private AppDatabase db;
    private ExpenseAdapter adapter;
    private TextView tvPhaseTotal, tvPhaseExplicit, tvPhaseImplicit, tvBreakevenLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phase_expense);

        phaseName = getIntent().getStringExtra("PHASE");
        if (phaseName == null) phaseName = "Land Preparation";
        
        batchId = getIntent().getLongExtra("BATCH_ID", -1);
        if (batchId <= 0) {
            batchId = 1; // Fallback to default batch if not provided
        }
        
        db = AppDatabase.getInstance(this);

        initViews();
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(phaseName);
            toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }

        tvPhaseTotal = findViewById(R.id.tv_phase_total);
        tvPhaseExplicit = findViewById(R.id.tv_phase_explicit);
        tvPhaseImplicit = findViewById(R.id.tv_phase_implicit);
        tvBreakevenLabel = findViewById(R.id.tv_breakeven_label);
        
        ExtendedFloatingActionButton fabAdd = findViewById(R.id.fab_add);
        
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, ExpenseLedgerActivity.class);
            intent.putExtra("PHASE", phaseName);
            intent.putExtra("BATCH_ID", batchId);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.rv_phase_expenses);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExpenseAdapter(new ArrayList<>());
        adapter.setOnExpenseClickListener(expense -> {
            Intent intent = new Intent(this, ExpenseLedgerActivity.class);
            intent.putExtra("PHASE", phaseName);
            intent.putExtra("BATCH_ID", batchId);
            intent.putExtra("EXPENSE_ID", expense.getId());
            startActivity(intent);
        });
        rv.setAdapter(adapter);
    }

    private void loadExpenses() {
        db.expenseDao().getExpensesByPhase(batchId, phaseName).observe(this, expenses -> {
            if (expenses != null) {
                adapter.setExpenses(expenses);
                
                ExpenseSummaryHelper helper = new ExpenseSummaryHelper(expenses);
                double explicit = helper.getTotalExplicitCost();
                double implicit = helper.getTotalImplicitCost();
                double grandTotal = helper.getGrandTotal();

                tvPhaseExplicit.setText(String.format(Locale.getDefault(), "₱%.2f", explicit));
                tvPhaseImplicit.setText(String.format(Locale.getDefault(), "₱%.2f", implicit));
                tvPhaseTotal.setText(String.format(Locale.getDefault(), "₱%.2f", grandTotal));

                // Calculate Breakeven based on Yield
                new Thread(() -> {
                    List<ProductEntity> products = db.productDao().getAll();
                    ProductEntity latest = (products != null && !products.isEmpty()) ? products.get(0) : null;
                    double yield = (latest != null) ? latest.getQuantity() : 5000.0;
                    
                    BigDecimal breakeven = ProfitCalculator.calculateBreakevenPrice(grandTotal, yield);
                    
                    runOnUiThread(() -> {
                        if (tvBreakevenLabel != null) {
                            tvBreakevenLabel.setText(String.format(Locale.getDefault(), 
                                    "Est. Breakeven Price: ₱%s/kg (Yield: %.0fkg)", 
                                    breakeven.toString(), yield));
                        }
                    });
                }).start();
            }
        });
    }
}
