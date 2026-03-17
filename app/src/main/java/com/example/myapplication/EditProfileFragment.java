package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EditProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).loadFragment(new ProfileFragment());
            }
        });

        view.findViewById(R.id.btn_save_changes).setOnClickListener(v -> {
            // In a real app, logic to save data would go here.
            // For now, just navigate back.
            if (getActivity() != null) {
                ((MainActivity) getActivity()).loadFragment(new ProfileFragment());
            }
        });

        return view;
    }
}
