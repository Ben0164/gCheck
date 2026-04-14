package com.example.myapplication.feature.collaboration;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.myapplication.core.common.FeatureNavigationHost;
import com.example.myapplication.core.common.SessionManager;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.BatchEntity;
import com.example.myapplication.core.data.entity.UserEntity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreatePostFragment extends Fragment {

    private EditText etTitle, etGrade, etMoisture, etQuantity;
    private EditText etFloorPrice, etBuyNowPrice, etDeadline;
    private MaterialSwitch switchBidding;
    private View layoutBiddingDetails;
    private MaterialCardView cardImagePicker;
    private ImageView ivPostImage;
    private View layoutUploadPlaceholder;
    private MaterialButton btnPost;

    private AutoCompleteTextView acSelectBatch;
    private MaterialButton btnCreateBatch;
    private ImageView btnBack;
    
    private Uri selectedImageUri;
    private long analysisId = 0;
    private long selectedBatchId = -1;
    private List<BatchEntity> batchList = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    if (ivPostImage != null) {
                        ivPostImage.setImageURI(null);
                        ivPostImage.setImageURI(uri);
                        ivPostImage.setVisibility(View.VISIBLE);
                    }
                    if (layoutUploadPlaceholder != null) {
                        layoutUploadPlaceholder.setVisibility(View.GONE);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_post, container, false);

        acSelectBatch = v.findViewById(R.id.ac_select_batch);
        btnCreateBatch = v.findViewById(R.id.btnCreateBatch);
        cardImagePicker = v.findViewById(R.id.card_image_picker);
        ivPostImage = v.findViewById(R.id.iv_post_image);
        layoutUploadPlaceholder = v.findViewById(R.id.layout_upload_placeholder);
        btnBack = v.findViewById(R.id.btn_back);
        btnPost = v.findViewById(R.id.btn_post);
        switchBidding = v.findViewById(R.id.switch_bidding);
        layoutBiddingDetails = v.findViewById(R.id.layout_bidding_details);
        etTitle = v.findViewById(R.id.et_post_title);
        etGrade = v.findViewById(R.id.et_grade);
        etMoisture = v.findViewById(R.id.et_moisture);
        etQuantity = v.findViewById(R.id.et_quantity);
        etFloorPrice = v.findViewById(R.id.et_floor_price);
        etBuyNowPrice = v.findViewById(R.id.et_buy_now_price);
        etDeadline = v.findViewById(R.id.et_deadline);

        setupBatchSelection();
        setupListeners();

        btnBack.setOnClickListener(view -> {
            if (getActivity() != null) getActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        return v;
    }

    private void setupBatchSelection() {
        if (!isAdded()) return;
        AppDatabase.getInstance(requireContext()).batchDao().getAllBatches().observe(getViewLifecycleOwner(), batches -> {
            if (batches != null) {
                this.batchList = batches;
                if (batches.isEmpty()) {
                    if (btnCreateBatch != null) btnCreateBatch.setVisibility(View.VISIBLE);
                } else {
                    if (btnCreateBatch != null) btnCreateBatch.setVisibility(View.GONE);
                    List<String> names = new ArrayList<>();
                    for (BatchEntity b : batches) names.add(b.getName());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, names);
                    if (acSelectBatch != null) acSelectBatch.setAdapter(adapter);
                }
            }
        });
    }

    private void setupListeners() {
        if (cardImagePicker != null) {
            cardImagePicker.setOnClickListener(view -> mGetContent.launch("image/*"));
        }
        if (switchBidding != null) {
            switchBidding.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (layoutBiddingDetails != null) {
                    layoutBiddingDetails.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                }
            });
        }
        if (acSelectBatch != null) {
            acSelectBatch.setOnItemClickListener((parent, view, position, id) -> {
                selectedBatchId = batchList.get(position).getId();
            });
        }
        if (btnCreateBatch != null) {
            btnCreateBatch.setOnClickListener(v -> {
                if (getActivity() instanceof FeatureNavigationHost) {
                    ((FeatureNavigationHost) getActivity()).openCreateBatchScreen();
                }
            });
        }
        if (btnPost != null) {
            btnPost.setOnClickListener(view -> validateAndPost());
        }
    }

    private void validateAndPost() {
        UserEntity user = SessionManager.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Please log in to post", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedBatchId <= 0) {
            Toast.makeText(getContext(), "Please select a batch", Toast.LENGTH_SHORT).show();
            return;
        }

        String qtyStr = etQuantity != null && etQuantity.getText() != null ? etQuantity.getText().toString().trim() : "";
        String floorStr = etFloorPrice != null && etFloorPrice.getText() != null ? etFloorPrice.getText().toString().trim() : "";
        String buyNowStr = etBuyNowPrice != null && etBuyNowPrice.getText() != null ? etBuyNowPrice.getText().toString().trim() : "";
        String hoursStr = etDeadline != null && etDeadline.getText() != null ? etDeadline.getText().toString().trim() : "";

        if (qtyStr.isEmpty() || floorStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double qty = Double.parseDouble(qtyStr);
            double floor = Double.parseDouble(floorStr);
            double buyNow = buyNowStr.isEmpty() ? floor * 1.2 : Double.parseDouble(buyNowStr);
            long hours = hoursStr.isEmpty() ? 24 : Long.parseLong(hoursStr);
            long deadline = System.currentTimeMillis() + (hours * 3600000);

            // PREVENT DOUBLE SUBMISSION
            btnPost.setEnabled(false);

            executor.execute(() -> {
                try {
                    if (!(getActivity() instanceof FeatureNavigationHost)) {
                        throw new IllegalStateException("Host does not implement FeatureNavigationHost");
                    }
                    ((FeatureNavigationHost) getActivity()).publishMarketplaceListing(
                            user.getId(),
                            user.getName(),
                            qty,
                            etGrade != null ? etGrade.getText().toString() : "Standard",
                            floor,
                            buyNow,
                            analysisId,
                            deadline,
                            14.5995,
                            120.9842,
                            selectedBatchId
                    );

                    if (isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Listing published successfully!", Toast.LENGTH_LONG).show();
                            if (getActivity() instanceof FeatureNavigationHost) {
                                ((FeatureNavigationHost) getActivity()).selectHomeTab();
                            }
                        });
                    }
                } catch (Exception e) {
                    if (isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            btnPost.setEnabled(true);
                            Toast.makeText(getContext(), "Error publishing listing", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
