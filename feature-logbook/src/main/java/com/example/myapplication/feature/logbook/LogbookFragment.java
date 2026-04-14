package com.example.myapplication.feature.logbook;

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
import com.example.myapplication.core.common.FeatureNavigationHost;
import com.example.myapplication.core.data.db.AppDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LogbookFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logbook, container, false);
        RecyclerView rvLogbook = view.findViewById(R.id.rv_logbook);
        rvLogbook.setLayoutManager(new LinearLayoutManager(getContext()));
        LogbookAdapter adapter = new LogbookAdapter(batch -> {
            if (getActivity() instanceof FeatureNavigationHost) {
                ((FeatureNavigationHost) getActivity()).openExpenseForBatch(batch.getId());
            }
        });
        rvLogbook.setAdapter(adapter);
        FloatingActionButton fabAddBatch = view.findViewById(R.id.fab_add_batch);
        fabAddBatch.setOnClickListener(v -> startActivity(new Intent(getContext(), CreateBatchActivity.class)));
        AppDatabase db = AppDatabase.getInstance(requireContext());
        db.batchDao().getAllBatches().observe(getViewLifecycleOwner(), batches -> {
            if (batches != null) {
                adapter.setBatches(batches);
                rvLogbook.setVisibility(batches.isEmpty() ? View.GONE : View.VISIBLE);
                View emptyView = view.findViewById(R.id.layout_empty_logbook);
                if (emptyView != null) emptyView.setVisibility(batches.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
        return view;
    }
}
