package com.example.myapplication.feature.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.palay.data.repository.SessionManager;
import java.util.Locale;

public class ProfileAnalyticsFragment extends Fragment {

    private TextView tvTotalProfit, tvRevenue, tvExpenses, tvAvgPrice, tvBestBuyer;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_analytics, container, false);

        db = AppDatabase.getInstance(requireContext());
        tvTotalProfit = view.findViewById(R.id.tv_total_profit);
        tvRevenue = view.findViewById(R.id.tv_revenue);
        tvExpenses = view.findViewById(R.id.tv_expenses);
        tvAvgPrice = view.findViewById(R.id.tv_avg_price);
        tvBestBuyer = view.findViewById(R.id.tv_best_buyer);

        loadAnalytics();

        return view;
    }

    private void loadAnalytics() {
        if (SessionManager.getCurrentUser() == null) return;
        
        // Mocking financial data for demo purposes
        double profit = 42500.00;
        double revenue = 124500.00;
        double expenses = 82000.00;
        double avgPrice = 18.50;
        String bestBuyer = "Bataan Millers";

        tvTotalProfit.setText(String.format(Locale.getDefault(), "₱%,.2f", profit));
        tvRevenue.setText(String.format(Locale.getDefault(), "₱%,.2f", revenue));
        tvExpenses.setText(String.format(Locale.getDefault(), "₱%,.2f", expenses));
        tvAvgPrice.setText(String.format(Locale.getDefault(), "₱%,.2f/kg", avgPrice));
        tvBestBuyer.setText(bestBuyer);
    }
}
