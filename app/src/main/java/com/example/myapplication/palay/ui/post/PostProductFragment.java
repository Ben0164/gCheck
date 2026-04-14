package com.example.myapplication.palay.ui.post;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.HomeFragment;
import com.example.myapplication.core.common.SessionManager;
import com.example.myapplication.feature.logbook.CreateBatchActivity;
import com.example.myapplication.feature.marketplace.repository.MarketplaceRepository;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.AnalysisEntity;
import com.example.myapplication.core.data.entity.BatchEntity;
import com.example.myapplication.core.data.entity.UserEntity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostProductFragment extends Fragment {

    private long selectedBatchId = -1;
    private List<BatchEntity> batchList = new ArrayList<>();
    private AnalysisEntity latestAnalysis;
    private boolean isDemoMode = false; // Toggle between AI and Demo mode

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_product, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_post_product);
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.loadFragment(new HomeFragment());
                activity.setBottomNavSelection(R.id.navigation_home);
            }
        });

        TextInputLayout tilQuantity = view.findViewById(R.id.til_listing_quantity);
        TextInputEditText etQuantity = view.findViewById(R.id.et_listing_quantity);
        TextView tvGrade = view.findViewById(R.id.tv_listing_grade);
        TextInputEditText etGrade = view.findViewById(R.id.et_listing_grade);
        TextView tvPrice = view.findViewById(R.id.tv_listing_price);
        MaterialButton btnPost = view.findViewById(R.id.btn_post_listing);
        
        TextInputLayout tilSelectBatch = view.findViewById(R.id.til_select_batch);
        AutoCompleteTextView acSelectBatch = view.findViewById(R.id.ac_select_batch);
        MaterialButton btnCreateBatch = view.findViewById(R.id.btnCreateBatch);
        
        // Demo mode button and labels
        MaterialButton btnDemoMode = view.findViewById(R.id.btn_demo_mode);
        TextView tvDemoLabel = view.findViewById(R.id.tv_demo_label);
        View cvVerifiedBadge = view.findViewById(R.id.cv_verified_badge);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        MarketplaceRepository marketplaceRepository = new MarketplaceRepository(requireContext());
        
        marketplaceRepository.getLatestAnalysis().observe(getViewLifecycleOwner(), analysis -> {
            this.latestAnalysis = analysis;
            if (analysis != null && !isDemoMode) {
                // Analysis available, AI verified post
                if (tvGrade != null) {
                    tvGrade.setVisibility(View.VISIBLE);
                    tvGrade.setText(analysis.getGrade());
                }
                if (etGrade != null) etGrade.setVisibility(View.GONE);
                if (tvPrice != null) tvPrice.setText(String.format(Locale.getDefault(), "₱ %.2f", analysis.getPrice()));
                if (tvDemoLabel != null) tvDemoLabel.setVisibility(View.GONE);
                if (cvVerifiedBadge != null) cvVerifiedBadge.setVisibility(View.VISIBLE);
            } else if (analysis == null) {
                // No analysis available
                if (cvVerifiedBadge != null) cvVerifiedBadge.setVisibility(View.GONE);
            }
        });

        // Demo mode toggle
        btnDemoMode.setOnClickListener(v -> {
            isDemoMode = !isDemoMode;
            if (isDemoMode) {
                // Enable demo mode
                btnDemoMode.setText("Use AI Verified");
                if (tvGrade != null) tvGrade.setVisibility(View.GONE);
                if (etGrade != null) etGrade.setVisibility(View.VISIBLE);
                if (tvDemoLabel != null) {
                    tvDemoLabel.setVisibility(View.VISIBLE);
                    tvDemoLabel.setText("⚠ Demo Data • Not AI Verified");
                }
                if (cvVerifiedBadge != null) cvVerifiedBadge.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Demo mode enabled. Data is not AI verified.", Toast.LENGTH_SHORT).show();
            } else {
                // Switch back to AI mode
                btnDemoMode.setText("Create Demo Listing");
                if (latestAnalysis != null) {
                    if (tvGrade != null) {
                        tvGrade.setVisibility(View.VISIBLE);
                        tvGrade.setText(latestAnalysis.getGrade());
                    }
                    if (etGrade != null) etGrade.setVisibility(View.GONE);
                    if (tvDemoLabel != null) tvDemoLabel.setVisibility(View.GONE);
                    if (cvVerifiedBadge != null) cvVerifiedBadge.setVisibility(View.VISIBLE);
                }
            }
        });

        // Load Batches for selection
        db.batchDao().getAllBatches().observe(getViewLifecycleOwner(), batches -> {
            if (batches != null) {
                this.batchList = batches;
                if (batches.isEmpty()) {
                    btnCreateBatch.setVisibility(View.VISIBLE);
                } else {
                    btnCreateBatch.setVisibility(View.GONE);
                    List<String> batchNames = new ArrayList<>();
                    for (BatchEntity b : batches) {
                        batchNames.add(b.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                            android.R.layout.simple_dropdown_item_1line, batchNames);
                    acSelectBatch.setAdapter(adapter);
                }
            }
        });

        btnCreateBatch.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateBatchActivity.class);
            startActivity(intent);
        });

        acSelectBatch.setOnItemClickListener((parent, v, position, id) -> {
            selectedBatchId = batchList.get(position).getId();
            tilSelectBatch.setError(null);
        });

        btnPost.setOnClickListener(v -> {
            UserEntity user = SessionManager.getCurrentUser();
            if (user == null) {
                Toast.makeText(requireContext(), "Login first.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!"farmer".equalsIgnoreCase(user.getRole())) {
                Toast.makeText(requireContext(), R.string.role_farmer_only, Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (selectedBatchId <= 0) {
                tilSelectBatch.setError(getString(R.string.error_select_batch));
                return;
            }

            tilQuantity.setError(null);
            String quantityStr = etQuantity.getText() != null ? etQuantity.getText().toString().trim() : "";
            if (TextUtils.isEmpty(quantityStr)) {
                tilQuantity.setError(getString(R.string.error_field_required));
                return;
            }

            double quantity;
            try {
                quantity = Double.parseDouble(quantityStr);
            } catch (NumberFormatException e) {
                tilQuantity.setError(getString(R.string.error_invalid_number));
                return;
            }

            // Determine post type and values
            String grade;
            double price;
            long analysisId;
            String postType;
            boolean isVerified;
            String verificationMethod = null;

            if (isDemoMode) {
                // Demo post - allow manual input
                String gradeStr = etGrade.getText() != null ? etGrade.getText().toString().trim() : "";
                if (TextUtils.isEmpty(gradeStr)) {
                    ((TextInputLayout) etGrade.getParent().getParent()).setError("Grade is required");
                    return;
                }
                grade = gradeStr;
                price = 0;
                analysisId = 0;
                postType = "DEMO";
                isVerified = false;
                verificationMethod = "Demo Data";
            } else {
                // AI verified post - requires analysis
                if (latestAnalysis == null) {
                    Toast.makeText(requireContext(), "No AI analysis available. Please scan grain first or use Demo mode.", Toast.LENGTH_LONG).show();
                    return;
                }
                grade = latestAnalysis.getGrade();
                price = latestAnalysis.getPrice();
                analysisId = latestAnalysis.getId();
                postType = "AI_VERIFIED";
                isVerified = true;
                verificationMethod = "GCheck AI";
            }

            long deadline = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000);
            
            // Get total expenses from batch
            double totalExpenses = 0;
            if (selectedBatchId > 0) {
                List<com.example.myapplication.core.data.entity.ExpenseEntity> expenses = db.expenseDao().getExpensesByBatchSync(selectedBatchId);
                if (expenses != null) {
                    com.example.myapplication.core.common.ExpenseSummaryHelper helper = new com.example.myapplication.core.common.ExpenseSummaryHelper(expenses);
                    totalExpenses = helper.getGrandTotal();
                }
            }
            
            double buyNowPrice = price > 0 ? price * 1.2 : 0;
            if (buyNowPrice == 0 && totalExpenses > 0) {
                buyNowPrice = com.example.myapplication.core.common.ProfitCalculator.calculateFloorPrice(totalExpenses, quantity, 0.10) * 1.2;
            }
            
            marketplaceRepository.postProduct(
                    user.getId(),
                    user.getName(),
                    quantity,
                    grade,
                    totalExpenses,
                    buyNowPrice,
                    analysisId,
                    deadline,
                    14.5995,
                    120.9842,
                    selectedBatchId,
                    postType,
                    isVerified,
                    verificationMethod
            );

            Toast.makeText(requireContext(), "Listing posted.", Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.loadFragment(new com.example.myapplication.feature.marketplace.ui.MarketplaceFragment());
                activity.setBottomNavSelection(R.id.navigation_market);
            }
        });

        return view;
    }
}
