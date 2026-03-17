package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class LogbookFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logbook, container, false);

        RecyclerView rvLogbook = view.findViewById(R.id.rv_logbook);
        rvLogbook.setLayoutManager(new LinearLayoutManager(getContext()));

        List<HarvestRecord> records = new ArrayList<>();
        records.add(new HarvestRecord("Premium Wheat Harvest", "Grade A | 500 Tons", "2023-10-25", "+₱1,250"));
        records.add(new HarvestRecord("Jasmine Rice Batch", "Grade B | 200 Tons", "2023-09-12", "+₱850"));
        records.add(new HarvestRecord("Corn Feed", "Grade C | 800 Tons", "2023-08-05", "+₱2,100"));

        LogbookAdapter adapter = new LogbookAdapter(records, record -> {
            // Navigate to details
            Fragment detailsFragment = new LogbookDetailsFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvLogbook.setAdapter(adapter);

        return view;
    }
}
