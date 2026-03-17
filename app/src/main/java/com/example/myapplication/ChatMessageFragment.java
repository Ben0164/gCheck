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
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessageFragment extends Fragment {

    private ChatAdapter adapter;
    private List<ChatMessageModel> messages;
    private RecyclerView rvChat;
    private TextInputEditText etInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_message, container, false);

        rvChat = view.findViewById(R.id.rv_chat);
        etInput = view.findViewById(R.id.et_chat_input);
        View btnSend = view.findViewById(R.id.btn_chat_send);
        View btnBack = view.findViewById(R.id.toolbar_chat);

        messages = new ArrayList<>();
        messages.add(new ChatMessageModel("Hello! How is the grain quality today?", false, "10:00 AM"));
        messages.add(new ChatMessageModel("It looks good, moisture is low.", true, "10:05 AM"));
        messages.add(new ChatMessageModel("Great! I'll check later.", false, "10:10 AM"));

        adapter = new ChatAdapter(messages);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(adapter);
        rvChat.scrollToPosition(messages.size() - 1);

        btnSend.setOnClickListener(v -> sendMessage());

        view.findViewById(R.id.toolbar_chat).setOnClickListener(v -> {
             // Handle back navigation or toolbar clicks
        });
        
        // Setup Toolbar back button logic
        com.google.android.material.appbar.MaterialToolbar toolbar = view.findViewById(R.id.toolbar_chat);
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).loadFragment(new InboxFragment());
            }
        });

        return view;
    }

    private void sendMessage() {
        String text = etInput.getText().toString().trim();
        if (!text.isEmpty()) {
            String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
            messages.add(new ChatMessageModel(text, true, currentTime));
            adapter.notifyItemInserted(messages.size() - 1);
            rvChat.scrollToPosition(messages.size() - 1);
            etInput.setText("");
        }
    }
}
