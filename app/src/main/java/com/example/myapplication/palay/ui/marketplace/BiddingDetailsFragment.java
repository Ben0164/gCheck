package com.example.myapplication.palay.ui.marketplace;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.core.common.ExpenseSummaryHelper;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.ExpenseEntity;
import com.example.myapplication.core.data.entity.ProductEntity;
import com.example.myapplication.palay.data.repository.MarketplaceRepository;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class BiddingDetailsFragment extends Fragment {

    private static final String TAG = "GrainCheckDebug";
    private static final String ARG_PRODUCT_ID = "product_id";
    private static final String ARG_BATCH_ID = "batch_id";
    private static final String ARG_INCLUDE_IMPLICIT = "include_implicit";

    private long productId;
    private long batchId;
    private boolean includeImplicit;
    
    private MarketplaceRepository repository;
    private AppDatabase db;
    private BidsAdapter adapter;
    private TextView tvTitle, tvTimer, tvErrorMessage;
    private View layoutContent;
    private CountDownTimer countDownTimer;

    public static BiddingDetailsFragment newInstance(long productId, long batchId, boolean includeImplicit) {
        BiddingDetailsFragment fragment = new BiddingDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PRODUCT_ID, productId);
        args.putLong(ARG_BATCH_ID, batchId);
        args.putBoolean(ARG_INCLUDE_IMPLICIT, includeImplicit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getLong(ARG_PRODUCT_ID);
            batchId = getArguments().getLong(ARG_BATCH_ID, -1);
            includeImplicit = getArguments().getBoolean(ARG_INCLUDE_IMPLICIT, false);
        }
        repository = new MarketplaceRepository(requireContext());
        db = AppDatabase.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bidding_details, container, false);

        tvTitle = view.findViewById(R.id.tv_detail_title);
        tvTimer = view.findViewById(R.id.tv_detail_timer);
        tvErrorMessage = view.findViewById(R.id.tv_error_message);
        layoutContent = view.findViewById(R.id.layout_bidding_content);
        RecyclerView rv = view.findViewById(R.id.rv_bids);

        Toolbar toolbar = view.findViewById(R.id.toolbar_bids);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }

        if (batchId <= 0) {
            showError("Select a batch to see accurate profit evaluation");
            return view;
        }

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new BidsAdapter(true, this::acceptBid);
        rv.setAdapter(adapter);

        loadData();
        return view;
    }

    private void showError(String msg) {
        if (tvErrorMessage != null) {
            tvErrorMessage.setText(msg);
            tvErrorMessage.setVisibility(View.VISIBLE);
        }
        if (layoutContent != null) layoutContent.setVisibility(View.GONE);
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            ProductEntity product = db.productDao().getById(productId);
            List<ExpenseEntity> expenses = db.expenseDao().getExpensesByBatchSync(batchId);
            
            if (product == null) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> showError("Product not found"));
                }
                return;
            }

            double yield = product.getQuantity();
            if (yield <= 0) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> showError("Invalid yield data"));
                }
                return;
            }


            ExpenseSummaryHelper helper = new ExpenseSummaryHelper(expenses);
            double totalCost = includeImplicit ? helper.getGrandTotal() : helper.getTotalExplicitCost();
            double costPerKg = totalCost / yield;

            double lat = product.getLatitude();
            double lng = product.getLongitude();

            repository.getRankedBids(productId, lat, lng, rankedBids -> {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        tvTitle.setText("Listing #" + productId + " (" + product.getGrade() + ")");
                        startTimer(product.getDeadline());
                        
                        // Pass production cost to adapter for True Profit calculation
                        adapter.setProductionCostPerKg(costPerKg);
                        adapter.submitList(rankedBids);
                        
                        if (rankedBids.isEmpty()) {
                            Toast.makeText(getContext(), "No buyer offers yet", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });
    }

    private void startTimer(long deadline) {
        if (countDownTimer != null) countDownTimer.cancel();
        long millis = deadline - System.currentTimeMillis();
        if (millis > 0) {
            countDownTimer = new CountDownTimer(millis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long secs = millisUntilFinished / 1000;
                    tvTimer.setText(String.format(Locale.getDefault(), "Time Left: %02d:%02d:%02d", 
                            secs/3600, (secs%3600)/60, secs%60));
                }
                @Override
                public void onFinish() {
                    tvTimer.setText("Bidding Ended");
                }
            }.start();
        } else {
            tvTimer.setText("Bidding Ended");
        }
    }

    private void acceptBid(MarketplaceRepository.SmartBid smartBid) {
        repository.acceptBid(productId, smartBid.bid.getId());
        Toast.makeText(requireContext(), "Offer Accepted! Buyer notified.", Toast.LENGTH_LONG).show();
        requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
