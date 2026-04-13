package com.example.myapplication;

public class PostModel {
    private String authorName;
    private String title;
    private String description;
    private String date;
    
    // Extended Fields
    private String grade;
    private double moisture;
    private String variety;
    private double quantity;
    private double floorPrice; // Added for bidding
    private double buyNowPrice; // Added for bidding
    private long deadline; // Added for bidding (timestamp)
    private double latitude;
    private double longitude;
    private boolean isBiddingEnabled;
    private String status; // Pending, Inspection Scheduled, Verified, Sold
    private boolean isVerifiedByAI;
    private String imageUrl;

    // Simple Constructor
    public PostModel(String authorName, String title, String description, String date) {
        this.authorName = authorName;
        this.title = title;
        this.description = description;
        this.date = date;
        this.status = "Pending";
    }

    // Full Constructor
    public PostModel(String authorName, String title, String description, String date, 
                     String grade, double moisture, String variety, double quantity, 
                     double floorPrice, double buyNowPrice, long deadline,
                     double latitude, double longitude, 
                     boolean isBiddingEnabled, String status, boolean isVerifiedByAI) {
        this.authorName = authorName;
        this.title = title;
        this.description = description;
        this.date = date;
        this.grade = grade;
        this.moisture = moisture;
        this.variety = variety;
        this.quantity = quantity;
        this.floorPrice = floorPrice;
        this.buyNowPrice = buyNowPrice;
        this.deadline = deadline;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isBiddingEnabled = isBiddingEnabled;
        this.status = status;
        this.isVerifiedByAI = isVerifiedByAI;
    }

    // Getters
    public String getAuthorName() { return authorName; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getGrade() { return grade; }
    public double getMoisture() { return moisture; }
    public String getVariety() { return variety; }
    public double getQuantity() { return quantity; }
    public double getFloorPrice() { return floorPrice; }
    public double getBuyNowPrice() { return buyNowPrice; }
    public long getDeadline() { return deadline; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isBiddingEnabled() { return isBiddingEnabled; }
    public String getStatus() { return status; }
    public boolean isVerifiedByAI() { return isVerifiedByAI; }
    public String getImageUrl() { return imageUrl; }

    public double getAskingPrice() {
        return buyNowPrice > 0 ? buyNowPrice : floorPrice;
    }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double calculateDistance(double userLat, double userLon) {
        float[] results = new float[1];
        android.location.Location.distanceBetween(latitude, longitude, userLat, userLon, results);
        return results[0] / 1000.0; // Return in km
    }
}
