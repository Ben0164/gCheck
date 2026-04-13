package com.example.myapplication.palay.analysis;

public class AnalysisResult {
    private final int goodPercentage;
    private final int badPercentage;
    private final String grade;
    private final double price;

    public AnalysisResult(int goodPercentage, int badPercentage, String grade, double price) {
        this.goodPercentage = goodPercentage;
        this.badPercentage = badPercentage;
        this.grade = grade;
        this.price = price;
    }

    public int getGoodPercentage() {
        return goodPercentage;
    }

    public int getBadPercentage() {
        return badPercentage;
    }

    public String getGrade() {
        return grade;
    }

    public double getPrice() {
        return price;
    }
}

