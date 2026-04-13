package com.example.myapplication.core.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProfitCalculator {

    private static final BigDecimal HAULING_RATE_PER_KM_PER_SACK = new BigDecimal("0.50");
    private static final BigDecimal KG_PER_SACK = new BigDecimal("50");

    /**
     * Calculates the Breakeven Price.
     * Prevents divide-by-zero by returning ZERO if yield <= 0.
     */
    public static BigDecimal calculateBreakevenPrice(double totalExpenses, double yield) {
        if (yield <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal total = BigDecimal.valueOf(totalExpenses);
        BigDecimal qty = BigDecimal.valueOf(yield);
        
        return total.divide(qty, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates ROI (Return on Investment).
     * ROI = (Revenue - Cost) / Cost
     */
    public static double calculateROI(double revenue, double totalCost) {
        if (totalCost <= 0) return 0.0;
        return (revenue - totalCost) / totalCost;
    }

    /**
     * Calculates hauling cost for a given yield and distance.
     */
    public static BigDecimal calculateHaulingCost(double yieldKg, double distanceKm) {
        return calculateFulfillmentHauling(BigDecimal.valueOf(yieldKg), distanceKm);
    }

    /**
     * TASK 4: Calculate Fulfillment Hauling (FUTURE COST)
     * Calculated dynamically per Bid based on distance.
     */
    public static BigDecimal calculateFulfillmentHauling(BigDecimal yieldKg, double distanceKm) {
        if (yieldKg.compareTo(BigDecimal.ZERO) <= 0 || distanceKm <= 0) {
            return BigDecimal.ZERO;
        }
        // sacks = yield / 50 (Ceiling)
        BigDecimal numSacks = yieldKg.divide(KG_PER_SACK, 0, RoundingMode.CEILING);
        // hauling = sacks * distance * 0.50
        return numSacks.multiply(BigDecimal.valueOf(distanceKm)).multiply(HAULING_RATE_PER_KM_PER_SACK);
    }

    /**
     * TASK 5: Reusable Net Profit Method (Distance-aware)
     * Separates sunk costs (Explicit + Implicit) from future costs (Fulfillment Hauling).
     */
    public static BigDecimal calculateNetProfit(
            BigDecimal pricePerKg,
            BigDecimal yieldKg,
            BigDecimal totalExplicitCost, // Includes Internal Hauling (Sunk)
            BigDecimal totalImplicitCost,
            double distanceKm,
            boolean includeImplicit
    ) {
        // If yield is 0, profit is negative sunk costs
        if (yieldKg.compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal totalSunkCost = totalExplicitCost;
            if (includeImplicit) {
                totalSunkCost = totalSunkCost.add(totalImplicitCost);
            }
            return totalSunkCost.negate().setScale(2, RoundingMode.HALF_UP);
        }

        // revenue = price × yield
        BigDecimal revenue = pricePerKg.multiply(yieldKg);
        
        // fulfillmentHauling = future cost calculated per bid
        BigDecimal fulfillmentHauling = calculateFulfillmentHauling(yieldKg, distanceKm);
        
        // totalSunkCost = explicit (+ implicit if toggle ON)
        BigDecimal totalSunkCost = totalExplicitCost;
        if (includeImplicit) {
            totalSunkCost = totalSunkCost.add(totalImplicitCost);
        }
        
        // Net Profit = Revenue - (Sunk Costs + Future Fulfillment Costs)
        return revenue.subtract(totalSunkCost).subtract(fulfillmentHauling).setScale(2, RoundingMode.HALF_UP);
    }
}
