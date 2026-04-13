package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.palay.data.adapter.ExpenseAdapter;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.ExpenseEntity;
import com.example.myapplication.core.data.entity.BatchEntity;
import com.example.myapplication.core.common.ExpenseSummaryHelper;
import com.example.myapplication.core.common.ProfitCalculator;
import com.example.myapplication.palay.data.PhaseCalculator;
import com.example.myapplication.palay.data.SuggestionHelper;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.button.MaterialButton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseFragment extends Fragment {

    private AppDatabase db;
    private TextView tvTotalExpenses, tvBreakevenPrice, tvExpectedYield;
    private TextView tvCostDisplayLabel, tvEstProfit, tvBreakevenHint;
    
    private TextView tvProductionCostSubtotal, tvInternalTransport, tvTransportToBuyer, tvGrandTotalAll;
    private TextView tvBatchStatus, tvLedgerTitle;
    
    private EditText etSellPrice;
    private View layoutImplicitSummary, layoutDashboardContent, layoutNoBatch;
    private MaterialSwitch switchProfitMode;
    private RecyclerView rvRecentExpenses;
    private ExpenseAdapter adapter;
    private MaterialButton btnCreateBatchFirst, btnCompleteBatch;

    // Phase Calculator Views
    private TextView tvCurrentPhaseDisplay, tvPhaseSuggestion;
    private LinearProgressIndicator phaseProgressBar;
    private MaterialButton btnChangePhase, btnResetAuto;
    
    private List<ExpenseEntity> currentExpenses = new ArrayList<>();
    private double currentTotalToDisplay = 0;
    
    private long currentBatchId = -1;
    private final double haulingDistanceKm = 10.0;
    private BatchEntity currentBatch;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentBatchId = getArguments().getLong("BATCH_ID", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        if (currentBatchId <= 0) {
            navigateToLogbook();
            return view;
        }

        db = AppDatabase.getInstance(requireContext());
        
        initViews(view);
        setupPhasesGrid(view);
        setupRecyclerView();

        switchProfitMode.setOnCheckedChangeListener((buttonView, isChecked) -> updateUI(currentExpenses));

        etSellPrice.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateEstimatedProfit();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnChangePhase.setOnClickListener(v -> showChangePhaseDialog());
        btnResetAuto.setOnClickListener(v -> resetToAutoMode());
        btnCompleteBatch.setOnClickListener(v -> handleEndCycle());

        loadCurrentBatchData();

        return view;
    }

    private void navigateToLogbook() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).loadFragment(new LogbookFragment());
        }
    }

    private void loadCurrentBatchData() {
        // Only load the specific batch passed to this fragment
        db.batchDao().getBatchById(currentBatchId).observe(getViewLifecycleOwner(), batch -> {
            if (batch != null) {
                currentBatch = batch;
                hideNoBatchLayout();
                if (tvLedgerTitle != null) {
                    tvLedgerTitle.setText(batch.getName());
                }
                updatePhaseUI();
                loadSummaryData();
            } else {
                showNoBatchLayout();
            }
        });
    }

    private void updatePhaseUI() {
        if (currentBatch == null || !isAdded()) return;

        boolean isCompleted = currentBatch.isCompleted();
        btnCompleteBatch.setVisibility(isCompleted ? View.GONE : View.VISIBLE);
        btnChangePhase.setEnabled(!isCompleted);
        btnResetAuto.setEnabled(!isCompleted);

        String currentPhase;
        long dayCount;

        if (isCompleted) {
            currentPhase = getString(R.string.harvest_completed_status);
            dayCount = -1;
            tvCurrentPhaseDisplay.setText(currentPhase);
            btnResetAuto.setVisibility(View.GONE);
        } else if (currentBatch.isManualOverride()) {
            currentPhase = currentBatch.getManualPhase();
            dayCount = -1;
            tvCurrentPhaseDisplay.setText(getString(R.string.current_phase_label, currentPhase) + " " + getString(R.string.manual_override_tag));
            btnResetAuto.setVisibility(View.VISIBLE);
        } else {
            PhaseCalculator.PhaseResult result = PhaseCalculator.calculatePhase(currentBatch.getStartDate(), System.currentTimeMillis());
            currentPhase = result.phaseName;
            dayCount = result.dayCount;
            tvCurrentPhaseDisplay.setText(getString(R.string.current_phase_label, currentPhase) + " " + getString(R.string.day_format, dayCount));
            btnResetAuto.setVisibility(View.GONE);
        }

        tvPhaseSuggestion.setText(SuggestionHelper.getSuggestion(currentPhase));
        int progress = (dayCount >= 0) ? (int) Math.min((dayCount * 100) / 130, 100) : 100;
        if (isCompleted) progress = 100;
        phaseProgressBar.setProgress(progress);
    }

    private void handleEndCycle() {
        if (currentBatch == null) return;
        if (currentBatch.getActualYieldKg() <= 0) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.yield_required_title)
                    .setMessage(R.string.yield_required_message)
                    .setPositiveButton(R.string.enter_actual_yield, (dialog, which) -> showYieldInputDialog())
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        } else {
            completeBatch();
        }
    }

    private void showYieldInputDialog() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint(R.string.actual_yield_hint);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.enter_actual_yield)
                .setView(input)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String val = input.getText().toString();
                    if (!val.isEmpty()) {
                        try {
                            double yield = Double.parseDouble(val);
                            if (yield > 0) {
                                currentBatch.setActualYieldKg(yield);
                                updateBatchInDb();
                                completeBatch();
                            } else {
                                Toast.makeText(getContext(), R.string.yield_positive_error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), R.string.error_invalid_number, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void completeBatch() {
        if (currentBatch == null) return;
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.end_batch_lifecycle_title)
                .setMessage(R.string.end_batch_lifecycle_message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    currentBatch.setCompleted(true);
                    currentBatch.setCompletedDate(System.currentTimeMillis());
                    updateBatchInDb();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showChangePhaseDialog() {
        if (getContext() == null || currentBatch == null || currentBatch.isCompleted()) return;
        
        String[] phases = {
            getString(R.string.phase_land_prep),
            getString(R.string.phase_crop_est),
            getString(R.string.phase_crop_mgmt),
            getString(R.string.phase_monitoring),
            getString(R.string.phase_harvest),
            getString(R.string.phase_post_harvest)
        };

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_select_phase)
                .setItems(phases, (dialog, which) -> {
                    currentBatch.setManualPhase(phases[which]);
                    currentBatch.setManualOverride(true);
                    updateBatchInDb();
                })
                .show();
    }

    private void resetToAutoMode() {
        if (currentBatch == null) return;
        currentBatch.setManualOverride(false);
        updateBatchInDb();
    }

    private void updateBatchInDb() {
        if (currentBatch == null) return;
        executor.execute(() -> db.batchDao().update(currentBatch));
    }

    private void showNoBatchLayout() {
        if (layoutNoBatch != null) layoutNoBatch.setVisibility(View.VISIBLE);
        if (layoutDashboardContent != null) layoutDashboardContent.setVisibility(View.GONE);
    }

    private void hideNoBatchLayout() {
        if (layoutNoBatch != null) layoutNoBatch.setVisibility(View.GONE);
        if (layoutDashboardContent != null) layoutDashboardContent.setVisibility(View.VISIBLE);
    }

    private void initViews(View view) {
        layoutDashboardContent = view.findViewById(R.id.layout_dashboard_content);
        layoutNoBatch = view.findViewById(R.id.layout_no_batch);
        btnCreateBatchFirst = view.findViewById(R.id.btn_create_batch_first);
        btnCompleteBatch = view.findViewById(R.id.btn_complete_batch);
        tvLedgerTitle = view.findViewById(R.id.tv_ledger_title);

        if (btnCreateBatchFirst != null) {
            btnCreateBatchFirst.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), CreateBatchActivity.class);
                startActivity(intent);
            });
        }

        tvTotalExpenses = view.findViewById(R.id.tv_total_expenses_all);
        tvBreakevenPrice = view.findViewById(R.id.tv_breakeven_price);
        tvExpectedYield = view.findViewById(R.id.tv_expected_yield);
        tvCostDisplayLabel = view.findViewById(R.id.tv_cost_display_label);
        tvEstProfit = view.findViewById(R.id.tv_est_profit);
        tvBreakevenHint = view.findViewById(R.id.tv_breakeven_hint);
        etSellPrice = view.findViewById(R.id.et_sell_price);
        
        tvProductionCostSubtotal = view.findViewById(R.id.tv_production_cost_subtotal); 
        tvInternalTransport = view.findViewById(R.id.tv_internal_transport);
        tvTransportToBuyer = view.findViewById(R.id.tv_transport_to_buyer);
        tvGrandTotalAll = view.findViewById(R.id.tv_grand_total_all);
        
        layoutImplicitSummary = view.findViewById(R.id.layout_implicit_summary);
        switchProfitMode = view.findViewById(R.id.switch_profit_mode);
        rvRecentExpenses = view.findViewById(R.id.rv_expenses);

        tvCurrentPhaseDisplay = view.findViewById(R.id.tv_current_phase_display);
        tvPhaseSuggestion = view.findViewById(R.id.tv_phase_suggestion);
        phaseProgressBar = view.findViewById(R.id.phase_progress_bar);
        btnChangePhase = view.findViewById(R.id.btn_change_phase);
        btnResetAuto = view.findViewById(R.id.btn_reset_auto);

        View btnBack = view.findViewById(R.id.btn_back_to_logbook);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> navigateToLogbook());
        }

        tvBatchStatus = view.findViewById(R.id.tv_batch_status);
        
        if (tvExpectedYield != null) {
            tvExpectedYield.setOnClickListener(v -> {
                if (currentBatch != null && !currentBatch.isCompleted()) {
                    showYieldInputDialog();
                }
            });
        }
    }

    private void setupPhasesGrid(View view) {
        View.OnClickListener phaseClickListener = v -> {
            if (currentBatchId <= 0) return;
            if (currentBatch != null && currentBatch.isCompleted()) {
                Toast.makeText(getContext(), R.string.viewing_only_completed, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getContext(), PhaseExpenseActivity.class);
            intent.putExtra("PHASE", (String) v.getTag());
            intent.putExtra("BATCH_ID", currentBatchId);
            startActivity(intent);
        };

        setPhaseTagAndListener(view, R.id.phase_land_prep, R.string.phase_land_prep, phaseClickListener);
        setPhaseTagAndListener(view, R.id.phase_crop_est, R.string.phase_crop_est, phaseClickListener);
        setPhaseTagAndListener(view, R.id.phase_crop_mgmt, R.string.phase_crop_mgmt, phaseClickListener);
        setPhaseTagAndListener(view, R.id.phase_monitoring, R.string.phase_monitoring, phaseClickListener);
        setPhaseTagAndListener(view, R.id.phase_harvest, R.string.phase_harvest, phaseClickListener);
        setPhaseTagAndListener(view, R.id.phase_post_harvest, R.string.phase_post_harvest, phaseClickListener);
    }

    private void setPhaseTagAndListener(View parent, int viewId, int stringResId, View.OnClickListener listener) {
        View phaseView = parent.findViewById(viewId);
        if (phaseView != null) {
            phaseView.setTag(getString(stringResId));
            phaseView.setOnClickListener(listener);
        }
    }

    private void setupRecyclerView() {
        rvRecentExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseAdapter(new ArrayList<>());
        adapter.setOnExpenseClickListener(expense -> {
            if (currentBatch != null && currentBatch.isCompleted()) {
                Toast.makeText(getContext(), R.string.viewing_only_completed, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getContext(), ExpenseLedgerActivity.class);
            intent.putExtra("EXPENSE_ID", expense.getId());
            intent.putExtra("BATCH_ID", currentBatchId);
            startActivity(intent);
        });
        rvRecentExpenses.setAdapter(adapter);
    }

    private void loadSummaryData() {
        if (currentBatchId <= 0) return;
        db.expenseDao().getExpensesByBatch(currentBatchId).observe(getViewLifecycleOwner(), expenses -> {
            if (expenses != null) {
                currentExpenses = expenses;
                adapter.setExpenses(expenses);
                updateUI(expenses);
            }
        });
    }

    private void updateUI(List<ExpenseEntity> expenses) {
        if (!isAdded() || currentBatch == null) return;
        
        double actualYield = currentBatch.getActualYieldKg();
        boolean hasYield = actualYield > 0;
        boolean isCompleted = currentBatch.isCompleted();

        ExpenseSummaryHelper helper = new ExpenseSummaryHelper(expenses);
        double explicitTotal = helper.getTotalExplicitCost();
        double internalHauling = helper.getTotalInternalHaulingCost();
        double implicitTotal = helper.getTotalImplicitCost();
        boolean includeImplicit = switchProfitMode.isChecked();
        
        BigDecimal fulfillmentHauling = ProfitCalculator.calculateFulfillmentHauling(
                BigDecimal.valueOf(actualYield), haulingDistanceKm);
        
        double subtotalSunk = explicitTotal + (includeImplicit ? implicitTotal : 0);
        double grandTotal = subtotalSunk + fulfillmentHauling.doubleValue();

        if (tvCostDisplayLabel != null) {
            tvCostDisplayLabel.setText(includeImplicit ? getString(R.string.total_economic_cost) : getString(R.string.cash_cost_explicit));
        }
        if (tvTotalExpenses != null) {
            tvTotalExpenses.setText(getString(R.string.currency_format, subtotalSunk));
        }

        if (layoutImplicitSummary != null) {
            layoutImplicitSummary.setVisibility(includeImplicit ? View.VISIBLE : View.GONE);
        }

        if (hasYield) {
            if (tvTransportToBuyer != null) tvTransportToBuyer.setText(getString(R.string.transport_to_buyer_label, fulfillmentHauling.doubleValue()));
            if (tvGrandTotalAll != null) tvGrandTotalAll.setText(getString(R.string.total_cost_all_label, grandTotal));
            
            double breakeven = ProfitCalculator.calculateBreakevenPrice(grandTotal, actualYield).doubleValue();
            if (tvBreakevenPrice != null) tvBreakevenPrice.setText(getString(R.string.currency_per_kg_format, breakeven));
            if (tvExpectedYield != null) tvExpectedYield.setText(getString(R.string.weight_kg_format, actualYield));
            if (tvBatchStatus != null) {
                tvBatchStatus.setText(isCompleted ? getString(R.string.status_completed) : getString(R.string.status_in_progress));
                tvBatchStatus.setTextColor(ContextCompat.getColor(requireContext(), isCompleted ? R.color.earth_green : R.color.gray_text));
            }
        } else {
            if (tvTransportToBuyer != null) tvTransportToBuyer.setText(getString(R.string.transport_not_available));
            if (tvGrandTotalAll != null) tvGrandTotalAll.setText(getString(R.string.awaiting_harvest_data));
            if (tvBreakevenPrice != null) tvBreakevenPrice.setText(getString(R.string.to_be_determined));
            if (tvExpectedYield != null) tvExpectedYield.setText(getString(R.string.tap_to_enter_yield));
            if (tvBatchStatus != null) {
                tvBatchStatus.setText(getString(R.string.status_in_progress));
                tvBatchStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_text));
            }
        }

        if (tvProductionCostSubtotal != null) {
            tvProductionCostSubtotal.setText(getString(R.string.production_cost_label, explicitTotal - internalHauling));
        }
        if (tvInternalTransport != null) {
            tvInternalTransport.setText(getString(R.string.internal_transport_label, internalHauling));
        }

        currentTotalToDisplay = grandTotal;
        calculateEstimatedProfit();
        
        // Lock UI if completed
        if (isCompleted) {
            if (btnChangePhase != null) btnChangePhase.setEnabled(false);
            if (btnResetAuto != null) btnResetAuto.setEnabled(false);
            if (btnCompleteBatch != null) btnCompleteBatch.setVisibility(View.GONE);
        }
    }

    private void calculateEstimatedProfit() {
        if (currentBatch == null) return;
        double actualYield = currentBatch.getActualYieldKg();
        
        String priceStr = etSellPrice.getText().toString();
        if (priceStr.isEmpty() || actualYield <= 0) {
            if (tvEstProfit != null) tvEstProfit.setText(actualYield <= 0 ? getString(R.string.awaiting_harvest_data) : getString(R.string.currency_format, 0.0));
            if (tvBreakevenHint != null) tvBreakevenHint.setText("");
            return;
        }

        try {
            double pricePerKg = Double.parseDouble(priceStr);
            double revenue = pricePerKg * actualYield;
            double profit = revenue - currentTotalToDisplay;
            
            if (tvEstProfit != null) tvEstProfit.setText(getString(R.string.currency_format, profit));
            
            double breakeven = currentTotalToDisplay / actualYield;
            if (tvBreakevenHint != null) {
                if (pricePerKg < breakeven) {
                    tvBreakevenHint.setText(getString(R.string.breakeven_warning));
                    tvBreakevenHint.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
                } else {
                    tvBreakevenHint.setText(getString(R.string.profitable_price));
                    tvBreakevenHint.setTextColor(ContextCompat.getColor(requireContext(), R.color.earth_green));
                }
            }
        } catch (Exception e) {
            if (tvEstProfit != null) tvEstProfit.setText(getString(R.string.currency_format, 0.0));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
