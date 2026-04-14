package com.example.myapplication.feature.collaboration.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "community_posts",
    foreignKeys = @ForeignKey(
        entity = UserEntity.class,
        parentColumns = "id",
        childColumns = "authorId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = @Index("authorId")
)
public class PostEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String authorId;
    private String authorName;
    private String authorAvatar;
    private String caption;
    private String imagePath;
    private String phaseTag;
    private String audienceTag;
    private boolean isVerifiedByCNN;
    private long likesCount;
    private long commentsCount;
    private long createdAt;
    private long updatedAt;

    public PostEntity() {
        this.likesCount = 0;
        this.commentsCount = 0;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.audienceTag = "Public";
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
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

    public String getPhaseTag() {
        return phaseTag;
    }

    public void setPhaseTag(String phaseTag) {
        this.phaseTag = phaseTag;
    }

    public String getAudienceTag() {
        return audienceTag;
    }

    public void setAudienceTag(String audienceTag) {
        this.audienceTag = audienceTag;
    }

    public boolean isVerifiedByCNN() {
        return isVerifiedByCNN;
    }

    public void setVerifiedByCNN(boolean verifiedByCNN) {
        isVerifiedByCNN = verifiedByCNN;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void incrementLikesCount() {
        this.likesCount++;
        this.updatedAt = System.currentTimeMillis();
    }

    public void decrementLikesCount() {
        if (this.likesCount > 0) {
            this.likesCount--;
        }
        this.updatedAt = System.currentTimeMillis();
    }

    public void incrementCommentsCount() {
        this.commentsCount++;
        this.updatedAt = System.currentTimeMillis();
    }

    public void decrementCommentsCount() {
        if (this.commentsCount > 0) {
            this.commentsCount--;
        }
        this.updatedAt = System.currentTimeMillis();
    }
}
