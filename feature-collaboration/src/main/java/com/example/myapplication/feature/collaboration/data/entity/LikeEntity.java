package com.example.myapplication.feature.collaboration.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "likes",
    foreignKeys = {
        @ForeignKey(
            entity = UserEntity.class,
            parentColumns = "id",
            childColumns = "userId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(value = {"userId", "postId"}, unique = true),
        @Index(value = {"userId", "commentId"}, unique = true),
        @Index(value = {"postId"}),
        @Index(value = {"commentId"})
    }
)
public class LikeEntity {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private long userId;
    private Long postId; // null if it's a comment like
    private Long commentId; // null if it's a post like
    private long createdAt;
    
    public LikeEntity() {
        this.createdAt = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getUserId() {
        return userId;
    }
    
    public void setUserId(long userId) {
        this.userId = userId;
    }
    
    public Long getPostId() {
        return postId;
    }
    
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    
    public Long getCommentId() {
        return commentId;
    }
    
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper methods
    public boolean isPostLike() {
        return postId != null && commentId == null;
    }
    
    public boolean isCommentLike() {
        return commentId != null && postId == null;
    }
}
