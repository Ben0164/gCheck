package com.example.myapplication.feature.collaboration.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "community_likes",
    foreignKeys = {
        @ForeignKey(
            entity = PostEntity.class,
            parentColumns = "id",
            childColumns = "postId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = CommentEntity.class,
            parentColumns = "id",
            childColumns = "commentId",
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
        @Index("postId"),
        @Index("commentId"),
        @Index("userId"),
        @Index(value = {"postId", "userId"}, unique = true),
        @Index(value = {"commentId", "userId"}, unique = true)
    }
)
public class LikeEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private Long postId; // null if this is a comment like
    private Long commentId; // null if this is a post like
    private String userId;
    private long createdAt;

    public LikeEntity() {
        this.createdAt = System.currentTimeMillis();
    }

    public LikeEntity(Long postId, Long commentId, String userId) {
        this();
        this.postId = postId;
        this.commentId = commentId;
        this.userId = userId;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isPostLike() {
        return postId != null && commentId == null;
    }

    public boolean isCommentLike() {
        return commentId != null && postId == null;
    }
}
