package com.example.myapplication.palay.ui.marketplace;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.core.common.ProfitCalculator;
import com.google.android.material.button.MaterialButton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BidAdapter extends RecyclerView.Adapter<BidAdapter.BidViewHolder> {

    private List<Bid> bidList = new ArrayList<>();
    private OnBidClickListener listener;
    
    private double yieldKg = 0;
    private double explicitCost = 0;
    private double implicitCost = 0;
    private boolean useImplicit = false;
    private double maxPrice = 0;
    private double maxProfit = -Double.MAX_VALUE;

    public interface OnBidClickListener {
        void onAcceptClick(Bid bid);
    }

    public BidAdapter(OnBidClickListener listener) {
        this.listener = listener;
    }

    public void setProductionContext(double yield, double explicit, double implicit, boolean includeImplicit) {
        this.yieldKg = yield;
        this.explicitCost = explicit;
        this.implicitCost = implicit;
        this.useImplicit = includeImplicit;
        notifyDataSetChanged();
    }

    public void setBids(List<Bid> bids) {
        if (bids == null || bids.isEmpty()) {
            this.bidList = new ArrayList<>();
            notifyDataSetChanged();
            return;
        }

        // Optimization: Pre-calculate profit to avoid BigDecimal overhead during sort
        Map<String, Double> profitMap = new HashMap<>();
        maxPrice = 0;
        maxProfit = -Double.MAX_VALUE;

        for (Bid b : bids) {
            double profit = ProfitCalculator.calculateNetProfitFromBid(b, yieldKg, explicitCost, implicitCost, useImplicit).doubleValue();
            profitMap.put(b.getBuyerName() + b.getPrice(), profit); // Simple key
            if (b.getPrice() > maxPrice) maxPrice = b.getPrice();
            if (profit > maxProfit) maxProfit = profit;
        }

        Collections.sort(bids, (b1, b2) -> {
            Double p1 = profitMap.get(b1.getBuyerName() + b1.getPrice());
            Double p2 = profitMap.get(b2.getBuyerName() + b2.getPrice());
            return p2.compareTo(p1);
        });

        this.bidList = bids;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bid_item, parent, false);
        return new BidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BidViewHolder holder, int position) {
        Bid currentBid = bidList.get(position);
        android.content.Context context = holder.itemView.getContext();

        holder.tvBuyerName.setText(maskName(currentBid.getBuyerName()));
        holder.tvPrice.setText(context.getString(R.string.currency_per_kg_format, currentBid.getPrice()));
        
        // Hauling
        BigDecimal hauling = ProfitCalculator.calculateHaulingCost(yieldKg, currentBid.getDistance());
        String haulingStr = context.getString(R.string.currency_format, hauling.doubleValue());
        holder.tvHaulingDeduction.setText(context.getString(R.string.bid_less_transport, haulingStr));
        
        // Net Profit
        BigDecimal netProfit = ProfitCalculator.calculateNetProfitFromBid(currentBid, yieldKg, explicitCost, implicitCost, useImplicit);
        double profitVal = netProfit.doubleValue();
        holder.tvNetOffer.setText(context.getString(R.string.bid_net_profit, context.getString(R.string.currency_format, profitVal)));

        // Distance
        holder.tvDistance.setText(context.getString(R.string.bid_distance_away, currentBid.getDistance()));

        // Highlights
        boolean isBestProfit = (profitVal >= maxProfit && maxProfit > 0);
        boolean isHighestPrice = (currentBid.getPrice() >= maxPrice && maxPrice > 0);

        holder.tvLabelBest.setVisibility(isBestProfit ? View.VISIBLE : View.GONE);
        holder.tvLabelHighest.setVisibility(isHighestPrice ? View.VISIBLE : View.GONE);
        holder.tvReason.setVisibility(isBestProfit ? View.VISIBLE : View.GONE);
        holder.tvReason.setText(R.string.bid_reason_best_profit);

        // Status Badges
        double revenue = currentBid.getPrice() * yieldKg;
        if (profitVal < 0) {
            holder.tvStatusBadge.setText(R.string.status_loss);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.profit_loss));
        } else if (revenue > 0 && (profitVal / revenue) < 0.10) {
            holder.tvStatusBadge.setText(R.string.status_low_margin);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.profit_low));
        } else {
            holder.tvStatusBadge.setText(R.string.status_profitable);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.profit_high));
        }

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) listener.onAcceptClick(currentBid);
        });
    }

    @Override
    public int getItemCount() {
        return bidList.size();
    }

    private String maskName(String name) {
        if (name == null || name.length() < 2) return "Buyer B***";
        return "Buyer " + name.charAt(0) + "***";
    }

    static class BidViewHolder extends RecyclerView.ViewHolder {
        TextView tvBuyerName, tvPrice, tvDistance, tvNetOffer, tvHaulingDeduction, tvReason, tvStatusBadge;
        TextView tvLabelBest, tvLabelHighest;
        MaterialButton btnAccept;

        public BidViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBuyerName = itemView.findViewById(R.id.tv_bid_buyer);
            tvPrice = itemView.findViewById(R.id.tv_bid_amount);
            tvDistance = itemView.findViewById(R.id.tv_bid_distance);
            tvNetOffer = itemView.findViewById(R.id.tv_bid_net_profit);
            tvHaulingDeduction = itemView.findViewById(R.id.tv_hauling_deduction);
            tvReason = itemView.findViewById(R.id.tv_bid_reason);
            tvStatusBadge = itemView.findViewById(R.id.tv_bid_status_badge);
            tvLabelBest = itemView.findViewById(R.id.tv_label_best_value);
            tvLabelHighest = itemView.findViewById(R.id.tv_label_highest_bid);
            btnAccept = itemView.findViewById(R.id.btn_accept_bid);
        }
    }
}
