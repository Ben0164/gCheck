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
import com.google.android.material.appbar.MaterialToolbar;
import java.util.Arrays;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    public static final String EXTRA_PHASE = "extra_phase";
    private String phaseName;
    private final List<String> CATEGORIES = Arrays.asList("Machinery", "Fuel & Maintenance", "Irrigation", "Weed Control", "Soil Inputs", "Labor", "Miscellaneous");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        phaseName = getIntent().getStringExtra(EXTRA_PHASE);
        if (phaseName == null) phaseName = "Land Preparation";
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }
        TextView tvPhaseTitle = findViewById(R.id.tv_phase_title);
        tvPhaseTitle.setText(phaseName);
        RecyclerView rv = findViewById(R.id.rv_categories);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new CategoryAdapter(CATEGORIES, category -> {
            Intent intent = new Intent(this, ItemListActivity.class);
            intent.putExtra(ItemListActivity.EXTRA_PHASE, phaseName);
            intent.putExtra(ItemListActivity.EXTRA_CATEGORY, category);
            startActivity(intent);
        }));
    }

    static class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private final List<String> categories;
        private final OnCategoryClickListener listener;
        interface OnCategoryClickListener { void onCategoryClick(String category); }
        CategoryAdapter(List<String> categories, OnCategoryClickListener listener) { this.categories = categories; this.listener = listener; }
        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_card, parent, false)); }
        @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) { String category = categories.get(position); holder.tvTitle.setText(category); holder.itemView.setOnClickListener(v -> listener.onCategoryClick(category)); }
        @Override public int getItemCount() { return categories.size(); }
        static class ViewHolder extends RecyclerView.ViewHolder { TextView tvTitle; ViewHolder(View itemView) { super(itemView); tvTitle = itemView.findViewById(R.id.tv_category_title); } }
    }
}
