package com.example.myapplication;

public class PostModel {
    private String authorName;
    private String title;
    private String description;
    private String date;

    public PostModel(String authorName, String title, String description, String date) {
        this.authorName = authorName;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public String getAuthorName() { return authorName; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
}
