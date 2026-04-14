package com.example.myapplication.model;

import java.util.Date;

public class Post {
    private String id;
    private String title;
    private String content;
    private String authorId;
    private String authorName;
    private Date createdAt;
    private Date updatedAt;
    private int likes;
    private int comments;
    private String imageUrl;
    private String category;
    private boolean isLiked;

    public Post() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.likes = 0;
        this.comments = 0;
        this.isLiked = false;
    }

    public Post(String id, String title, String content, String authorId, String authorName) {
        this();
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public void incrementLikes() {
        this.likes++;
        this.isLiked = true;
    }

    public void decrementLikes() {
        if (this.likes > 0) {
            this.likes--;
        }
        this.isLiked = false;
    }

    public void incrementComments() {
        this.comments++;
    }
}
