package com.example.myapplication.palay.analysis;

import java.util.Random;

public class MockAnalysisService implements AnalysisService {

    private final Random random;

    public MockAnalysisService() {
        this(new Random());
    }

    public MockAnalysisService(Random random) {
        this.random = random;
    }

    @Override
    public AnalysisResult analyze(byte[] imageBytes, double moisture) {
        // Mock: simulate AI output with realistic values.
        int goodPercentage = 70 + random.nextInt(26); // 70..95 inclusive
        int badPercentage = 100 - goodPercentage;

        String grade;
        if (goodPercentage >= 80 && moisture <= 14.0) {
            grade = "A";
        } else if (goodPercentage >= 60) {
            grade = "B";
        } else {
            grade = "C";
        }

        double penalty = Math.max(0.0, moisture - 14.0) * 0.5;
        double price = 20.0 + (goodPercentage / 100.0 * 10.0) - penalty;

        return new AnalysisResult(goodPercentage, badPercentage, grade, price);
    }
}

