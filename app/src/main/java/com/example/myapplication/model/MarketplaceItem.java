package com.example.myapplication.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MarketplaceItem {
    private String id;
    private String title;
    private String description;
    private String sellerId;
    private String sellerName;
    private double startingPrice;
    private double currentBid;
    private String highestBidderId;
    private String highestBidderName;
    private Date endTime;
    private List<String> imageUrls;
    private String category;
    private String location;
    private int quantity;
    private String unit;
    private boolean isActive;
    private Date createdAt;
    private List<Bid> bidHistory;

    public MarketplaceItem() {
        this.imageUrls = new ArrayList<>();
        this.bidHistory = new ArrayList<>();
        this.isActive = true;
        this.createdAt = new Date();
    }

    public MarketplaceItem(String id, String title, String description, String sellerId, String sellerName, double startingPrice, Date endTime) {
        this();
        this.id = id;
        this.title = title;
        this.description = description;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.startingPrice = startingPrice;
        this.currentBid = startingPrice;
        this.endTime = endTime;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
    }

    public double getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(double currentBid) {
        this.currentBid = currentBid;
    }

    public String getHighestBidderId() {
        return highestBidderId;
    }

    public void setHighestBidderId(String highestBidderId) {
        this.highestBidderId = highestBidderId;
    }

    public String getHighestBidderName() {
        return highestBidderName;
    }

    public void setHighestBidderName(String highestBidderName) {
        this.highestBidderName = highestBidderName;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<Bid> getBidHistory() {
        return bidHistory;
    }

    public void setBidHistory(List<Bid> bidHistory) {
        this.bidHistory = bidHistory;
    }

    public void addBid(Bid bid) {
        this.bidHistory.add(bid);
        this.currentBid = bid.getAmount();
        this.highestBidderId = bid.getBidderId();
        this.highestBidderName = bid.getBidderName();
    }

    public boolean isAuctionEnded() {
        return new Date().after(endTime);
    }

    public long getTimeRemaining() {
        return endTime.getTime() - System.currentTimeMillis();
    }

    public static class Bid {
        private String id;
        private String bidderId;
        private String bidderName;
        private double amount;
        private Date timestamp;

        public Bid() {
            this.timestamp = new Date();
        }

        public Bid(String id, String bidderId, String bidderName, double amount) {
            this();
            this.id = id;
            this.bidderId = bidderId;
            this.bidderName = bidderName;
            this.amount = amount;
        }

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBidderId() {
            return bidderId;
        }

        public void setBidderId(String bidderId) {
            this.bidderId = bidderId;
        }

        public String getBidderName() {
            return bidderName;
        }

        public void setBidderName(String bidderName) {
            this.bidderName = bidderName;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }
    }
}
