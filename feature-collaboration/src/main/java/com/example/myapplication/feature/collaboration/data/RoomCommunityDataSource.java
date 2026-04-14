package com.example.myapplication.feature.collaboration.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import androidx.lifecycle.LiveData;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.myapplication.core.data.entity.PostEntity;
import com.example.myapplication.core.data.entity.CommentEntity;
import com.example.myapplication.core.data.entity.UserEntity;
import com.example.myapplication.core.data.entity.LikeEntity;
import com.example.myapplication.core.data.dao.PostDao;
import com.example.myapplication.core.data.dao.CommentDao;
import com.example.myapplication.core.data.dao.LikeDao;
import com.example.myapplication.core.data.dao.UserDao;

/**
 * Room implementation of CommunityDataSource.
 * Handles all local database operations for the Community Collaboration system.
 */
public class RoomCommunityDataSource implements CommunityDataSource {
    
    private final PostDao postDao;
    private final CommentDao commentDao;
    private final LikeDao likeDao;
    private final UserDao userDao;
    private final Context context;
    
    public RoomCommunityDataSource(Context context, PostDao postDao, CommentDao commentDao, 
                                   LikeDao likeDao, UserDao userDao) {
        this.context = context;
        this.postDao = postDao;
        this.commentDao = commentDao;
        this.likeDao = likeDao;
        this.userDao = userDao;
    }
    
    // Post operations
    @Override
    public LiveData<List<PostEntity>> getAllPosts() {
        return postDao.getAllPosts();
    }
    
    @Override
    public LiveData<List<PostEntity>> getPostsByAuthor(long authorId) {
        return postDao.getPostsByAuthor(authorId);
    }
    
    @Override
    public LiveData<List<PostEntity>> getPostsByPhase(String phase) {
        return postDao.getPostsByPhase(phase);
    }
    
    @Override
    public LiveData<List<PostEntity>> getPostsByAudience(String audience) {
        return postDao.getPostsByAudience(audience);
    }
    
    @Override
    public PostEntity getPostById(long postId) {
        return postDao.getPostById(postId);
    }
    
    @Override
    public long createPost(PostEntity post) {
        return postDao.insertPost(post);
    }
    
    @Override
    public void updatePost(PostEntity post) {
        postDao.updatePost(post);
    }
    
    @Override
    public void deletePost(PostEntity post) {
        // Delete associated likes and comments first
        likeDao.deleteLikesByPost(post.getId());
        commentDao.deleteCommentsByPost(post.getId());
        
        // Delete image if exists
        if (post.hasImage()) {
            deleteImageFromLocal(post.getImagePath());
        }
        
        postDao.deletePost(post);
    }
    
    // Post like operations
    @Override
    public void togglePostLike(long postId, long userId) {
        LikeEntity existingLike = likeDao.getPostLike(userId, postId);
        if (existingLike != null) {
            // Unlike
            likeDao.deleteLike(existingLike);
            postDao.decrementLikesCount(postId);
        } else {
            // Like
            LikeEntity newLike = new LikeEntity();
            newLike.setUserId(userId);
            newLike.setPostId(postId);
            likeDao.insertLike(newLike);
            postDao.incrementLikesCount(postId);
        }
    }
    
    @Override
    public boolean isPostLikedByUser(long postId, long userId) {
        return likeDao.isPostLikedByUser(userId, postId);
    }
    
    @Override
    public LiveData<Integer> getPostLikesCount(long postId) {
        return likeDao.getPostLikesCount(postId);
    }
    
    // Comment operations
    @Override
    public LiveData<List<CommentEntity>> getCommentsByPost(long postId) {
        return commentDao.getAllCommentsByPost(postId);
    }
    
    @Override
    public LiveData<List<CommentEntity>> getTopLevelCommentsByPost(long postId) {
        return commentDao.getTopLevelCommentsByPost(postId);
    }
    
    @Override
    public LiveData<List<CommentEntity>> getRepliesByComment(long parentCommentId) {
        return commentDao.getRepliesByComment(parentCommentId);
    }
    
    @Override
    public CommentEntity getCommentById(long commentId) {
        return commentDao.getCommentById(commentId);
    }
    
    @Override
    public long createComment(CommentEntity comment) {
        long commentId = commentDao.insertComment(comment);
        // Update post comment count
        postDao.incrementCommentCount(comment.getPostId());
        return commentId;
    }
    
    @Override
    public void updateComment(CommentEntity comment) {
        commentDao.updateComment(comment);
    }
    
    @Override
    public void deleteComment(CommentEntity comment) {
        // Delete associated likes first
        likeDao.deleteLikesByComment(comment.getId());
        
        // Update post comment count
        postDao.decrementCommentCount(comment.getPostId());
        
        commentDao.deleteComment(comment);
    }
    
    // Comment like operations
    @Override
    public void toggleCommentLike(long commentId, long userId) {
        LikeEntity existingLike = likeDao.getCommentLike(userId, commentId);
        if (existingLike != null) {
            // Unlike
            likeDao.deleteLike(existingLike);
            commentDao.decrementLikesCount(commentId);
        } else {
            // Like
            LikeEntity newLike = new LikeEntity();
            newLike.setUserId(userId);
            newLike.setCommentId(commentId);
            likeDao.insertLike(newLike);
            commentDao.incrementLikesCount(commentId);
        }
    }
    
    @Override
    public boolean isCommentLikedByUser(long commentId, long userId) {
        return likeDao.isCommentLikedByUser(userId, commentId);
    }
    
    @Override
    public LiveData<Integer> getCommentLikesCount(long commentId) {
        return likeDao.getCommentLikesCount(commentId);
    }
    
    // User operations
    @Override
    public UserEntity getUserById(long userId) {
        return userDao.getUserById(userId);
    }
    
    @Override
    public UserEntity getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }
    
    @Override
    public LiveData<List<UserEntity>> getAllUsers() {
        return userDao.getAllUsers();
    }
    
    @Override
    public LiveData<List<UserEntity>> getUsersByRole(String role) {
        return userDao.getUsersByRole(role);
    }
    
    @Override
    public long createUser(UserEntity user) {
        return userDao.insertUser(user);
    }
    
    @Override
    public void updateUser(UserEntity user) {
        userDao.updateUser(user);
    }
    
    @Override
    public void deleteUser(UserEntity user) {
        userDao.deleteUser(user);
    }
    
    // Validation operations
    @Override
    public boolean isValidImageFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        String extension = filePath.toLowerCase().substring(filePath.lastIndexOf('.') + 1);
        return getSupportedImageFormats().contains(extension);
    }
    
    @Override
    public boolean isValidCaption(String caption) {
        if (caption == null) {
            return true; // Caption is optional if image exists
        }
        String trimmed = caption.trim();
        return trimmed.length() <= 1000;
    }
    
    @Override
    public boolean isValidComment(String content) {
        if (content == null) {
            return false;
        }
        String trimmed = content.trim();
        return !trimmed.isEmpty() && trimmed.length() <= 500;
    }
    
    // Utility operations
    @Override
    public String saveImageToLocal(String imagePath) {
        try {
            File sourceFile = new File(imagePath);
            if (!sourceFile.exists()) {
                return null;
            }
            
            // Create app's images directory
            File imagesDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "community_images");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }
            
            // Generate unique filename
            String fileName = "img_" + UUID.randomUUID().toString() + ".jpg";
            File destFile = new File(imagesDir, fileName);
            
            // Copy and compress image
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null) {
                // Compress to reduce file size
                FileOutputStream out = new FileOutputStream(destFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
                out.close();
                bitmap.recycle();
                return destFile.getAbsolutePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public void deleteImageFromLocal(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }
    }
    
    @Override
    public List<String> getSupportedImageFormats() {
        List<String> formats = new ArrayList<>();
        formats.add("jpg");
        formats.add("jpeg");
        formats.add("png");
        return formats;
    }
    
    // Search operations (simplified implementation)
    @Override
    public LiveData<List<PostEntity>> searchPosts(String query) {
        // For now, return all posts. In a real implementation, 
        // you would use Room's @Query with LIKE operator
        return getAllPosts();
    }
    
    @Override
    public LiveData<List<CommentEntity>> searchComments(String query) {
        // For now, return empty list. In a real implementation,
        // you would use Room's @Query with LIKE operator
        return new androidx.lifecycle.MutableLiveData<>();
    }
    
    // Statistics operations
    @Override
    public LiveData<Integer> getTotalPostsCount() {
        return postDao.getPostsCount();
    }
    
    @Override
    public LiveData<Integer> getTotalCommentsCount() {
        return commentDao.getCommentCountByPost(0); // Simplified
    }
    
    @Override
    public LiveData<Integer> getTotalUsersCount() {
        return userDao.getUsersCount();
    }
    
    @Override
    public LiveData<Integer> getPostsCountByAuthor(long authorId) {
        return postDao.getPostsCountByAuthor(authorId);
    }
}
