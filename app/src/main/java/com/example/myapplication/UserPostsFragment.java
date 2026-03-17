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

public class UserPostsFragment extends Fragment {

    private PostAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_posts, container, false);

        RecyclerView rvPosts = view.findViewById(R.id.rv_user_posts);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Filter posts for the current user
        adapter = new PostAdapter(PostRepository.getUserPosts("Aeron John G. Ramos"));
        rvPosts.setAdapter(adapter);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).loadFragment(new ProfileFragment());
            }
        });

        view.findViewById(R.id.btn_create_post).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).loadFragment(new CreatePostFragment());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
