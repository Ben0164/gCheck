package com.example.myapplication.core.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "posts",
    foreignKeys = {
        @ForeignKey(
            entity = UserEntity.class,
            parentColumns = "id",
            childColumns = "authorId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(value = {"authorId"}),
        @Index(value = {"createdAt"}),
        @Index(value = {"phase"})
    }
)
public class PostEntity {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private long authorId;
    private String caption;
    private String imagePath;
    private String phase;
    private String audience; // "public" or "advice"
    private boolean isVerified;
    private String cnnResult;
    private float confidence;
    private int likesCount;
    private int commentCount;
    private long createdAt;
    
    public PostEntity() {
        this.createdAt = System.currentTimeMillis();
        this.likesCount = 0;
        this.commentCount = 0;
        this.isVerified = false;
        this.confidence = 0f;
        this.audience = "public";
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }
    
    public String getCaption() {
        return caption;
    }
    
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public String getPhase() {
        return phase;
    }
    
    public void setPhase(String phase) {
        this.phase = phase;
    }
    
    public String getAudience() {
        return audience;
    }
    
    public void setAudience(String audience) {
        this.audience = audience;
    }
    
    public boolean isVerified() {
        return isVerified;
    }
    
    public void setVerified(boolean verified) {
        isVerified = verified;
    }
    
    public String getCnnResult() {
        return cnnResult;
    }
    
    public void setCnnResult(String cnnResult) {
        this.cnnResult = cnnResult;
    }
    
    public float getConfidence() {
        return confidence;
    }
    
    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }
    
    public int getLikesCount() {
        return likesCount;
    }
    
    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
    
    public int getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper methods
    public boolean hasImage() {
        return imagePath != null && !imagePath.isEmpty();
    }
    
    public boolean hasCaption() {
        return caption != null && !caption.trim().isEmpty();
    }
    
    public boolean isValid() {
        return hasImage() || hasCaption();
    }
}
