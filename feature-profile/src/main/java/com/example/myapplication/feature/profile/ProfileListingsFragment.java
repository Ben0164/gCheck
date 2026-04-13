package com.example.myapplication.feature.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.palay.data.repository.SessionManager;
import java.util.ArrayList;

public class ProfileListingsFragment extends Fragment {

    private RecyclerView rvListings;
    private TextView tvEmpty;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_listings, container, false);

        db = AppDatabase.getInstance(requireContext());
        rvListings = view.findViewById(R.id.rv_profile_listings);
        tvEmpty = view.findViewById(R.id.tv_empty_listings);

        setupRecyclerView();
        loadData();

        return view;
    }

    private void setupRecyclerView() {
        rvListings.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        rvListings.setAdapter(new ProfileGridAdapter(new ArrayList<>()));
    }

    private void loadData() {
        if (SessionManager.getCurrentUser() == null) return;
        
        db.productDao().getUserListings(SessionManager.getCurrentUser().getId()).observe(getViewLifecycleOwner(), listings -> {
            if (listings == null || listings.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvListings.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvListings.setVisibility(View.VISIBLE);
                // Update adapter with images - since ProductEntity might not have image yet, using placeholder
                ArrayList<Integer> placeholders = new ArrayList<>();
                for (int i = 0; i < listings.size(); i++) placeholders.add(R.drawable.ic_grain);
                ((ProfileGridAdapter) rvListings.getAdapter()).updateItems(placeholders);
            }
        });
    }
}
