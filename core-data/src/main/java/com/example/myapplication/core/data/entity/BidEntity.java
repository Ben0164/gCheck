package com.example.myapplication.core.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "bids",
        foreignKeys = @ForeignKey(entity = ProductEntity.class,
                parentColumns = "id",
                childColumns = "productId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("productId")})
public class BidEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long productId;
    private long buyerId;
    private double bidAmount;
    private long createdAt;
    // Buyer location for distance calculation
    private double buyerLatitude;
    private double buyerLongitude;

    @Ignore
    public BidEntity(long productId, long buyerId, double bidAmount, long createdAt) {
        this.productId = productId;
        this.buyerId = buyerId;
        this.bidAmount = bidAmount;
        this.createdAt = createdAt;
    }

    public BidEntity(long productId, long buyerId, double bidAmount, long createdAt, double buyerLatitude, double buyerLongitude) {
        this.productId = productId;
        this.buyerId = buyerId;
        this.bidAmount = bidAmount;
        this.createdAt = createdAt;
        this.buyerLatitude = buyerLatitude;
        this.buyerLongitude = buyerLongitude;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }
    public long getBuyerId() { return buyerId; }
    public void setBuyerId(long buyerId) { this.buyerId = buyerId; }
    public double getBidAmount() { return bidAmount; }
    public void setBidAmount(double bidAmount) { this.bidAmount = bidAmount; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public double getBuyerLatitude() { return buyerLatitude; }
    public void setBuyerLatitude(double buyerLatitude) { this.buyerLatitude = buyerLatitude; }
    public double getBuyerLongitude() { return buyerLongitude; }
    public void setBuyerLongitude(double buyerLongitude) { this.buyerLongitude = buyerLongitude; }
}
