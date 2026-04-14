package com.example.myapplication.feature.collaboration;

public class PostModel {
    private String authorName;
    private String title;
    private String description;
    private String date;
    private String grade;
    private double moisture;
    private String variety;
    private double quantity;
    private double floorPrice;
    private double buyNowPrice;
    private long deadline;
    private double latitude;
    private double longitude;
    private boolean isBiddingEnabled;
    private String status;
    private boolean isVerifiedByAI;
    private String imageUrl;
    private int likes = 0;
    private int comments = 0;

    public PostModel(String authorName, String title, String description, String date) {
        this.authorName = authorName;
        this.title = title;
        this.description = description;
        this.date = date;
        this.status = "Pending";
    }

    public String getAuthorName() { return authorName; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getGrade() { return grade; }
    public double getMoisture() { return moisture; }
    public String getStatus() { return status; }
    public String getImageUrl() { return imageUrl; }
    public double getAskingPrice() { return buyNowPrice > 0 ? buyNowPrice : floorPrice; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public int getLikes() { return likes; }
    public int getComments() { return comments; }
    public double calculateDistance(double userLat, double userLon) {
        float[] results = new float[1];
        android.location.Location.distanceBetween(latitude, longitude, userLat, userLon, results);
        return results[0] / 1000.0;
    }
}
