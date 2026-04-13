package com.example.myapplication.core.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.myapplication.core.data.entity.BidEntity;
import java.util.List;

@Dao
public interface BidDao {
    @Insert
    long insert(BidEntity bid);

    @Query("SELECT * FROM bids WHERE productId = :productId ORDER BY bidAmount DESC")
    List<BidEntity> getBidsForProduct(long productId);

    @Query("SELECT MAX(bidAmount) FROM bids WHERE productId = :productId")
    Double getHighestBid(long productId);
}
