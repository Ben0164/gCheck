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

    // Bidding system queries
    @Query("SELECT * FROM products WHERE listingStatus = 'ACTIVE' ORDER BY createdAt DESC")
    LiveData<List<ProductEntity>> getActiveListings();

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    LiveData<ProductEntity> getByIdLiveData(long id);

    @Query("UPDATE products SET currentHighestBid = :bidAmount, currentHighestBidderId = :bidderId WHERE id = :productId")
    void updateHighestBid(long productId, double bidAmount, long bidderId);

    @Query("UPDATE products SET listingStatus = :status, finalSalePrice = :salePrice, winningBuyerId = :buyerId, isSold = 1 WHERE id = :productId")
    void markAsSold(long productId, String status, double salePrice, long buyerId);
}
