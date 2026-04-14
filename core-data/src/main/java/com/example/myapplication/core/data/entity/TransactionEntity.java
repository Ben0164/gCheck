package com.example.myapplication.core.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions",
        foreignKeys = {
                @ForeignKey(entity = ProductEntity.class,
                        parentColumns = "id",
                        childColumns = "productId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = UserEntity.class,
                        parentColumns = "id",
                        childColumns = "buyerId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("productId"), @Index("buyerId")})
public class TransactionEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long productId;
    private long buyerId;
    private long farmerId;
    private double finalSalePrice;
    private double quantity;
    private double totalAmount;
    private long transactionDate;
    private String transactionStatus; // COMPLETED, PENDING, CANCELLED
    private String paymentStatus; // PAID, PENDING, UNPAID

    public TransactionEntity(long productId, long buyerId, long farmerId, double finalSalePrice,
                           double quantity, long transactionDate) {
        this.productId = productId;
        this.buyerId = buyerId;
        this.farmerId = farmerId;
        this.finalSalePrice = finalSalePrice;
        this.quantity = quantity;
        this.totalAmount = finalSalePrice * quantity;
        this.transactionDate = transactionDate;
        this.transactionStatus = "COMPLETED";
        this.paymentStatus = "PENDING";
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }
    public long getBuyerId() { return buyerId; }
    public void setBuyerId(long buyerId) { this.buyerId = buyerId; }
    public long getFarmerId() { return farmerId; }
    public void setFarmerId(long farmerId) { this.farmerId = farmerId; }
    public double getFinalSalePrice() { return finalSalePrice; }
    public void setFinalSalePrice(double finalSalePrice) { this.finalSalePrice = finalSalePrice; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public long getTransactionDate() { return transactionDate; }
    public void setTransactionDate(long transactionDate) { this.transactionDate = transactionDate; }
    public String getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
