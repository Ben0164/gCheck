package com.example.myapplication.feature.collaboration.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "community_users")
public class UserEntity {
    @PrimaryKey
    private String id;
    
    private String name;
    private String email;
    private String avatarPath;
    private String farmName;
    private String location;
    private long joinedAt;
    private boolean isActive;

    public UserEntity() {
        this.joinedAt = System.currentTimeMillis();
        this.isActive = true;
    }

    public UserEntity(String id, String name, String email) {
        this();
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(long joinedAt) {
        this.joinedAt = joinedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
