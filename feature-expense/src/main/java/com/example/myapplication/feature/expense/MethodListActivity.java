package com.example.myapplication.feature.expense;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.MethodEntity;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MethodListActivity extends AppCompatActivity {
    private long activityId;
    private String activityName;
    private AppDatabase db;
    private MethodAdapter adapter;
    private TextView tvActivityTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method_list);
        activityId = getIntent().getLongExtra("ACTIVITY_ID", -1);
        activityName = getIntent().getStringExtra("ACTIVITY_NAME");
        if (activityName == null) activityName = "Methods";
        db = AppDatabase.getInstance(this);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(activityName);
            toolbar.setNavigationOnClickListener(v -> finish());
        }
        tvActivityTotal = findViewById(R.id.tv_activity_total);
        RecyclerView rvMethods = findViewById(R.id.rv_methods);
        rvMethods.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MethodAdapter(new ArrayList<>());
        rvMethods.setAdapter(adapter);
        db.methodDao().getMethodsByActivity(activityId).observe(this, methods -> adapter.setMethods(methods));
        db.expenseDao().getTotalByActivity(1, activityId).observe(this, total -> tvActivityTotal.setText(String.format(Locale.getDefault(), "₱%.2f", total != null ? total : 0.0)));
    }

    class MethodAdapter extends RecyclerView.Adapter<MethodAdapter.ViewHolder> {
        private List<MethodEntity> methods;
        MethodAdapter(List<MethodEntity> methods) { this.methods = methods; }
        void setMethods(List<MethodEntity> methods) { this.methods = methods; notifyDataSetChanged(); }
        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_method, parent, false)); }
        @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MethodEntity method = methods.get(position);
            holder.tvName.setText(method.getName());
            db.expenseDao().getTotalByMethod(1, method.getId()).observe(MethodListActivity.this, total -> holder.tvTotal.setText(String.format(Locale.getDefault(), "Total: ₱%.2f", total != null ? total : 0.0)));
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(MethodListActivity.this, ExpenseListActivity.class);
                intent.putExtra("METHOD_ID", method.getId());
                intent.putExtra("METHOD_NAME", method.getName());
                intent.putExtra("ACTIVITY_ID", activityId);
                intent.putExtra("ACTIVITY_NAME", activityName);
                startActivity(intent);
            });
        }
        @Override public int getItemCount() { return methods.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvTotal;
            ViewHolder(View v) { super(v); tvName = v.findViewById(R.id.tv_name); tvTotal = v.findViewById(R.id.tv_total_cost); }
        }
    }
}
