package com.example.myapplication.backup_screen;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.HomeFragment;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

public class GrainCheckFragment extends Fragment {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingSubmitFinish;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grain_check, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_grain);
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.loadFragment(new HomeFragment());
                activity.setBottomNavSelection(R.id.navigation_home);
            }
        });

        TextInputLayout tilQuality = view.findViewById(R.id.til_quality);
        TextInputLayout tilBasePrice = view.findViewById(R.id.til_base_price);
        TextInputLayout tilQuantity = view.findViewById(R.id.til_quantity);

        MaterialButton btnSubmit = view.findViewById(R.id.btn_submit);
        CircularProgressIndicator progressSubmit = view.findViewById(R.id.progress_submit);

        view.findViewById(R.id.card_upload).setOnClickListener(v -> {
            // Placeholder: real app would launch picker / camera
        });

        btnSubmit.setOnClickListener(v -> {
            clearErrors(tilQuality, tilBasePrice, tilQuantity);

            boolean ok = true;
            if (tilQuality.getEditText() != null && TextUtils.isEmpty(tilQuality.getEditText().getText().toString().trim())) {
                tilQuality.setError(getString(R.string.error_field_required));
                ok = false;
            }
            if (tilBasePrice.getEditText() != null && TextUtils.isEmpty(tilBasePrice.getEditText().getText().toString().trim())) {
                tilBasePrice.setError(getString(R.string.error_field_required));
                ok = false;
            }
            if (tilQuantity.getEditText() != null && TextUtils.isEmpty(tilQuantity.getEditText().getText().toString().trim())) {
                tilQuantity.setError(getString(R.string.error_field_required));
                ok = false;
            }
            if (!ok) {
                return;
            }

            if (pendingSubmitFinish != null) {
                mainHandler.removeCallbacks(pendingSubmitFinish);
            }

            btnSubmit.setEnabled(false);
            btnSubmit.setAlpha(0.6f);
            progressSubmit.setVisibility(View.VISIBLE);

            pendingSubmitFinish = () -> {
                if (!isAdded()) {
                    return;
                }
                progressSubmit.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                btnSubmit.setAlpha(1f);
                pendingSubmitFinish = null;
            };
            mainHandler.postDelayed(pendingSubmitFinish, 900);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        if (pendingSubmitFinish != null) {
            mainHandler.removeCallbacks(pendingSubmitFinish);
            pendingSubmitFinish = null;
        }
        super.onDestroyView();
    }

    private static void clearErrors(TextInputLayout... layouts) {
        for (TextInputLayout l : layouts) {
            l.setError(null);
        }
    }
}
