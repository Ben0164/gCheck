package com.example.myapplication.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.myapplication.core.data.entity.MethodEntity;
import java.util.List;

@Dao
public interface MethodDao {
    @Insert
    long insert(MethodEntity method);

    @Query("SELECT * FROM methods WHERE activityId = :activityId")
    LiveData<List<MethodEntity>> getMethodsByActivity(long activityId);

    @Query("SELECT * FROM methods WHERE activityId = :activityId")
    List<MethodEntity> getMethodsByActivitySync(long activityId);
}
