package com.example.myapplication.feature.collaboration.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;

import com.example.myapplication.feature.collaboration.data.entity.UserEntity;

@Dao
public interface UserDao {
    
    @Insert
    long insertUser(UserEntity user);
    
    @Update
    void updateUser(UserEntity user);
    
    @Delete
    void deleteUser(UserEntity user);
    
    @Query("SELECT * FROM users ORDER BY name ASC")
    LiveData<List<UserEntity>> getAllUsers();
    
    @Query("SELECT * FROM users WHERE id = :userId")
    UserEntity getUserById(long userId);
    
    @Query("SELECT * FROM users WHERE email = :email")
    UserEntity getUserByEmail(String email);
    
    @Query("SELECT * FROM users WHERE role = :role ORDER BY name ASC")
    LiveData<List<UserEntity>> getUsersByRole(String role);
    
    @Query("SELECT COUNT(*) FROM users")
    LiveData<Integer> getUsersCount();
    
    @Query("SELECT COUNT(*) FROM users WHERE role = :role")
    LiveData<Integer> getUsersCountByRole(String role);
    
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    boolean isEmailExists(String email);
}
