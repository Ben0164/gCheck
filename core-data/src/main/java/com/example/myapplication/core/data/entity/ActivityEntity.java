package com.example.myapplication.core.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "activities")
public class ActivityEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String phase;
    private String name;

    public ActivityEntity(String phase, String name) {
        this.phase = phase;
        this.name = name;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
