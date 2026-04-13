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
import com.example.myapplication.CreateBatchActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.HomeFragment;
import com.example.myapplication.core.data.entity.AnalysisEntity;
import com.example.myapplication.core.data.entity.BatchEntity;
import com.example.myapplication.core.data.entity.UserEntity;
import com.example.myapplication.palay.data.repository.MarketplaceRepository;
import com.example.myapplication.palay.data.repository.SessionManager;
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
        TextView tvPrice = view.findViewById(R.id.tv_listing_price);
        MaterialButton btnPost = view.findViewById(R.id.btn_post_listing);
        
        TextInputLayout tilSelectBatch = view.findViewById(R.id.til_select_batch);
        AutoCompleteTextView acSelectBatch = view.findViewById(R.id.ac_select_batch);
        MaterialButton btnCreateBatch = view.findViewById(R.id.btnCreateBatch);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        MarketplaceRepository marketplaceRepository = new MarketplaceRepository(requireContext());
        
        marketplaceRepository.getLatestAnalysis().observe(getViewLifecycleOwner(), analysis -> {
            this.latestAnalysis = analysis;
            if (analysis == null) {
                Toast.makeText(requireContext(), R.string.latest_analysis_missing, Toast.LENGTH_SHORT).show();
            } else {
                tvGrade.setText(analysis.getGrade());
                tvPrice.setText(String.format(Locale.getDefault(), "₱ %.2f", analysis.getPrice()));
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
            if (latestAnalysis == null) {
                Toast.makeText(requireContext(), R.string.latest_analysis_missing, Toast.LENGTH_SHORT).show();
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

            long deadline = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000);
            
            marketplaceRepository.postProduct(
                    user.getId(),
                    user.getName(),
                    quantity,
                    latestAnalysis.getGrade(),
                    latestAnalysis.getPrice(),
                    latestAnalysis.getPrice() * 1.2,
                    latestAnalysis.getId(),
                    deadline,
                    14.5995,
                    120.9842,
                    selectedBatchId
            );

            Toast.makeText(requireContext(), "Listing posted.", Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.loadFragment(new com.example.myapplication.palay.ui.marketplace.MarketplaceFragment());
                activity.setBottomNavSelection(R.id.navigation_inbox);
            }
        });

        return view;
    }
}
