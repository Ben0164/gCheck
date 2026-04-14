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
import com.example.myapplication.core.data.entity.ActivityEntity;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityListActivity extends AppCompatActivity {
    private String phaseName;
    private AppDatabase db;
    private ActivityAdapter adapter;
    private TextView tvPhaseTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_list);
        phaseName = getIntent().getStringExtra("PHASE");
        if (phaseName == null) phaseName = "Land Preparation";
        db = AppDatabase.getInstance(this);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(phaseName);
            toolbar.setNavigationOnClickListener(v -> finish());
        }
        tvPhaseTotal = findViewById(R.id.tv_phase_total);
        RecyclerView rvActivities = findViewById(R.id.rv_activities);
        rvActivities.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ActivityAdapter(new ArrayList<>());
        rvActivities.setAdapter(adapter);
        db.activityDao().getActivitiesByPhase(phaseName).observe(this, activities -> adapter.setActivities(activities));
        db.expenseDao().getTotalByPhase(1, phaseName).observe(this, total -> tvPhaseTotal.setText(String.format(Locale.getDefault(), "₱%.2f", total != null ? total : 0.0)));
    }

    class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
        private List<ActivityEntity> activities;
        ActivityAdapter(List<ActivityEntity> activities) { this.activities = activities; }
        void setActivities(List<ActivityEntity> activities) { this.activities = activities; notifyDataSetChanged(); }
        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_method, parent, false)); }
        @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ActivityEntity activity = activities.get(position);
            holder.tvName.setText(activity.getName());
            db.expenseDao().getTotalByActivity(1, activity.getId()).observe(ActivityListActivity.this, total -> holder.tvTotal.setText(String.format(Locale.getDefault(), "Total: ₱%.2f", total != null ? total : 0.0)));
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(ActivityListActivity.this, MethodListActivity.class);
                intent.putExtra("ACTIVITY_ID", activity.getId());
                intent.putExtra("ACTIVITY_NAME", activity.getName());
                startActivity(intent);
            });
        }
        @Override public int getItemCount() { return activities.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvTotal;
            ViewHolder(View v) { super(v); tvName = v.findViewById(R.id.tv_name); tvTotal = v.findViewById(R.id.tv_total_cost); }
        }
    }
}
