package com.example.myapplication.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.myapplication.core.data.entity.UserEntity;
import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(UserEntity user);

    @Insert
    long insertUser(UserEntity user);

    @Update
    void update(UserEntity user);

    @Update
    void updateUser(UserEntity user);

    @Delete
    void delete(UserEntity user);

    @Delete
    void deleteUser(UserEntity user);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    UserEntity getUserById(long userId);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity getUserByEmail(String email);

    @Query("SELECT * FROM users")
    LiveData<List<UserEntity>> getAllUsers();

    @Query("SELECT * FROM users WHERE role = :role")
    LiveData<List<UserEntity>> getUsersByRole(String role);

    @Query("SELECT COUNT(*) FROM users")
    LiveData<Integer> getUsersCount();

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity findByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    UserEntity login(String email, String password);
}
