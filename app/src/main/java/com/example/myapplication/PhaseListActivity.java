package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class PhaseListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phase_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }

        setupPhaseCards();
    }

    private void setupPhaseCards() {
        findViewById(R.id.card_land_prep).setOnClickListener(v -> openPhase("Land Preparation"));
        findViewById(R.id.card_crop_est).setOnClickListener(v -> openPhase("Crop Establishment"));
        findViewById(R.id.card_crop_mgmt).setOnClickListener(v -> openPhase("Crop Management"));
        findViewById(R.id.card_monitoring).setOnClickListener(v -> openPhase("Reproductive/Ripening Monitoring"));
        findViewById(R.id.card_harvesting).setOnClickListener(v -> openPhase("Harvesting"));
        findViewById(R.id.card_post_harvest).setOnClickListener(v -> openPhase("Post-Harvest"));
    }

    private void openPhase(String phaseName) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra(CategoryActivity.EXTRA_PHASE, phaseName);
        startActivity(intent);
    }
}
