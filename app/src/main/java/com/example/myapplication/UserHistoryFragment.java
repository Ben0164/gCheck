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

public class UserHistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_history, container, false);

        RecyclerView rvHistory = view.findViewById(R.id.rv_user_history);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        List<HistoryModel> historyItems = new ArrayList<>();
        historyItems.add(new HistoryModel("Grain Check Submitted", "Quality analysis for Jasmine Rice Batch #42", "10:30 AM"));
        historyItems.add(new HistoryModel("Profit Calculated", "Computed potential margin for Wheat harvest", "Yesterday"));
        historyItems.add(new HistoryModel("Post Created", "Shared update about current field conditions", "2 days ago"));
        historyItems.add(new HistoryModel("Message Sent", "Reply sent to John dave corpuz", "3 days ago"));

        HistoryAdapter adapter = new HistoryAdapter(historyItems);
        rvHistory.setAdapter(adapter);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).loadFragment(new ProfileFragment());
            }
        });

        return view;
    }
}
