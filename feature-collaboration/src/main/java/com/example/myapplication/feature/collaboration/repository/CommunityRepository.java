package com.example.myapplication.feature.collaboration.repository;

import androidx.lifecycle.LiveData;
import java.util.List;

import com.example.myapplication.feature.collaboration.data.CommunityDataSource;
import com.example.myapplication.core.data.entity.PostEntity;
import com.example.myapplication.core.data.entity.CommentEntity;
import com.example.myapplication.core.data.entity.UserEntity;

/**
 * Repository for Community Collaboration system.
 * Follows clean architecture pattern by abstracting data source operations.
 * UI layer should only interact with this repository, never directly with data source.
 */
public class CommunityRepository {
    
    private final CommunityDataSource dataSource;
    private static CommunityRepository instance;
    
    private CommunityRepository(CommunityDataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public static synchronized CommunityRepository getInstance(CommunityDataSource dataSource) {
        if (instance == null) {
            instance = new CommunityRepository(dataSource);
        }
        return instance;
    }
    
    // Post operations
    public LiveData<List<PostEntity>> getAllPosts() {
        return dataSource.getAllPosts();
    }
    
    public LiveData<List<PostEntity>> getPostsByAuthor(long authorId) {
        return dataSource.getPostsByAuthor(authorId);
    }
    
    public LiveData<List<PostEntity>> getPostsByPhase(String phase) {
        return dataSource.getPostsByPhase(phase);
    }
    
    public LiveData<List<PostEntity>> getPostsByAudience(String audience) {
        return dataSource.getPostsByAudience(audience);
    }
    
    public PostEntity getPostById(long postId) {
        return dataSource.getPostById(postId);
    }
    
    public long createPost(PostEntity post) {
        // Validate post before creation
        if (!validatePost(post)) {
            return -1;
        }
        
        // Save image if exists
        if (post.hasImage() && post.getImagePath() != null) {
            String savedPath = dataSource.saveImageToLocal(post.getImagePath());
            if (savedPath != null) {
                post.setImagePath(savedPath);
            } else {
                return -1; // Failed to save image
            }
        }
        
        return dataSource.createPost(post);
    }
    
    public void updatePost(PostEntity post) {
        dataSource.updatePost(post);
    }
    
    public void deletePost(PostEntity post) {
        dataSource.deletePost(post);
    }
    
    // Post like operations
    public void togglePostLike(long postId, long userId) {
        dataSource.togglePostLike(postId, userId);
    }
    
    public boolean isPostLikedByUser(long postId, long userId) {
        return dataSource.isPostLikedByUser(postId, userId);
    }
    
    public LiveData<Integer> getPostLikesCount(long postId) {
        return dataSource.getPostLikesCount(postId);
    }
    
    // Comment operations
    public LiveData<List<CommentEntity>> getCommentsByPost(long postId) {
        return dataSource.getCommentsByPost(postId);
    }
    
    public LiveData<List<CommentEntity>> getTopLevelCommentsByPost(long postId) {
        return dataSource.getTopLevelCommentsByPost(postId);
    }
    
    public LiveData<List<CommentEntity>> getRepliesByComment(long parentCommentId) {
        return dataSource.getRepliesByComment(parentCommentId);
    }
    
    public CommentEntity getCommentById(long commentId) {
        return dataSource.getCommentById(commentId);
    }
    
    public long createComment(CommentEntity comment) {
        // Validate comment before creation
        if (!dataSource.isValidComment(comment.getContent())) {
            return -1;
        }
        
        return dataSource.createComment(comment);
    }
    
    public void updateComment(CommentEntity comment) {
        dataSource.updateComment(comment);
    }
    
    public void deleteComment(CommentEntity comment) {
        dataSource.deleteComment(comment);
    }
    
    // Comment like operations
    public void toggleCommentLike(long commentId, long userId) {
        dataSource.toggleCommentLike(commentId, userId);
    }
    
    public boolean isCommentLikedByUser(long commentId, long userId) {
        return dataSource.isCommentLikedByUser(commentId, userId);
    }
    
    public LiveData<Integer> getCommentLikesCount(long commentId) {
        return dataSource.getCommentLikesCount(commentId);
    }
    
    // User operations
    public UserEntity getUserById(long userId) {
        return dataSource.getUserById(userId);
    }
    
    public UserEntity getUserByEmail(String email) {
        return dataSource.getUserByEmail(email);
    }
    
    public LiveData<List<UserEntity>> getAllUsers() {
        return dataSource.getAllUsers();
    }
    
    public LiveData<List<UserEntity>> getUsersByRole(String role) {
        return dataSource.getUsersByRole(role);
    }
    
    public long createUser(UserEntity user) {
        return dataSource.createUser(user);
    }
    
    public void updateUser(UserEntity user) {
        dataSource.updateUser(user);
    }
    
    public void deleteUser(UserEntity user) {
        dataSource.deleteUser(user);
    }
    
    // Validation operations
    public boolean isValidImageFile(String filePath) {
        return dataSource.isValidImageFile(filePath);
    }
    
    public boolean isValidCaption(String caption) {
        return dataSource.isValidCaption(caption);
    }
    
    public boolean isValidComment(String content) {
        return dataSource.isValidComment(content);
    }
    
    // Search operations
    public LiveData<List<PostEntity>> searchPosts(String query) {
        return dataSource.searchPosts(query);
    }
    
    public LiveData<List<CommentEntity>> searchComments(String query) {
        return dataSource.searchComments(query);
    }
    
    // Statistics operations
    public LiveData<Integer> getTotalPostsCount() {
        return dataSource.getTotalPostsCount();
    }
    
    public LiveData<Integer> getTotalCommentsCount() {
        return dataSource.getTotalCommentsCount();
    }
    
    public LiveData<Integer> getTotalUsersCount() {
        return dataSource.getTotalUsersCount();
    }
    
    public LiveData<Integer> getPostsCountByAuthor(long authorId) {
        return dataSource.getPostsCountByAuthor(authorId);
    }
    
    // Helper methods
    private boolean validatePost(PostEntity post) {
        // Check if post has either caption or image
        if (!post.hasCaption() && !post.hasImage()) {
            return false;
        }
        
        // Validate caption if exists
        if (post.hasCaption() && !dataSource.isValidCaption(post.getCaption())) {
            return false;
        }
        
        // Validate image if exists
        if (post.hasImage() && !dataSource.isValidImageFile(post.getImagePath())) {
            return false;
        }
        
        return true;
    }
    
    // Post creation helper with validation
    public PostValidationResult validatePostForCreation(String caption, String imagePath, String audience) {
        PostValidationResult result = new PostValidationResult();
        
        // Check if at least one field is provided
        boolean hasCaption = caption != null && !caption.trim().isEmpty();
        boolean hasImage = imagePath != null && !imagePath.isEmpty();
        
        if (!hasCaption && !hasImage) {
            result.isValid = false;
            result.errorMessage = "Please add a caption or image";
            return result;
        }
        
        // Validate caption
        if (hasCaption && !isValidCaption(caption)) {
            result.isValid = false;
            result.errorMessage = "Caption is too long (max 1000 characters)";
            return result;
        }
        
        // Validate image
        if (hasImage && !isValidImageFile(imagePath)) {
            result.isValid = false;
            result.errorMessage = "Invalid image format. Supported formats: JPG, JPEG, PNG";
            return result;
        }
        
        result.isValid = true;
        return result;
    }
    
    // Comment creation helper with validation
    public CommentValidationResult validateCommentForCreation(String content) {
        CommentValidationResult result = new CommentValidationResult();
        
        if (!isValidComment(content)) {
            result.isValid = false;
            result.errorMessage = "Comment cannot be empty and must be less than 500 characters";
            return result;
        }
        
        result.isValid = true;
        return result;
    }
    
    // Validation result classes
    public static class PostValidationResult {
        public boolean isValid;
        public String errorMessage;
    }
    
    public static class CommentValidationResult {
        public boolean isValid;
        public String errorMessage;
    }
}
