package com.example.myapplication.feature.collaboration;

public class Message {
    private String title;
    private String description;
    private String timestamp;
    private int color;
    private boolean isRead;

    public Message(String title, String description, String timestamp, int color) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.color = color;
        this.isRead = false;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTimestamp() { return timestamp; }
    public int getColor() { return color; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
