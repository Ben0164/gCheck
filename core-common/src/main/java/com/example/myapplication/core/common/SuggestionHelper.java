package com.example.myapplication.core.common;

public class SuggestionHelper {
    public static String getSuggestion(String phase) {
        if (phase == null) return "No suggestions available.";
        switch (phase) {
            case "Land Preparation":
                return "Ensure proper leveling for water management.";
            case "Crop Establishment":
                return "Monitor seedling health and moisture levels.";
            case "Crop Management":
                return "Apply fertilizer based on soil analysis.";
            case "Harvesting":
                return "Harvest at 20-25% moisture content.";
            default:
                return "Continue monitoring crop health.";
        }
    }
}
