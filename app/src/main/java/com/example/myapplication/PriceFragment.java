package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import java.util.Locale;

public class PriceFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price, container, false);

        EditText etPriceSold = view.findViewById(R.id.et_price_sold);
        EditText etEntityFee = view.findViewById(R.id.et_entity_fee);
        EditText etPricePaid = view.findViewById(R.id.et_price_paid);
        TextView tvResultProfit = view.findViewById(R.id.tv_result_profit);
        MaterialButton btnCalculate = view.findViewById(R.id.btn_calculate);

        btnCalculate.setOnClickListener(v -> {
            String soldStr = etPriceSold.getText().toString();
            String feeStr = etEntityFee.getText().toString();
            String paidStr = etPricePaid.getText().toString();

            double priceSold = soldStr.isEmpty() ? 0 : Double.parseDouble(soldStr);
            double entityFee = feeStr.isEmpty() ? 0 : Double.parseDouble(feeStr);
            double pricePaid = paidStr.isEmpty() ? 0 : Double.parseDouble(paidStr);

            double netProfit = priceSold - entityFee - pricePaid;

            tvResultProfit.setText(String.format(Locale.getDefault(), "₱ %.2f", netProfit));
        });

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).loadFragment(new HomeFragment());
                ((MainActivity) getActivity()).setBottomNavSelection(R.id.navigation_home);
            }
        });

        return view;
    }
}
