package com.example.myapplication.core.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "analysis_results")
public class AnalysisEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private double moisture;
    private int goodPercentage;
    private int badPercentage;
    private String grade;
    private double price;
    private long createdAt;

    public AnalysisEntity(double moisture,
                           int goodPercentage,
                           int badPercentage,
                           String grade,
                           double price,
                           long createdAt) {
        this.moisture = moisture;
        this.goodPercentage = goodPercentage;
        this.badPercentage = badPercentage;
        this.grade = grade;
        this.price = price;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getMoisture() {
        return moisture;
    }

    public int getGoodPercentage() {
        return goodPercentage;
    }

    public int getBadPercentage() {
        return badPercentage;
    }

    public String getGrade() {
        return grade;
    }

    public double getPrice() {
        return price;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
