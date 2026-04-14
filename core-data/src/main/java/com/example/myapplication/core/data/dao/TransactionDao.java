package com.example.myapplication.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.myapplication.core.data.entity.TransactionEntity;
import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    long insert(TransactionEntity transaction);

    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC")
    LiveData<List<TransactionEntity>> getAll();

    @Query("SELECT * FROM transactions WHERE productId = :productId")
    LiveData<TransactionEntity> getByProductId(long productId);

    @Query("SELECT * FROM transactions WHERE farmerId = :farmerId ORDER BY transactionDate DESC")
    LiveData<List<TransactionEntity>> getFarmerTransactions(long farmerId);

    @Query("SELECT * FROM transactions WHERE buyerId = :buyerId ORDER BY transactionDate DESC")
    LiveData<List<TransactionEntity>> getBuyerTransactions(long buyerId);

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    TransactionEntity getById(long id);
}
