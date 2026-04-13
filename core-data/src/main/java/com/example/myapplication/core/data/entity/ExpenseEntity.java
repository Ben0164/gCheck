package com.example.myapplication.core.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses",
        foreignKeys = @ForeignKey(entity = BatchEntity.class,
                parentColumns = "id",
                childColumns = "batchId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("batchId"), @Index("activityId")})
public class ExpenseEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long userId;
    private long batchId;
    private String phase;
    private String category;
    private String productName;
    private double quantity;
    private String unit;
    private double unitPrice;
    private double totalCost;
    private long createdAt;

    public static final String TYPE_GENERAL = "general";
    public static final String TYPE_LABOR = "labor";
    public static final String TYPE_HAULING_INTERNAL = "hauling_internal";

    private String expenseType = TYPE_GENERAL; 

    private String location;
    private double area;
    private String date;
    private String notes;
    private double wage;
    private int workers;
    private int days;

    private Long activityId; 
    private Long methodId;   

    private String laborType; 
    private double implicitCost; 

    public ExpenseEntity(long userId, long batchId, String phase, String category, 
                         String productName, double quantity, String unit, double unitPrice) {
        if (batchId <= 0) {
            throw new IllegalArgumentException("Invalid batchId: " + batchId);
        }
        this.userId = userId;
        this.batchId = batchId;
        this.phase = phase;
        this.category = category;
        this.productName = productName;
        this.quantity = quantity;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.totalCost = quantity * unitPrice;
        this.createdAt = System.currentTimeMillis();
        this.laborType = "paid"; 
        this.implicitCost = 0.0;
        this.expenseType = TYPE_GENERAL;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public long getBatchId() { return batchId; }
    public void setBatchId(long batchId) { 
        if (batchId <= 0) {
            throw new IllegalArgumentException("Invalid batchId: " + batchId);
        }
        this.batchId = batchId; 
    }
    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public double getArea() { return area; }
    public void setArea(double area) { this.area = area; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public double getWage() { return wage; }
    public void setWage(double wage) { this.wage = wage; }
    public int getWorkers() { return workers; }
    public void setWorkers(int workers) { this.workers = workers; }
    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public Long getMethodId() { return methodId; }
    public void setMethodId(Long methodId) { this.methodId = methodId; }
    public String getLaborType() { return laborType; }
    public void setLaborType(String laborType) { this.laborType = laborType; }
    public double getImplicitCost() { return implicitCost; }
    public void setImplicitCost(double implicitCost) { this.implicitCost = implicitCost; }
    public String getExpenseType() { return expenseType; }
    public void setExpenseType(String expenseType) { this.expenseType = expenseType; }
}
