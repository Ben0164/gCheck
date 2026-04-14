package com.example.myapplication.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.model.Post;
import com.example.myapplication.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PostRepository {
    private static PostRepository instance;
    private final MutableLiveData<List<Post>> allPosts = new MutableLiveData<>();
    private final MutableLiveData<Post> currentPost = new MutableLiveData<>();
    private List<Post> postsList = new ArrayList<>();
    private User currentUser;

    private PostRepository() {
        // Initialize with sample data
        initializeSamplePosts();
    }

    public static PostRepository getInstance() {
        if (instance == null) {
            instance = new PostRepository();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void initializeSamplePosts() {
        // Sample posts for testing
        postsList.add(new Post(
            UUID.randomUUID().toString(),
            "Welcome to gCheck!",
            "This is your first post in our agricultural community. Share your farming experiences and connect with other farmers.",
            "user1",
            "System Admin"
        ));
        
        postsList.add(new Post(
            UUID.randomUUID().toString(),
            "Best Practices for Rice Farming",
            "Today I learned about the importance of proper water management in rice fields. Here are some tips...",
            "user2",
            "John Farmer"
        ));
        
        postsList.add(new Post(
            UUID.randomUUID().toString(),
            "New Equipment Arrived!",
            "Just got my new tractor! Excited to improve my farming efficiency. Any recommendations for attachments?",
            "user3",
            "Maria Santos"
        ));
        
        allPosts.setValue(postsList);
    }

    public LiveData<List<Post>> getAllPosts() {
        return allPosts;
    }

    public LiveData<Post> getCurrentPost() {
        return currentPost;
    }

    public void createPost(String title, String content, String category, String imageUrl) {
        if (currentUser == null) return;

        Post newPost = new Post(
            UUID.randomUUID().toString(),
            title,
            content,
            currentUser.getId(),
            currentUser.getName()
        );
        
        newPost.setCategory(category);
        newPost.setImageUrl(imageUrl);
        
        postsList.add(0, newPost); // Add to beginning of list
        allPosts.setValue(postsList);
    }

    public void updatePost(String postId, String title, String content, String category, String imageUrl) {
        for (int i = 0; i < postsList.size(); i++) {
            Post post = postsList.get(i);
            if (post.getId().equals(postId)) {
                post.setTitle(title);
                post.setContent(content);
                post.setCategory(category);
                post.setImageUrl(imageUrl);
                post.setUpdatedAt(new Date());
                
                allPosts.setValue(postsList);
                currentPost.setValue(post);
                break;
            }
        }
    }

    public void deletePost(String postId) {
        for (int i = 0; i < postsList.size(); i++) {
            if (postsList.get(i).getId().equals(postId)) {
                postsList.remove(i);
                allPosts.setValue(postsList);
                break;
            }
        }
    }

    public void likePost(String postId) {
        for (Post post : postsList) {
            if (post.getId().equals(postId)) {
                post.incrementLikes();
                allPosts.setValue(postsList);
                break;
            }
        }
    }

    public void unlikePost(String postId) {
        for (Post post : postsList) {
            if (post.getId().equals(postId)) {
                post.decrementLikes();
                allPosts.setValue(postsList);
                break;
            }
        }
    }

    public void getPostById(String postId) {
        for (Post post : postsList) {
            if (post.getId().equals(postId)) {
                currentPost.setValue(post);
                break;
            }
        }
    }

    public List<Post> getPostsByCategory(String category) {
        List<Post> filteredPosts = new ArrayList<>();
        for (Post post : postsList) {
            if (category == null || category.equals("All") || 
                (post.getCategory() != null && post.getCategory().equals(category))) {
                filteredPosts.add(post);
            }
        }
        return filteredPosts;
    }

    public List<Post> getPostsByAuthor(String authorId) {
        List<Post> authorPosts = new ArrayList<>();
        for (Post post : postsList) {
            if (post.getAuthorId().equals(authorId)) {
                authorPosts.add(post);
            }
        }
        return authorPosts;
    }

    public void searchPosts(String query) {
        List<Post> searchResults = new ArrayList<>();
        String lowercaseQuery = query.toLowerCase();
        
        for (Post post : postsList) {
            if (post.getTitle().toLowerCase().contains(lowercaseQuery) ||
                post.getContent().toLowerCase().contains(lowercaseQuery) ||
                post.getAuthorName().toLowerCase().contains(lowercaseQuery)) {
                searchResults.add(post);
            }
        }
        
        allPosts.setValue(searchResults);
    }

    public void refreshPosts() {
        allPosts.setValue(postsList);
    }
}
