package com.example.myapplication.feature.collaboration;

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
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class InboxFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        
        RecyclerView rvInbox = view.findViewById(R.id.rv_inbox);
        rvInbox.setLayoutManager(new LinearLayoutManager(getContext()));
        
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("John dave corpuz", "Hello! How is the grain quality today?", "10:30 AM", 0xFF98FB98));
        messages.add(new Message("Market Alert", "Prices in Bataan have increased by 2%.", "09:15 AM", 0xFF98FB98));
        messages.add(new Message("New Feature", "Try our new profit calculator tool!", "Yesterday", 0xFF98FB98));
        
        InboxAdapter adapter = new InboxAdapter(messages, message -> {
            if (getActivity() instanceof FeatureNavigationHost) {
                ((FeatureNavigationHost) getActivity()).openFragment(new ChatMessageFragment());
            }
        });
        rvInbox.setAdapter(adapter);

        MaterialButton btnMarkRead = view.findViewById(R.id.btn_mark_read);
        btnMarkRead.setOnClickListener(v -> adapter.markAllAsRead());
        
        return view;
    }
}
