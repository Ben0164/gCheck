package com.example.myapplication.feature.collaboration.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

@Entity(
    tableName = "users",
    indices = {
        @Index(value = {"email"}, unique = true),
        @Index(value = {"name"})
    }
)
public class UserEntity {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String name;
    private String email;
    private String avatarPath;
    private String role; // "farmer" or "buyer"
    private long createdAt;
    
    public UserEntity() {
        this.createdAt = System.currentTimeMillis();
        this.role = "farmer";
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
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
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper methods
    public boolean hasAvatar() {
        return avatarPath != null && !avatarPath.isEmpty();
    }
    
    public boolean isFarmer() {
        return "farmer".equals(role);
    }
    
    public boolean isBuyer() {
        return "buyer".equals(role);
    }
}
