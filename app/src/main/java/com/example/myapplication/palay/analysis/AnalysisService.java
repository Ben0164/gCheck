package com.example.myapplication.palay.analysis;

import android.graphics.Bitmap;

/**
 * Pluggable interface for AI integration.
 */
public interface AnalysisService {
    
    /**
     * Legacy analysis method.
     */
    AnalysisResult analyze(byte[] imageBytes, double moisture);

    /**
     * Placeholder for future AI integration (TFLite or API).
     * Task 6 Hook.
     */
    default AnalysisResult analyzeGrainQuality(Bitmap image) {
        // Implementation will be added here later (e.g. TFLite Interpreter)
        return null; 
    }
}
