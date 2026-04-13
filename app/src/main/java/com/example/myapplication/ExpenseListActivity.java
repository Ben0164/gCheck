package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.palay.data.adapter.ExpenseAdapter;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.ExpenseEntity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.util.ArrayList;
import java.util.Locale;

public class ExpenseListActivity extends AppCompatActivity {

    private long methodId;
    private long activityId;
    private String methodName;
    private String activityName;
    private AppDatabase db;
    private RecyclerView rvExpenses;
    private ExpenseAdapter adapter;
    private TextView tvMethodTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        methodId = getIntent().getLongExtra("METHOD_ID", -1);
        methodName = getIntent().getStringExtra("METHOD_NAME");
        activityId = getIntent().getLongExtra("ACTIVITY_ID", -1);
        activityName = getIntent().getStringExtra("ACTIVITY_NAME");

        db = AppDatabase.getInstance(this);
        initViews();
        setupRecyclerView();
        loadData();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(methodName);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvMethodTotal = findViewById(R.id.tv_method_total);
        rvExpenses = findViewById(R.id.rv_expenses);

        ExtendedFloatingActionButton fabAdd = findViewById(R.id.fab_add_expense);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, ExpenseLedgerActivity.class);
            intent.putExtra("PHASE", "Land Preparation");
            intent.putExtra("ACTIVITY_ID", activityId);
            intent.putExtra("ACTIVITY_NAME", activityName);
            intent.putExtra("METHOD_ID", methodId);
            intent.putExtra("METHOD_NAME", methodName);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExpenseAdapter(new ArrayList<>());
        adapter.setOnExpenseClickListener(expense -> {
            Intent intent = new Intent(this, ExpenseLedgerActivity.class);
            intent.putExtra("EXPENSE_ID", expense.getId());
            startActivity(intent);
        });
        rvExpenses.setAdapter(adapter);
    }

    private void loadData() {
        // Using userId 1 as per project convention
        db.expenseDao().getExpensesByMethod(1, methodId).observe(this, expenses -> {
            adapter.setExpenses(expenses);
        });

        db.expenseDao().getTotalByMethod(1, methodId).observe(this, total -> {
            tvMethodTotal.setText(String.format(Locale.getDefault(), "₱%.2f", total != null ? total : 0.0));
        });
    }
}
