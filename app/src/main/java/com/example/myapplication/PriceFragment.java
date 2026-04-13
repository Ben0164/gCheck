package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.ExpenseEntity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;

public class PriceFragment extends Fragment {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingCalculateFinish;
    private AppDatabase db;
    private double totalExplicit = 0;
    private double totalImplicit = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price, container, false);
        db = AppDatabase.getInstance(requireContext());

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_profit);
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.loadFragment(new HomeFragment());
                activity.setBottomNavSelection(R.id.navigation_home);
            }
        });

        TextInputLayout tilSold = view.findViewById(R.id.til_price_sold);
        TextInputLayout tilExplicit = view.findViewById(R.id.til_explicit_costs);
        TextInputLayout tilImplicit = view.findViewById(R.id.til_implicit_costs);

        TextView tvResult = view.findViewById(R.id.tv_result_profit);
        TextView tvNetStatus = view.findViewById(R.id.tv_net_status);
        TextView tvFormula = view.findViewById(R.id.tv_profit_formula);
        MaterialCardView cardResult = view.findViewById(R.id.card_total_profit);
        
        MaterialSwitch switchImplicit = view.findViewById(R.id.switch_include_implicit);
        View layoutSummary = view.findViewById(R.id.layout_summary);
        View rowImplicit = view.findViewById(R.id.row_implicit);
        
        TextView tvSumRevenue = view.findViewById(R.id.tv_summary_revenue);
        TextView tvSumExplicit = view.findViewById(R.id.tv_summary_explicit);
        TextView tvSumImplicit = view.findViewById(R.id.tv_summary_implicit);
        TextView tvSumProfitLabel = view.findViewById(R.id.tv_summary_profit_label);
        TextView tvSumProfitValue = view.findViewById(R.id.tv_summary_profit_value);

        MaterialButton btnCalculate = view.findViewById(R.id.btn_calculate);
        CircularProgressIndicator progress = view.findViewById(R.id.progress_calculate);

        // Fetch real data from DB to pre-fill
        db.expenseDao().getExpensesByUser(1).observe(getViewLifecycleOwner(), expenses -> {
            if (expenses != null) {
                totalExplicit = 0;
                totalImplicit = 0;
                for (ExpenseEntity e : expenses) {
                    totalExplicit += e.getTotalCost();
                    totalImplicit += e.getImplicitCost();
                }
                if (tilExplicit.getEditText() != null) tilExplicit.getEditText().setText(String.valueOf(totalExplicit));
                if (tilImplicit.getEditText() != null) tilImplicit.getEditText().setText(String.valueOf(totalImplicit));
            }
        });

        switchImplicit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tilImplicit.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            tvNetStatus.setText(isChecked ? "ECONOMIC PROFIT" : "CASH PROFIT");
            tvFormula.setText(isChecked ? "Revenue − (Explicit + Implicit Costs)" : "Revenue − Explicit Costs");
        });

        btnCalculate.setOnClickListener(v -> {
            String soldStr = tilSold.getEditText() != null ? tilSold.getEditText().getText().toString() : "";
            String explicitStr = tilExplicit.getEditText() != null ? tilExplicit.getEditText().getText().toString() : "";
            String implicitStr = tilImplicit.getEditText() != null ? tilImplicit.getEditText().getText().toString() : "0";

            if (TextUtils.isEmpty(soldStr.trim())) {
                tilSold.setError("Required");
                return;
            }

            final double revenue = Double.parseDouble(soldStr);
            final double explicit = explicitStr.isEmpty() ? 0 : Double.parseDouble(explicitStr);
            final double implicit = implicitStr.isEmpty() ? 0 : Double.parseDouble(implicitStr);

            btnCalculate.setEnabled(false);
            progress.setVisibility(View.VISIBLE);

            pendingCalculateFinish = () -> {
                if (!isAdded()) return;
                
                boolean includeImplicit = switchImplicit.isChecked();
                double result = includeImplicit ? (revenue - (explicit + implicit)) : (revenue - explicit);
                
                applyProfitUi(tvResult, tvNetStatus, cardResult, result, includeImplicit);
                
                // Update Summary
                layoutSummary.setVisibility(View.VISIBLE);
                tvSumRevenue.setText(String.format(Locale.getDefault(), "₱%,.2f", revenue));
                tvSumExplicit.setText(String.format(Locale.getDefault(), "- ₱%,.2f", explicit));
                
                if (includeImplicit) {
                    rowImplicit.setVisibility(View.VISIBLE);
                    tvSumImplicit.setText(String.format(Locale.getDefault(), "- ₱%,.2f", implicit));
                    tvSumProfitLabel.setText("Economic Profit");
                } else {
                    rowImplicit.setVisibility(View.GONE);
                    tvSumProfitLabel.setText("Cash Profit");
                }
                
                tvSumProfitValue.setText(String.format(Locale.getDefault(), "₱%,.2f", result));
                tvSumProfitValue.setTextColor(ContextCompat.getColor(requireContext(), 
                        result >= 0 ? R.color.profit_positive : R.color.loss_negative));

                progress.setVisibility(View.GONE);
                btnCalculate.setEnabled(true);
            };
            mainHandler.postDelayed(pendingCalculateFinish, 600);
        });

        return view;
    }

    private void applyProfitUi(TextView tvAmount, TextView tvStatus, MaterialCardView card, double net, boolean isEconomic) {
        tvAmount.setText(String.format(Locale.getDefault(), "₱ %,.2f", net));
        boolean profit = net >= 0;
        
        int color = ContextCompat.getColor(requireContext(), profit ? R.color.profit_positive : R.color.loss_negative);
        int bgColor = ContextCompat.getColor(requireContext(), profit ? R.color.profit_positive_bg : R.color.loss_negative_bg);

        tvAmount.setTextColor(color);
        tvStatus.setTextColor(color);
        card.setCardBackgroundColor(bgColor);
        
        String label = isEconomic ? (profit ? "ECONOMIC PROFIT" : "ECONOMIC LOSS") 
                                  : (profit ? "CASH PROFIT" : "CASH LOSS");
        tvStatus.setText(label);
    }

    @Override
    public void onDestroyView() {
        if (pendingCalculateFinish != null) mainHandler.removeCallbacks(pendingCalculateFinish);
        super.onDestroyView();
    }
}
