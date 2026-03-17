package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreatePostFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);

        EditText etTitle = view.findViewById(R.id.et_post_title);
        EditText etDesc = view.findViewById(R.id.et_post_desc);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).loadFragment(new UserPostsFragment());
            }
        });

        view.findViewById(R.id.btn_post).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            
            if (!title.isEmpty() && !desc.isEmpty()) {
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                // Add the new post to the central repository
                PostRepository.addPost(new PostModel("Aeron John G. Ramos", title, desc, date));
                
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).loadFragment(new UserPostsFragment());
                }
            }
        });

        return view;
    }
}
