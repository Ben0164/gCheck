package com.example.myapplication.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;

import com.example.myapplication.core.data.entity.LikeEntity;

@Dao
public interface LikeDao {
    
    @Insert
    long insertLike(LikeEntity like);
    
    @Delete
    void deleteLike(LikeEntity like);
    
    @Query("SELECT * FROM likes WHERE userId = :userId AND postId = :postId")
    LikeEntity getPostLike(long userId, long postId);
    
    @Query("SELECT * FROM likes WHERE userId = :userId AND commentId = :commentId")
    LikeEntity getCommentLike(long userId, long commentId);
    
    @Query("SELECT COUNT(*) FROM likes WHERE postId = :postId")
    LiveData<Integer> getPostLikesCount(long postId);
    
    @Query("SELECT COUNT(*) FROM likes WHERE commentId = :commentId")
    LiveData<Integer> getCommentLikesCount(long commentId);
    
    @Query("SELECT * FROM likes WHERE postId = :postId")
    LiveData<List<LikeEntity>> getPostLikes(long postId);
    
    @Query("SELECT * FROM likes WHERE commentId = :commentId")
    LiveData<List<LikeEntity>> getCommentLikes(long commentId);
    
    @Query("SELECT * FROM likes WHERE userId = :userId")
    LiveData<List<LikeEntity>> getLikesByUser(long userId);
    
    @Query("DELETE FROM likes WHERE postId = :postId")
    void deleteLikesByPost(long postId);
    
    @Query("DELETE FROM likes WHERE commentId = :commentId")
    void deleteLikesByComment(long commentId);
    
    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE userId = :userId AND postId = :postId)")
    boolean isPostLikedByUser(long userId, long postId);
    
    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE userId = :userId AND commentId = :commentId)")
    boolean isCommentLikedByUser(long userId, long commentId);
}
