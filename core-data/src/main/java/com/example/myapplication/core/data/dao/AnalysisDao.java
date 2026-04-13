package com.example.myapplication.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.myapplication.core.data.entity.AnalysisEntity;
import java.util.List;

@Dao
public interface AnalysisDao {

    @Insert
    long insert(AnalysisEntity entity);

    @Query("SELECT * FROM analysis_results ORDER BY createdAt DESC LIMIT 1")
    AnalysisEntity getLatest();

    @Query("SELECT * FROM analysis_results ORDER BY createdAt DESC LIMIT 1")
    LiveData<AnalysisEntity> getLatestLiveData();

    @Query("SELECT * FROM analysis_results ORDER BY createdAt DESC")
    LiveData<List<AnalysisEntity>> getAllAnalyses();

    @Query("SELECT * FROM analysis_results WHERE id = :id")
    AnalysisEntity getAnalysisById(long id);
}
