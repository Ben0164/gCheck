package com.example.myapplication.core.common;

import com.example.myapplication.core.data.entity.ExpenseEntity;
import java.util.List;

public class ExpenseSummaryHelper {

    private final List<ExpenseEntity> expenses;

    public ExpenseSummaryHelper(List<ExpenseEntity> expenses) {
        this.expenses = expenses;
    }

    /**
     * Sum of all explicit (cash) costs.
     */
    public double getTotalExplicitCost() {
        double total = 0;
        if (expenses != null) {
            for (ExpenseEntity e : expenses) {
                total += e.getTotalCost();
            }
        }
        return total;
    }

    /**
     * Sum of internal hauling costs only.
     */
    public double getTotalInternalHaulingCost() {
        double total = 0;
        if (expenses != null) {
            for (ExpenseEntity e : expenses) {
                if (ExpenseEntity.TYPE_HAULING_INTERNAL.equals(e.getExpenseType())) {
                    total += e.getTotalCost();
                }
            }
        }
        return total;
    }

    /**
     * Sum of all implicit (owner labor) costs.
     */
    public double getTotalImplicitCost() {
        double total = 0;
        if (expenses != null) {
            for (ExpenseEntity e : expenses) {
                total += e.getImplicitCost();
            }
        }
        return total;
    }

    /**
     * Grand Total = Explicit + Implicit Costs.
     */
    public double getGrandTotal() {
        return getTotalExplicitCost() + getTotalImplicitCost();
    }
}
