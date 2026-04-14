package com.example.myapplication.feature.collaboration.data;

import java.util.List;
import androidx.lifecycle.LiveData;

import com.example.myapplication.core.data.entity.PostEntity;
import com.example.myapplication.core.data.entity.CommentEntity;
import com.example.myapplication.core.data.entity.UserEntity;
import com.example.myapplication.core.data.entity.LikeEntity;

/**
 * Abstract data source interface for Community Collaboration system.
 * This allows for future migration to Supabase without changing the UI layer.
 * Currently implemented by RoomCommunityDataSource for local storage.
 */
public interface CommunityDataSource {
    
    // Post operations
    LiveData<List<PostEntity>> getAllPosts();
    LiveData<List<PostEntity>> getPostsByAuthor(long authorId);
    LiveData<List<PostEntity>> getPostsByPhase(String phase);
    LiveData<List<PostEntity>> getPostsByAudience(String audience);
    PostEntity getPostById(long postId);
    long createPost(PostEntity post);
    void updatePost(PostEntity post);
    void deletePost(PostEntity post);
    
    // Post like operations
    void togglePostLike(long postId, long userId);
    boolean isPostLikedByUser(long postId, long userId);
    LiveData<Integer> getPostLikesCount(long postId);
    
    // Comment operations
    LiveData<List<CommentEntity>> getCommentsByPost(long postId);
    LiveData<List<CommentEntity>> getTopLevelCommentsByPost(long postId);
    LiveData<List<CommentEntity>> getRepliesByComment(long parentCommentId);
    CommentEntity getCommentById(long commentId);
    long createComment(CommentEntity comment);
    void updateComment(CommentEntity comment);
    void deleteComment(CommentEntity comment);
    
    // Comment like operations
    void toggleCommentLike(long commentId, long userId);
    boolean isCommentLikedByUser(long commentId, long userId);
    LiveData<Integer> getCommentLikesCount(long commentId);
    
    // User operations
    UserEntity getUserById(long userId);
    UserEntity getUserByEmail(String email);
    LiveData<List<UserEntity>> getAllUsers();
    LiveData<List<UserEntity>> getUsersByRole(String role);
    long createUser(UserEntity user);
    void updateUser(UserEntity user);
    void deleteUser(UserEntity user);
    
    // Validation operations
    boolean isValidImageFile(String filePath);
    boolean isValidCaption(String caption);
    boolean isValidComment(String content);
    
    // Utility operations
    String saveImageToLocal(String imagePath);
    void deleteImageFromLocal(String imagePath);
    List<String> getSupportedImageFormats();
    
    // Search operations
    LiveData<List<PostEntity>> searchPosts(String query);
    LiveData<List<CommentEntity>> searchComments(String query);
    
    // Statistics operations
    LiveData<Integer> getTotalPostsCount();
    LiveData<Integer> getTotalCommentsCount();
    LiveData<Integer> getTotalUsersCount();
    LiveData<Integer> getPostsCountByAuthor(long authorId);
}
