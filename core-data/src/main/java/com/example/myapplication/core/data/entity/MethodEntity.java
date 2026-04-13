package com.example.myapplication.core.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "methods")
public class MethodEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long activityId;
    private String name;

    public MethodEntity(long activityId, String name) {
        this.activityId = activityId;
        this.name = name;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getActivityId() { return activityId; }
    public void setActivityId(long activityId) { this.activityId = activityId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
