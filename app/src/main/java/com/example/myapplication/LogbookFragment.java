package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LogbookFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logbook, container, false);

        RecyclerView rvLogbook = view.findViewById(R.id.rv_logbook);
        rvLogbook.setLayoutManager(new LinearLayoutManager(getContext()));

        LogbookAdapter adapter = new LogbookAdapter(batch -> {
            // Navigate to ExpenseFragment for this specific batch
            ExpenseFragment expenseFragment = new ExpenseFragment();
            Bundle args = new Bundle();
            args.putLong("BATCH_ID", batch.getId());
            // actualYieldKg logic is now handled inside ExpenseFragment via LiveData
            expenseFragment.setArguments(args);

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(expenseFragment);
            }
        });
        rvLogbook.setAdapter(adapter);

        FloatingActionButton fabAddBatch = view.findViewById(R.id.fab_add_batch);
        fabAddBatch.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateBatchActivity.class);
            startActivity(intent);
        });

        AppDatabase db = AppDatabase.getInstance(requireContext());
        // UI uses LiveData for reactive updates without main thread queries
        db.batchDao().getAllBatches().observe(getViewLifecycleOwner(), batches -> {
            if (batches != null) {
                adapter.setBatches(batches);
                rvLogbook.setVisibility(batches.isEmpty() ? View.GONE : View.VISIBLE);
                View emptyView = view.findViewById(R.id.layout_empty_logbook);
                if (emptyView != null) {
                    emptyView.setVisibility(batches.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }
        });

        return view;
    }
}
