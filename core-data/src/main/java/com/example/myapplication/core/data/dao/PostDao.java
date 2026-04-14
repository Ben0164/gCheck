package com.example.myapplication.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;

import com.example.myapplication.core.data.entity.PostEntity;

@Dao
public interface PostDao {
    
    @Insert
    long insertPost(PostEntity post);
    
    @Update
    void updatePost(PostEntity post);
    
    @Delete
    void deletePost(PostEntity post);
    
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    LiveData<List<PostEntity>> getAllPosts();
    
    @Query("SELECT * FROM posts WHERE authorId = :authorId ORDER BY createdAt DESC")
    LiveData<List<PostEntity>> getPostsByAuthor(long authorId);
    
    @Query("SELECT * FROM posts WHERE phase = :phase ORDER BY createdAt DESC")
    LiveData<List<PostEntity>> getPostsByPhase(String phase);
    
    @Query("SELECT * FROM posts WHERE audience = :audience ORDER BY createdAt DESC")
    LiveData<List<PostEntity>> getPostsByAudience(String audience);
    
    @Query("SELECT * FROM posts WHERE id = :postId")
    PostEntity getPostById(long postId);
    
    @Query("UPDATE posts SET likesCount = likesCount + 1 WHERE id = :postId")
    void incrementLikesCount(long postId);
    
    @Query("UPDATE posts SET likesCount = likesCount - 1 WHERE id = :postId AND likesCount > 0")
    void decrementLikesCount(long postId);
    
    @Query("UPDATE posts SET commentCount = commentCount + 1 WHERE id = :postId")
    void incrementCommentCount(long postId);
    
    @Query("UPDATE posts SET commentCount = commentCount - 1 WHERE id = :postId AND commentCount > 0")
    void decrementCommentCount(long postId);
    
    @Query("SELECT COUNT(*) FROM posts")
    LiveData<Integer> getPostsCount();
    
    @Query("SELECT COUNT(*) FROM posts WHERE authorId = :authorId")
    LiveData<Integer> getPostsCountByAuthor(long authorId);
}
