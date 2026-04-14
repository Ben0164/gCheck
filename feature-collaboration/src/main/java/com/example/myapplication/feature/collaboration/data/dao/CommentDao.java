package com.example.myapplication.feature.collaboration.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;

import com.example.myapplication.feature.collaboration.data.entity.CommentEntity;

@Dao
public interface CommentDao {
    
    @Insert
    long insertComment(CommentEntity comment);
    
    @Update
    void updateComment(CommentEntity comment);
    
    @Delete
    void deleteComment(CommentEntity comment);
    
    @Query("SELECT * FROM comments WHERE postId = :postId AND parentCommentId IS NULL ORDER BY createdAt ASC")
    LiveData<List<CommentEntity>> getTopLevelCommentsByPost(long postId);
    
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    LiveData<List<CommentEntity>> getAllCommentsByPost(long postId);
    
    @Query("SELECT * FROM comments WHERE parentCommentId = :parentCommentId ORDER BY createdAt ASC")
    LiveData<List<CommentEntity>> getRepliesByComment(long parentCommentId);
    
    @Query("SELECT * FROM comments WHERE id = :commentId")
    CommentEntity getCommentById(long commentId);
    
    @Query("SELECT * FROM comments WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<CommentEntity>> getCommentsByUser(long userId);
    
    @Query("UPDATE comments SET likesCount = likesCount + 1 WHERE id = :commentId")
    void incrementLikesCount(long commentId);
    
    @Query("UPDATE comments SET likesCount = likesCount - 1 WHERE id = :commentId AND likesCount > 0")
    void decrementLikesCount(long commentId);
    
    @Query("SELECT COUNT(*) FROM comments WHERE postId = :postId")
    LiveData<Integer> getCommentCountByPost(long postId);
    
    @Query("SELECT COUNT(*) FROM comments WHERE parentCommentId = :parentCommentId")
    LiveData<Integer> getReplyCountByComment(long parentCommentId);
    
    @Query("DELETE FROM comments WHERE postId = :postId")
    void deleteCommentsByPost(long postId);
}
