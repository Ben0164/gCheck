package com.example.myapplication.feature.analysis.analysis;

import android.graphics.Bitmap;

public interface AnalysisService {
    AnalysisResult analyze(byte[] imageBytes, double moisture);

    default AnalysisResult analyzeGrainQuality(Bitmap image) {
        return null;
    }
}
