package com.example.myapplication.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activity.CreatePostActivity;
import com.example.myapplication.adapter.PostAdapter;
import com.example.myapplication.model.Post;
import com.example.myapplication.repository.PostRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PostFragment extends Fragment implements PostAdapter.OnPostClickListener {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private FloatingActionButton fabCreatePost;
    private PostRepository postRepository;
    private List<Post> posts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupObservers();
        
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        fabCreatePost = view.findViewById(R.id.fabCreatePost);
        
        postRepository = PostRepository.getInstance();
        
        fabCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreatePostActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        postAdapter = new PostAdapter(getContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(postAdapter);
    }

    private void setupObservers() {
        postRepository.getAllPosts().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> postList) {
                posts = postList;
                postAdapter.setPosts(posts);
            }
        });
    }

    @Override
    public void onPostClick(Post post) {
        // Navigate to post details
        postRepository.getPostById(post.getId());
        // TODO: Navigate to PostDetailActivity
    }

    @Override
    public void onPostLike(Post post) {
        if (post.isLiked()) {
            postRepository.unlikePost(post.getId());
        } else {
            postRepository.likePost(post.getId());
        }
    }

    @Override
    public void onPostComment(Post post) {
        // Navigate to comments
        // TODO: Navigate to CommentsActivity
    }

    @Override
    public void onPostShare(Post post) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, post.getTitle() + "\n\n" + post.getContent());
        startActivity(Intent.createChooser(shareIntent, "Share Post"));
    }

    @Override
    public void onPostEdit(Post post) {
        Intent intent = new Intent(getActivity(), CreatePostActivity.class);
        intent.putExtra("postId", post.getId());
        intent.putExtra("title", post.getTitle());
        intent.putExtra("content", post.getContent());
        intent.putExtra("category", post.getCategory());
        intent.putExtra("imageUrl", post.getImageUrl());
        startActivity(intent);
    }

    @Override
    public void onPostDelete(Post post) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    postRepository.deletePost(post.getId());
                    Toast.makeText(getContext(), "Post deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh posts when fragment resumes
        postRepository.refreshPosts();
    }
}
