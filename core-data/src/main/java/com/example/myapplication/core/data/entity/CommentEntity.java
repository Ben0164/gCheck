package com.example.myapplication.core.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "comments",
    foreignKeys = {
        @ForeignKey(
            entity = PostEntity.class,
            parentColumns = "id",
            childColumns = "postId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = UserEntity.class,
            parentColumns = "id",
            childColumns = "userId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(value = {"postId"}),
        @Index(value = {"userId"}),
        @Index(value = {"parentCommentId"}),
        @Index(value = {"createdAt"})
    }
)
public class CommentEntity {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private long postId;
    private long userId;
    private String content;
    private long createdAt;
    private int likesCount;
    private Long parentCommentId; // null for top-level comments
    
    public CommentEntity() {
        this.createdAt = System.currentTimeMillis();
        this.likesCount = 0;
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getPostId() {
        return postId;
    }
    
    public void setPostId(long postId) {
        this.postId = postId;
    }
    
    public long getUserId() {
        return userId;
    }
    
    public void setUserId(long userId) {
        this.userId = userId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public int getLikesCount() {
        return likesCount;
    }
    
    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
    
    public Long getParentCommentId() {
        return parentCommentId;
    }
    
    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
    
    // Helper methods
    public boolean isReply() {
        return parentCommentId != null;
    }
    
    public boolean isTopLevel() {
        return parentCommentId == null;
    }
    
    public boolean isValid() {
        return content != null && !content.trim().isEmpty() && content.length() <= 500;
    }
}
