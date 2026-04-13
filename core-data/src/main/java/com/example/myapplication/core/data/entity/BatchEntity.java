package com.example.myapplication.core.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "batches")
public class BatchEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long userId;
    private String name;
    private long startDate;
    private long expectedHarvestDate;
    private boolean isCompleted = false;
    private long completedDate;
    
    // Hybrid Phase System fields
    private String manualPhase;
    private boolean isManualOverride = false;
    
    private double targetYield;
    private double actualYieldKg;

    public BatchEntity(long userId, String name, long startDate) {
        this.userId = userId;
        this.name = name;
        this.startDate = startDate;
        // Auto-calculate harvest = startDate + 120 days
        this.expectedHarvestDate = startDate + (120L * 24 * 60 * 60 * 1000);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getStartDate() { return startDate; }
    public void setStartDate(long startDate) { this.startDate = startDate; }

    public long getExpectedHarvestDate() { return expectedHarvestDate; }
    public void setExpectedHarvestDate(long expectedHarvestDate) { this.expectedHarvestDate = expectedHarvestDate; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { this.isCompleted = completed; }

    public long getCompletedDate() { return completedDate; }
    public void setCompletedDate(long completedDate) { this.completedDate = completedDate; }

    public String getManualPhase() { return manualPhase; }
    public void setManualPhase(String manualPhase) { this.manualPhase = manualPhase; }

    public boolean isManualOverride() { return isManualOverride; }
    public void setManualOverride(boolean manualOverride) { this.isManualOverride = manualOverride; }

    public double getTargetYield() { return targetYield; }
    public void setTargetYield(double targetYield) { this.targetYield = targetYield; }

    public double getActualYieldKg() { return actualYieldKg; }
    public void setActualYieldKg(double actualYieldKg) { this.actualYieldKg = actualYieldKg; }
}
