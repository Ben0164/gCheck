package com.example.myapplication.core.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class ProductEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long farmerId;
    private String farmerName;
    private double quantity;
    private String grade;
    private double price; // Floor price
    private double buyNowPrice;
    private long analysisId;
    private long createdAt;
    private long deadline;
    private double latitude;
    private double longitude;
    private boolean isSold;
    private long batchId;
    // Posting type: "AI_VERIFIED" or "DEMO"
    private String postType;
    private boolean isVerified;
    private String verificationMethod; // nullable, e.g., "Self-Declared", "Third-Party"
    // Bidding system fields
    private double currentHighestBid;
    private long currentHighestBidderId;
    private String listingStatus; // ACTIVE, SOLD, EXPIRED
    private double finalSalePrice;
    private long winningBuyerId;

    public ProductEntity(long farmerId, String farmerName, double quantity, String grade, 
                         double price, double buyNowPrice, long analysisId, long createdAt, 
                         long deadline, double latitude, double longitude, long batchId) {
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.quantity = quantity;
        this.grade = grade;
        this.price = price;
        this.buyNowPrice = buyNowPrice;
        this.analysisId = analysisId;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isSold = false;
        this.batchId = batchId;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getFarmerId() { return farmerId; }
    public void setFarmerId(long farmerId) { this.farmerId = farmerId; }
    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getBuyNowPrice() { return buyNowPrice; }
    public void setBuyNowPrice(double buyNowPrice) { this.buyNowPrice = buyNowPrice; }
    public long getAnalysisId() { return analysisId; }
    public void setAnalysisId(long analysisId) { this.analysisId = analysisId; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getDeadline() { return deadline; }
    public void setDeadline(long deadline) { this.deadline = deadline; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public boolean isSold() { return isSold; }
    public void setSold(boolean sold) { isSold = sold; }
    public long getBatchId() { return batchId; }
    public void setBatchId(long batchId) { this.batchId = batchId; }
    public String getPostType() { return postType; }
    public void setPostType(String postType) { this.postType = postType; }
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
    public String getVerificationMethod() { return verificationMethod; }
    public void setVerificationMethod(String verificationMethod) { this.verificationMethod = verificationMethod; }
    public double getCurrentHighestBid() { return currentHighestBid; }
    public void setCurrentHighestBid(double currentHighestBid) { this.currentHighestBid = currentHighestBid; }
    public long getCurrentHighestBidderId() { return currentHighestBidderId; }
    public void setCurrentHighestBidderId(long currentHighestBidderId) { this.currentHighestBidderId = currentHighestBidderId; }
    public String getListingStatus() { return listingStatus; }
    public void setListingStatus(String listingStatus) { this.listingStatus = listingStatus; }
    public double getFinalSalePrice() { return finalSalePrice; }
    public void setFinalSalePrice(double finalSalePrice) { this.finalSalePrice = finalSalePrice; }
    public long getWinningBuyerId() { return winningBuyerId; }
    public void setWinningBuyerId(long winningBuyerId) { this.winningBuyerId = winningBuyerId; }
}
