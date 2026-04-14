package com.example.myapplication.feature.analysis.repository;

import android.content.Context;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.AnalysisEntity;

public class AnalysisRepository {
    private final AppDatabase db;

    public AnalysisRepository(Context context) {
        this.db = AppDatabase.getInstance(context);
    }

    public long insert(AnalysisEntity entity) {
        return db.analysisDao().insert(entity);
    }
}
