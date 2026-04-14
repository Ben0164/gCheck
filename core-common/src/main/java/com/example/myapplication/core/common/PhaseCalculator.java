package com.example.myapplication.core.common;

public class PhaseCalculator {
    public static class PhaseResult {
        public final String phaseName;
        public final long dayCount;

        public PhaseResult(String phaseName, long dayCount) {
            this.phaseName = phaseName;
            this.dayCount = dayCount;
        }
    }

    public static PhaseResult calculatePhase(long startDate, long currentDate) {
        long diff = currentDate - startDate;
        long days = diff / (24 * 60 * 60 * 1000);
        
        String phase;
        if (days < 15) phase = "Land Preparation";
        else if (days < 30) phase = "Crop Establishment";
        else if (days < 90) phase = "Crop Management";
        else if (days < 110) phase = "Reproductive / Ripening Monitoring";
        else if (days < 125) phase = "Harvesting";
        else phase = "Post-Harvest";
        
        return new PhaseResult(phase, days);
    }
}
