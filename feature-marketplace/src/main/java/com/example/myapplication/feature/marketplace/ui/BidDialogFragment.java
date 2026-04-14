package com.example.myapplication.feature.marketplace.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.myapplication.feature.marketplace.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class BidDialogFragment extends DialogFragment {
    public interface OnBidSubmitListener { void onSubmit(double bidAmount); }
    private OnBidSubmitListener listener;
    private double breakEvenPrice = 0.0;

    public static BidDialogFragment newInstance(double breakEven, OnBidSubmitListener listener) {
        BidDialogFragment fragment = new BidDialogFragment();
        fragment.breakEvenPrice = breakEven;
        fragment.listener = listener;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_place_bid, null);
        TextInputLayout tilBid = view.findViewById(R.id.til_bid_amount);
        TextInputEditText etBid = view.findViewById(R.id.et_bid_amount);
        TextView tvWarning = view.findViewById(R.id.tv_bid_warning);

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.place_bid)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.save_bid, null)
                .create();

        dialog.setOnShowListener(dlg -> dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            tilBid.setError(null);
            if (tvWarning != null) tvWarning.setVisibility(View.GONE);
            String bidStr = etBid.getText() != null ? etBid.getText().toString().trim() : "";
            if (TextUtils.isEmpty(bidStr)) {
                tilBid.setError(getString(R.string.error_field_required));
                return;
            }
            double bidValue;
            try {
                bidValue = Double.parseDouble(bidStr);
            } catch (NumberFormatException e) {
                tilBid.setError(getString(R.string.error_invalid_number));
                return;
            }
            if (bidValue < breakEvenPrice) {
                if (tvWarning != null) {
                    tvWarning.setVisibility(View.VISIBLE);
                    tvWarning.setText("⚠️ This offer is below the farmer's break-even price.");
                } else {
                    tilBid.setError("⚠️ This offer is below break-even price");
                }
            }
            if (listener != null) listener.onSubmit(bidValue);
            dialog.dismiss();
        }));
        return dialog;
    }
}
