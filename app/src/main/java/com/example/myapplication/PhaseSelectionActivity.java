package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.Arrays;
import java.util.List;

public class PhaseSelectionActivity extends AppCompatActivity {

    private final List<String> PHASES = Arrays.asList(
            "Land Preparation", "Crop Establishment", "Crop Management",
            "Reproductive/Ripening Monitoring", "Harvesting", "Post-Harvest"
    );

    private final List<String> DESCRIPTIONS = Arrays.asList(
            "Tractor, plowing, and clearing",
            "Seeds, planting, and basal fertilizer",
            "Irrigation, weed and pest control",
            "Monitoring and topdress fertilizer",
            "Machinery, labor, and sacks",
            "Drying, storage, and transport"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phase_selection);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }

        RecyclerView rv = findViewById(R.id.rv_phases);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new PhaseAdapter());
    }

    class PhaseAdapter extends RecyclerView.Adapter<PhaseAdapter.VH> {
        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_phase_card, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            String name = PHASES.get(pos);
            String desc = DESCRIPTIONS.get(pos);
            h.tvName.setText(name);
            h.tvDesc.setText(desc);
            h.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(PhaseSelectionActivity.this, PhaseExpenseActivity.class);
                intent.putExtra("PHASE", name);
                startActivity(intent);
            });
        }

        @Override public int getItemCount() { return PHASES.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvDesc;
            VH(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_phase_name);
                tvDesc = itemView.findViewById(R.id.tv_phase_desc);
            }
        }
    }
}
