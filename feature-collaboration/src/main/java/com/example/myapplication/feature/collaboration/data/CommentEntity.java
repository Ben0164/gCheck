package com.example.myapplication.feature.collaboration.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "community_comments",
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
            childColumns = "authorId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index("postId"),
        @Index("authorId"),
        @Index("parentCommentId")
    }
)
public class CommentEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private long postId;
    private String authorId;
    private String authorName;
    private String authorAvatar;
    private String content;
    private Long parentCommentId; // null for top-level comments
    private long likesCount;
    private long createdAt;
    private long updatedAt;

    public CommentEntity() {
        this.likesCount = 0;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
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

    public boolean isReply() {
        return parentCommentId != null;
    }
}
