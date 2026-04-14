package com.example.myapplication.core.data.dao;

import androidx.lifecycle.LiveData;
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

    @Query("SELECT * FROM bids WHERE productId = :productId ORDER BY bidAmount DESC")
    LiveData<List<BidEntity>> getBidsForProductLiveData(long productId);

    @Query("SELECT MAX(bidAmount) FROM bids WHERE productId = :productId")
    Double getHighestBid(long productId);

    @Query("SELECT * FROM bids WHERE productId = :productId AND buyerId = :buyerId ORDER BY bidAmount DESC LIMIT 1")
    BidEntity getUserBidForProduct(long productId, long buyerId);

    @Query("SELECT * FROM bids WHERE buyerId = :buyerId ORDER BY createdAt DESC")
    LiveData<List<BidEntity>> getUserBids(long buyerId);

    @Query("SELECT COUNT(*) FROM bids WHERE productId = :productId")
    LiveData<Integer> getBidCount(long productId);
}
