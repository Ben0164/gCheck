package com.example.myapplication.feature.analysis.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.myapplication.core.common.FeatureNavigationHost;
import com.example.myapplication.feature.analysis.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.Locale;

public class AnalysisResultFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis_result, container, false);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_analysis_result);
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() instanceof FeatureNavigationHost) {
                ((FeatureNavigationHost) getActivity()).selectHomeTab();
            }
        });

        TextView tvGood = view.findViewById(R.id.tv_good_percentage);
        TextView tvBad = view.findViewById(R.id.tv_bad_percentage);
        TextView tvGrade = view.findViewById(R.id.tv_grade);
        TextView tvPrice = view.findViewById(R.id.tv_price);
        MaterialCardView cardGrade = view.findViewById(R.id.card_grade);
        MaterialButton btnTrackExpenses = view.findViewById(R.id.btn_track_expenses);

        Bundle args = getArguments();
        long analysisId = args != null ? args.getLong("analysisId", -1) : -1;
        int goodPercentage = args != null ? args.getInt("goodPercentage", 0) : 0;
        int badPercentage = args != null ? args.getInt("badPercentage", 0) : 0;
        String grade = args != null ? args.getString("grade", "C") : "C";
        double price = args != null ? args.getDouble("price", 0.0) : 0.0;

        tvGood.setText(String.format(Locale.getDefault(), "%d%%", goodPercentage));
        tvBad.setText(String.format(Locale.getDefault(), "%d%%", badPercentage));
        tvGrade.setText(grade);
        tvPrice.setText(String.format(Locale.getDefault(), "₱ %.2f", price));

        int gradeColor;
        int gradeBg;
        if ("A".equalsIgnoreCase(grade)) {
            gradeColor = ContextCompat.getColor(requireContext(), R.color.primary);
            gradeBg = ContextCompat.getColor(requireContext(), R.color.primary_container);
        } else if ("B".equalsIgnoreCase(grade)) {
            gradeColor = ContextCompat.getColor(requireContext(), R.color.profit_positive);
            gradeBg = ContextCompat.getColor(requireContext(), R.color.profit_positive_bg);
        } else {
            gradeColor = ContextCompat.getColor(requireContext(), R.color.error);
            gradeBg = ContextCompat.getColor(requireContext(), R.color.loss_negative_bg);
        }
        tvGrade.setTextColor(gradeColor);
        cardGrade.setCardBackgroundColor(gradeBg);

        btnTrackExpenses.setOnClickListener(v -> {
            if (getActivity() instanceof FeatureNavigationHost) {
                ((FeatureNavigationHost) getActivity()).openExpenseForBatch(analysisId);
            }
        });
        return view;
    }
}
