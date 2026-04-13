package com.example.myapplication.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.myapplication.core.data.entity.ProductEntity;
import java.util.List;

@Dao
public interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ProductEntity product);

    @Update
    void update(ProductEntity product);

    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    List<ProductEntity> getAll();

    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    LiveData<List<ProductEntity>> getAllLiveData();

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    ProductEntity getById(long id);

    @Query("SELECT * FROM products WHERE farmerId = :userId ORDER BY createdAt DESC")
    LiveData<List<ProductEntity>> getUserListings(long userId);

    @Query("SELECT COUNT(*) FROM products WHERE farmerId = :userId")
    LiveData<Integer> getListingsCount(long userId);
}
