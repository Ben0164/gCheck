package com.example.myapplication.feature.analysis.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.core.common.FeatureNavigationHost;
import com.example.myapplication.feature.analysis.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AnalyzeFragment extends Fragment {
    private AnalyzeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analyze_palay, container, false);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_analyze);
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() instanceof FeatureNavigationHost) {
                ((FeatureNavigationHost) getActivity()).selectHomeTab();
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
            if (!TextUtils.isEmpty(err)) tilMoisture.setError(err);
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
            if (getActivity() instanceof FeatureNavigationHost) {
                FeatureNavigationHost host = (FeatureNavigationHost) getActivity();
                host.openFragment(resultFragment);
                host.selectScanTab();
            }
        });

        btnAnalyze.setOnClickListener(v -> {
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
            if (moisture < 0.0 || moisture > 30.0) {
                tilMoisture.setError("Moisture should be between 0 and 30.");
                return;
            }
            viewModel.analyze(moisture);
        });
        return view;
    }
}
