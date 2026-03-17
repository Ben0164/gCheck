package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GrainCheckFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grain_check, container, false);
        
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new HomeFragment());
            ((MainActivity) requireActivity()).setBottomNavSelection(R.id.navigation_home);
        });
        
        return view;
    }
}
