package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class PostRepository {
    private static List<PostModel> posts = new ArrayList<>();

    static {
        // Sample data
        posts.add(new PostModel("John Dave Corpuz", "Good Quality", "Low moisture levels detected in Bataan.", "2023-10-25"));
        posts.add(new PostModel("Aeron John G. Ramos", "Bumper Harvest", "Our Jasmine rice is looking great this season.", "2023-10-20"));
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
