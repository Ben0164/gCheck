package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ExpensePhaseActivity extends AppCompatActivity {

    private RecyclerView rvPhases;
    private PhaseAdapter adapter;
    private TextView tvTabDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_phase);

        initViews();
        setupRecyclerView();
    }

    private void initViews() {
        rvPhases = findViewById(R.id.rv_phases);
        tvTabDashboard = findViewById(R.id.tv_tab_dashboard);

        tvTabDashboard.setOnClickListener(v -> {
            finish();
        });
    }

    private void setupRecyclerView() {
        rvPhases.setLayoutManager(new LinearLayoutManager(this));
        
        List<PhaseAdapter.PhaseItem> phases = new ArrayList<>();
        phases.add(new PhaseAdapter.PhaseItem("Land Preparation", "Plowing, harrowing, leveling"));
        phases.add(new PhaseAdapter.PhaseItem("Crop Establishment", "Seedbed, seedling, transplanting"));
        phases.add(new PhaseAdapter.PhaseItem("Crop Management", "Fertilizing, weeding, irrigation"));
        phases.add(new PhaseAdapter.PhaseItem("Reproductive / Ripening Monitoring", "Monitoring growth and health"));
        phases.add(new PhaseAdapter.PhaseItem("Harvesting", "Cutting, threshing, hauling"));
        phases.add(new PhaseAdapter.PhaseItem("Post-Harvest", "Drying, storage, milling"));

        adapter = new PhaseAdapter(phases, item -> {
            // Simplified flow: All phases go directly to PhaseExpenseActivity
            Intent intent = new Intent(ExpensePhaseActivity.this, PhaseExpenseActivity.class);
            intent.putExtra("PHASE", item.getTitle());
            startActivity(intent);
        });
        
        rvPhases.setAdapter(adapter);
    }
}
