package com.example.myapplication.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.myapplication.core.data.entity.ActivityEntity;
import java.util.List;

@Dao
public interface ActivityDao {
    @Insert
    long insert(ActivityEntity activity);

    @Query("SELECT * FROM activities WHERE phase = :phase")
    List<ActivityEntity> getActivitiesByPhaseSync(String phase);

    @Query("SELECT * FROM activities WHERE phase = :phase")
    LiveData<List<ActivityEntity>> getActivitiesByPhase(String phase);
}
