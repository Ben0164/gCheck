package com.example.myapplication.core.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
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

    public BidEntity(long productId, long buyerId, double bidAmount, long createdAt) {
        this.productId = productId;
        this.buyerId = buyerId;
        this.bidAmount = bidAmount;
        this.createdAt = createdAt;
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
}
