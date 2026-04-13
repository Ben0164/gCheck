package com.example.myapplication.palay.ui.analyze;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.palay.ui.result.AnalysisResultFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import java.util.Locale;

public class AnalyzeFragment extends Fragment {

    private AnalyzeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analyze_palay, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_analyze);
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.loadFragment(new com.example.myapplication.HomeFragment());
                activity.setBottomNavSelection(R.id.navigation_home);
            }
        });

        TextInputLayout tilMoisture = view.findViewById(R.id.til_moisture);
        TextInputEditText etMoisture = view.findViewById(R.id.et_moisture);
        MaterialButton btnAnalyze = view.findViewById(R.id.btn_analyze);
        CircularProgressIndicator progressAnalyze = view.findViewById(R.id.progress_analyze);

        viewModel = new ViewModelProvider(this).get(AnalyzeViewModel.class);
        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            boolean loading = Boolean.TRUE.equals(isLoading);
            btnAnalyze.setEnabled(!loading);
            btnAnalyze.setAlpha(loading ? 0.6f : 1f);
            progressAnalyze.setVisibility(loading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), err -> {
            if (!TextUtils.isEmpty(err)) {
                // Show inline error for moisture field.
                tilMoisture.setError(err);
            }
        });

        viewModel.getAnalysisResult().observe(getViewLifecycleOwner(), uiModel -> {
            if (uiModel == null) return;
            Bundle args = new Bundle();
            args.putLong("analysisId", uiModel.getAnalysisId());
            args.putInt("goodPercentage", uiModel.getGoodPercentage());
            args.putInt("badPercentage", uiModel.getBadPercentage());
            args.putString("grade", uiModel.getGrade());
            args.putDouble("price", uiModel.getPrice());

            AnalysisResultFragment resultFragment = new AnalysisResultFragment();
            resultFragment.setArguments(args);

            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.loadFragment(resultFragment);
                activity.setBottomNavSelection(R.id.navigation_scan);
            }
        });

        btnAnalyze.setOnClickListener(v -> {
            if (tilMoisture.getEditText() == null) return;

            tilMoisture.setError(null);

            String moistureStr = etMoisture.getText() != null ? etMoisture.getText().toString().trim() : "";
            if (TextUtils.isEmpty(moistureStr)) {
                tilMoisture.setError(getString(R.string.error_field_required));
                return;
            }

            double moisture;
            try {
                moisture = Double.parseDouble(moistureStr);
            } catch (NumberFormatException e) {
                tilMoisture.setError(getString(R.string.error_invalid_number));
                return;
            }

            // Optional: basic sanity clamp to keep mock output realistic.
            if (moisture < 0.0 || moisture > 30.0) {
                tilMoisture.setError("Moisture should be between 0 and 30.");
                return;
            }

            viewModel.analyze(moisture);
        });

        return view;
    }
}

