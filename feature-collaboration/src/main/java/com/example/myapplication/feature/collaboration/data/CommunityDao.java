package com.example.myapplication.feature.collaboration.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface CommunityDao {
    
    // Post operations
    @Insert
    long insertPost(PostEntity post);
    
    @Update
    void updatePost(PostEntity post);
    
    @Delete
    void deletePost(PostEntity post);
    
    @Query("SELECT * FROM community_posts ORDER BY createdAt DESC")
    LiveData<List<PostEntity>> getAllPosts();
    
    @Query("SELECT * FROM community_posts WHERE authorId = :authorId ORDER BY createdAt DESC")
    LiveData<List<PostEntity>> getPostsByAuthor(String authorId);
    
    @Query("SELECT * FROM community_posts WHERE id = :postId")
    PostEntity getPostById(long postId);
    
    @Query("UPDATE community_posts SET likesCount = likesCount + 1 WHERE id = :postId")
    void incrementPostLikes(long postId);
    
    @Query("UPDATE community_posts SET likesCount = likesCount - 1 WHERE id = :postId AND likesCount > 0")
    void decrementPostLikes(long postId);
    
    @Query("UPDATE community_posts SET commentsCount = commentsCount + 1 WHERE id = :postId")
    void incrementPostComments(long postId);
    
    @Query("UPDATE community_posts SET commentsCount = commentsCount - 1 WHERE id = :postId AND commentsCount > 0")
    void decrementPostComments(long postId);
    
    // Comment operations
    @Insert
    long insertComment(CommentEntity comment);
    
    @Update
    void updateComment(CommentEntity comment);
    
    @Delete
    void deleteComment(CommentEntity comment);
    
    @Query("SELECT * FROM community_comments WHERE postId = :postId AND parentCommentId IS NULL ORDER BY createdAt ASC")
    LiveData<List<CommentEntity>> getTopLevelCommentsByPost(long postId);
    
    @Query("SELECT * FROM community_comments WHERE parentCommentId = :parentCommentId ORDER BY createdAt ASC")
    LiveData<List<CommentEntity>> getRepliesByComment(long parentCommentId);
    
    @Query("SELECT * FROM community_comments WHERE postId = :postId ORDER BY createdAt ASC")
    LiveData<List<CommentEntity>> getAllCommentsByPost(long postId);
    
    @Query("SELECT * FROM community_comments WHERE id = :commentId")
    CommentEntity getCommentById(long commentId);
    
    @Query("UPDATE community_comments SET likesCount = likesCount + 1 WHERE id = :commentId")
    void incrementCommentLikes(long commentId);
    
    @Query("UPDATE community_comments SET likesCount = likesCount - 1 WHERE id = :commentId AND likesCount > 0")
    void decrementCommentLikes(long commentId);
    
    // Like operations
    @Insert
    long insertLike(LikeEntity like);
    
    @Delete
    void deleteLike(LikeEntity like);
    
    @Query("SELECT * FROM community_likes WHERE postId = :postId AND userId = :userId")
    LikeEntity getPostLike(long postId, String userId);
    
    @Query("SELECT * FROM community_likes WHERE commentId = :commentId AND userId = :userId")
    LikeEntity getCommentLike(long commentId, String userId);
    
    @Query("SELECT COUNT(*) FROM community_likes WHERE postId = :postId")
    int getPostLikesCount(long postId);
    
    @Query("SELECT COUNT(*) FROM community_likes WHERE commentId = :commentId")
    int getCommentLikesCount(long commentId);
    
    @Query("SELECT * FROM community_likes WHERE userId = :userId")
    LiveData<List<LikeEntity>> getLikesByUser(String userId);
    
    // User operations
    @Insert
    long insertUser(UserEntity user);
    
    @Update
    void updateUser(UserEntity user);
    
    @Delete
    void deleteUser(UserEntity user);
    
    @Query("SELECT * FROM community_users WHERE id = :userId")
    UserEntity getUserById(String userId);
    
    @Query("SELECT * FROM community_users WHERE email = :email")
    UserEntity getUserByEmail(String email);
    
    @Query("SELECT * FROM community_users ORDER BY joinedAt DESC")
    LiveData<List<UserEntity>> getAllUsers();
    
    // Transactional operations
    @Transaction
    default void togglePostLike(long postId, String userId) {
        LikeEntity existingLike = getPostLike(postId, userId);
        if (existingLike != null) {
            deleteLike(existingLike);
            decrementPostLikes(postId);
        } else {
            insertLike(new LikeEntity(postId, null, userId));
            incrementPostLikes(postId);
        }
    }
    
    @Transaction
    default void toggleCommentLike(long commentId, String userId) {
        LikeEntity existingLike = getCommentLike(commentId, userId);
        if (existingLike != null) {
            deleteLike(existingLike);
            decrementCommentLikes(commentId);
        } else {
            insertLike(new LikeEntity(null, commentId, userId));
            incrementCommentLikes(commentId);
        }
    }
    
    @Transaction
    default long createPostWithComment(PostEntity post, CommentEntity comment) {
        long postId = insertPost(post);
        comment.setPostId(postId);
        insertComment(comment);
        incrementPostComments(postId);
        return postId;
    }
}
