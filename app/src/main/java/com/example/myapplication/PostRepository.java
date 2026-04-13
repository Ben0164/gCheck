package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class PostRepository {
    private static List<PostModel> posts = new ArrayList<>();

    static {
        // Mock data removed for production stabilization
    }

    public static List<PostModel> getAllPosts() {
        return posts;
    }

    public static List<PostModel> getUserPosts(String userName) {
        List<PostModel> userPosts = new ArrayList<>();
        for (PostModel post : posts) {
            if (post.getAuthorName().equals(userName)) {
                userPosts.add(post);
            }
        }
        return userPosts;
    }

    public static void addPost(PostModel post) {
        posts.add(0, post);
    }
}
