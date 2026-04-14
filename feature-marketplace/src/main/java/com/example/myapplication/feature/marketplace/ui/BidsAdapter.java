package com.example.myapplication.feature.marketplace.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.feature.marketplace.R;
import com.example.myapplication.feature.marketplace.repository.MarketplaceRepository;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BidsAdapter extends RecyclerView.Adapter<BidsAdapter.Holder> {
    public interface OnBidActionListener { void onAccept(MarketplaceRepository.SmartBid smartBid); }
    private final List<MarketplaceRepository.SmartBid> bids = new ArrayList<>();
    private final OnBidActionListener listener;
    private final boolean isFarmer;
    private double productionCostPerKg = 0;

    public BidsAdapter(boolean isFarmer, OnBidActionListener listener) {
        this.isFarmer = isFarmer;
        this.listener = listener;
    }

    public void setProductionCostPerKg(double cost) { this.productionCostPerKg = cost; }

    public void submitList(List<MarketplaceRepository.SmartBid> data) {
        bids.clear();
        bids.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bid, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        MarketplaceRepository.SmartBid sb = bids.get(position);
        double trueProfitPerKg = sb.netOffer - productionCostPerKg;
        holder.tvBuyer.setText(sb.maskedBuyerName);
        holder.tvAmount.setText(String.format(Locale.getDefault(), "₱ %.2f/kg", sb.bid.getBidAmount()));
        holder.tvDistance.setText(String.format(Locale.getDefault(), "%.1f km", sb.distance));
        holder.tvNetProfit.setText(String.format(Locale.getDefault(), "True Profit: ₱ %.2f/kg", trueProfitPerKg));
        if (trueProfitPerKg < 0) {
            holder.tvNetProfit.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.loss_negative));
        } else {
            holder.tvNetProfit.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.profit_positive));
        }
        holder.tvLabelBestValue.setVisibility(sb.isBestValue ? View.VISIBLE : View.GONE);
        holder.tvLabelHighestBid.setVisibility(sb.isHighest ? View.VISIBLE : View.GONE);
        holder.btnAccept.setVisibility(isFarmer ? View.VISIBLE : View.GONE);
        holder.btnAccept.setOnClickListener(v -> { if (listener != null) listener.onAccept(sb); });
    }

    @Override public int getItemCount() { return bids.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tvBuyer, tvAmount, tvDistance, tvNetProfit, tvLabelBestValue, tvLabelHighestBid;
        MaterialButton btnAccept;
        Holder(@NonNull View itemView) {
            super(itemView);
            tvBuyer = itemView.findViewById(R.id.tv_bid_buyer);
            tvAmount = itemView.findViewById(R.id.tv_bid_amount);
            tvDistance = itemView.findViewById(R.id.tv_bid_distance);
            tvNetProfit = itemView.findViewById(R.id.tv_bid_net_profit);
            tvLabelBestValue = itemView.findViewById(R.id.tv_label_best_value);
            tvLabelHighestBid = itemView.findViewById(R.id.tv_label_highest_bid);
            btnAccept = itemView.findViewById(R.id.btn_accept_bid);
        }
    }
}
