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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.TextView;
import java.util.Locale;

public class PriceFragment extends Fragment {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingCalculateFinish;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_profit);
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.loadFragment(new HomeFragment());
                activity.setBottomNavSelection(R.id.navigation_home);
            }
        });

        TextInputLayout tilSold = view.findViewById(R.id.til_price_sold);
        TextInputLayout tilFee = view.findViewById(R.id.til_entity_fee);
        TextInputLayout tilPaid = view.findViewById(R.id.til_price_paid);

        TextView tvResult = view.findViewById(R.id.tv_result_profit);
        TextView tvNetStatus = view.findViewById(R.id.tv_net_status);
        MaterialCardView cardResult = view.findViewById(R.id.card_total_profit);

        MaterialButton btnCalculate = view.findViewById(R.id.btn_calculate);
        CircularProgressIndicator progress = view.findViewById(R.id.progress_calculate);

        btnCalculate.setOnClickListener(v -> {
            String soldStr = tilSold.getEditText() != null ? tilSold.getEditText().getText().toString() : "";
            String feeStr = tilFee.getEditText() != null ? tilFee.getEditText().getText().toString() : "";
            String paidStr = tilPaid.getEditText() != null ? tilPaid.getEditText().getText().toString() : "";

            tilSold.setError(null);
            tilFee.setError(null);
            tilPaid.setError(null);

            if (TextUtils.isEmpty(soldStr.trim())) {
                tilSold.setError(getString(R.string.error_field_required));
                return;
            }
            if (TextUtils.isEmpty(feeStr.trim())) {
                tilFee.setError(getString(R.string.error_field_required));
                return;
            }
            if (TextUtils.isEmpty(paidStr.trim())) {
                tilPaid.setError(getString(R.string.error_field_required));
                return;
            }

            final double priceSold;
            final double entityFee;
            final double pricePaid;
            try {
                priceSold = Double.parseDouble(soldStr.trim());
                entityFee = Double.parseDouble(feeStr.trim());
                pricePaid = Double.parseDouble(paidStr.trim());
            } catch (NumberFormatException e) {
                tilSold.setError(getString(R.string.error_invalid_number));
                return;
            }

            if (pendingCalculateFinish != null) {
                mainHandler.removeCallbacks(pendingCalculateFinish);
            }

            btnCalculate.setEnabled(false);
            btnCalculate.setAlpha(0.65f);
            progress.setVisibility(View.VISIBLE);

            pendingCalculateFinish = () -> {
                if (!isAdded()) {
                    return;
                }
                double netProfit = priceSold - entityFee - pricePaid;
                applyProfitUi(tvResult, tvNetStatus, cardResult, netProfit);

                progress.setVisibility(View.GONE);
                btnCalculate.setEnabled(true);
                btnCalculate.setAlpha(1f);
                pendingCalculateFinish = null;
            };
            mainHandler.postDelayed(pendingCalculateFinish, 500);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        if (pendingCalculateFinish != null) {
            mainHandler.removeCallbacks(pendingCalculateFinish);
            pendingCalculateFinish = null;
        }
        super.onDestroyView();
    }

    private void applyProfitUi(TextView tvAmount, TextView tvStatus, MaterialCardView card, double net) {
        tvAmount.setText(String.format(Locale.getDefault(), "₱ %,.2f", net));

        boolean profitOrZero = net >= 0;
        int amountColor = ContextCompat.getColor(requireContext(),
                profitOrZero ? R.color.profit_positive : R.color.loss_negative);
        int bgColor = ContextCompat.getColor(requireContext(),
                profitOrZero ? R.color.profit_positive_bg : R.color.loss_negative_bg);

        tvAmount.setTextColor(amountColor);
        tvStatus.setTextColor(amountColor);
        tvStatus.setText(profitOrZero ? getString(R.string.net_profit_label) : getString(R.string.net_loss_label));
        card.setCardBackgroundColor(bgColor);
    }
}
