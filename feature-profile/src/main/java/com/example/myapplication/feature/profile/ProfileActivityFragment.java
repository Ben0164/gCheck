package com.example.myapplication.feature.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.feature.profile.R;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.common.SessionManager;

public class ProfileActivityFragment extends Fragment {

    private RecyclerView rvActivity;
    private TextView tvEmpty;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_activity, container, false);

        db = AppDatabase.getInstance(requireContext());
        rvActivity = view.findViewById(R.id.rv_profile_activity);
        tvEmpty = view.findViewById(R.id.tv_empty_activity);

        setupRecyclerView();
        loadData();

        return view;
    }

    private void setupRecyclerView() {
        rvActivity.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void loadData() {
        if (SessionManager.getCurrentUser() == null) return;
        tvEmpty.setVisibility(View.VISIBLE);
    }
}
