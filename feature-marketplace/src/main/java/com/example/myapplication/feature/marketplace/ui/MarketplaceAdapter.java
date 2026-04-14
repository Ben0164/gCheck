package com.example.myapplication.feature.marketplace.ui;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.feature.marketplace.R;
import com.example.myapplication.feature.marketplace.repository.MarketplaceRepository;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MarketplaceAdapter extends RecyclerView.Adapter<MarketplaceAdapter.Holder> {
    public interface OnBidClickListener {
        void onBidClick(MarketplaceRepository.ListingItem item);
    }

    private final List<MarketplaceRepository.ListingItem> listings = new ArrayList<>();
    private final OnBidClickListener onBidClickListener;

    public MarketplaceAdapter(OnBidClickListener onBidClickListener) {
        this.onBidClickListener = onBidClickListener;
    }

    public void submitList(List<MarketplaceRepository.ListingItem> data) {
        listings.clear();
        listings.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_marketplace_listing, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        MarketplaceRepository.ListingItem item = listings.get(position);
        holder.tvGrade.setText("Grade " + item.grade);
        
        // Show verification badge
        if ("AI_VERIFIED".equals(item.postType) && item.isVerified) {
            holder.tvVerification.setVisibility(View.VISIBLE);
            holder.tvVerification.setText("✔ Verified by GCheck AI");
            holder.tvVerification.setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.profit_high));
            holder.tvVerification.setBackgroundColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.profit_high_light));
        } else {
            holder.tvVerification.setVisibility(View.VISIBLE);
            holder.tvVerification.setText("⚠ Demo Data • Not AI Verified");
            holder.tvVerification.setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.profit_loss));
            holder.tvVerification.setBackgroundColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.profit_loss_light));
        }
        holder.tvPrice.setText(String.format(Locale.getDefault(), "Floor: ₱ %.2f", item.floorPrice));
        holder.tvQuantity.setText(String.format(Locale.getDefault(), "%.2f kg", item.quantity));
        if (item.buyNowPrice > 0) {
            holder.tvBuyNow.setVisibility(View.VISIBLE);
            holder.tvBuyNow.setText(String.format(Locale.getDefault(), "Buy Now: ₱ %.2f", item.buyNowPrice));
        } else {
            holder.tvBuyNow.setVisibility(View.GONE);
        }
        if (item.highestBid != null) {
            holder.tvHighestBid.setText(String.format(Locale.getDefault(), "₱ %.2f", item.highestBid));
        } else {
            holder.tvHighestBid.setText("No bids yet");
        }
        if (holder.timer != null) holder.timer.cancel();
        long millisInFuture = item.deadline - System.currentTimeMillis();
        if (millisInFuture > 0 && !item.isSold) {
            holder.timer = new CountDownTimer(millisInFuture, 1000) {
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    holder.tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes % 60, seconds % 60));
                }
                public void onFinish() {
                    holder.tvTimer.setText("Bidding Closed");
                    holder.btnBid.setEnabled(false);
                }
            }.start();
        } else {
            holder.tvTimer.setText(item.isSold ? "SOLD" : "Bidding Closed");
            holder.btnBid.setEnabled(false);
        }
        holder.btnBid.setOnClickListener(v -> onBidClickListener.onBidClick(item));
    }

    @Override public int getItemCount() { return listings.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tvGrade, tvPrice, tvBuyNow, tvQuantity, tvHighestBid, tvTimer, tvVerification;
        MaterialButton btnBid;
        CountDownTimer timer;
        Holder(@NonNull View itemView) {
            super(itemView);
            tvGrade = itemView.findViewById(R.id.tv_item_grade);
            tvPrice = itemView.findViewById(R.id.tv_item_price);
            tvBuyNow = itemView.findViewById(R.id.tv_item_buy_now);
            tvQuantity = itemView.findViewById(R.id.tv_item_quantity);
            tvHighestBid = itemView.findViewById(R.id.tv_item_highest_bid);
            tvTimer = itemView.findViewById(R.id.tv_item_timer);
            tvVerification = itemView.findViewById(R.id.tv_item_verification);
            btnBid = itemView.findViewById(R.id.btn_item_place_bid);
        }
    }
}
