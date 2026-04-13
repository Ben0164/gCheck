package com.example.myapplication.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.myapplication.core.data.entity.BatchEntity;
import java.util.List;

@Dao
public interface BatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BatchEntity batch);

    @Update
    void update(BatchEntity batch);

    @Query("SELECT * FROM batches WHERE userId = :userId ORDER BY startDate DESC")
    LiveData<List<BatchEntity>> getBatchesByUser(long userId);

    @Query("SELECT * FROM batches WHERE id = :id")
    LiveData<BatchEntity> getBatchById(long id);

    @Query("SELECT * FROM batches WHERE id = :id")
    BatchEntity getBatchByIdSync(long id);

    @Query("SELECT * FROM batches ORDER BY startDate DESC")
    LiveData<List<BatchEntity>> getAllBatches();
}
