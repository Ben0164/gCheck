package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.MarketplaceItem;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MarketplaceAdapter extends RecyclerView.Adapter<MarketplaceAdapter.MarketplaceViewHolder> {
    private Context context;
    private List<MarketplaceItem> items;
    private OnMarketplaceItemClickListener listener;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    public interface OnMarketplaceItemClickListener {
        void onItemClick(MarketplaceItem item);
        void onBidClick(MarketplaceItem item);
        void onEditClick(MarketplaceItem item);
        void onDeleteClick(MarketplaceItem item);
    }

    public MarketplaceAdapter(Context context, OnMarketplaceItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.items = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    public void setItems(List<MarketplaceItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MarketplaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_marketplace, parent, false);
        return new MarketplaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketplaceViewHolder holder, int position) {
        MarketplaceItem item = items.get(position);
        
        holder.textTitle.setText(item.getTitle());
        holder.textDescription.setText(item.getDescription());
        holder.textSeller.setText(item.getSellerName());
        holder.textLocation.setText(item.getLocation());
        
        // Format price
        holder.textCurrentBid.setText(String.format("Current Bid: $%.2f", item.getCurrentBid()));
        holder.textStartingPrice.setText(String.format("Starting: $%.2f", item.getStartingPrice()));
        
        // Format time remaining
        long timeRemaining = item.getTimeRemaining();
        String timeText;
        if (timeRemaining > 0) {
            long days = TimeUnit.MILLISECONDS.toDays(timeRemaining);
            long hours = TimeUnit.MILLISECONDS.toHours(timeRemaining) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining) % 60;
            
            if (days > 0) {
                timeText = String.format("%d days %d hrs left", days, hours);
            } else if (hours > 0) {
                timeText = String.format("%d hrs %d mins left", hours, minutes);
            } else {
                timeText = String.format("%d mins left", minutes);
            }
        } else {
            timeText = "Auction Ended";
        }
        
        holder.textTimeRemaining.setText(timeText);
        
        // Show end time
        holder.textEndTime.setText("Ends: " + timeFormat.format(item.getEndTime()));
        
        // Category
        if (item.getCategory() != null) {
            holder.textCategory.setText(item.getCategory());
            holder.textCategory.setVisibility(View.VISIBLE);
        } else {
            holder.textCategory.setVisibility(View.GONE);
        }
        
        // Quantity and unit
        if (item.getQuantity() > 0 && item.getUnit() != null) {
            holder.textQuantity.setText(String.format("%d %s", item.getQuantity(), item.getUnit()));
            holder.textQuantity.setVisibility(View.VISIBLE);
        } else {
            holder.textQuantity.setVisibility(View.GONE);
        }
        
        // Load first image if available
        if (item.getImageUrls() != null && !item.getImageUrls().isEmpty()) {
            holder.imageItem.setVisibility(View.VISIBLE);
            Picasso.get().load(item.getImageUrls().get(0)).into(holder.imageItem);
        } else {
            holder.imageItem.setVisibility(View.GONE);
        }
        
        // Set auction status
        if (!item.isActive() || item.isAuctionEnded()) {
            holder.buttonBid.setEnabled(false);
            holder.buttonBid.setText("Ended");
            holder.textTimeRemaining.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.buttonBid.setEnabled(true);
            holder.buttonBid.setText("Place Bid");
            holder.textTimeRemaining.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        }
        
        // Set click listeners
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.buttonBid.setOnClickListener(v -> listener.onBidClick(item));
        holder.buttonEdit.setOnClickListener(v -> listener.onEditClick(item));
        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MarketplaceViewHolder extends RecyclerView.ViewHolder {
        ImageView imageItem;
        TextView textTitle;
        TextView textDescription;
        TextView textSeller;
        TextView textLocation;
        TextView textCategory;
        TextView textCurrentBid;
        TextView textStartingPrice;
        TextView textTimeRemaining;
        TextView textEndTime;
        TextView textQuantity;
        Button buttonBid;
        ImageButton buttonEdit;
        ImageButton buttonDelete;

        public MarketplaceViewHolder(@NonNull View itemView) {
            super(itemView);
            imageItem = itemView.findViewById(R.id.imageItem);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
            textSeller = itemView.findViewById(R.id.textSeller);
            textLocation = itemView.findViewById(R.id.textLocation);
            textCategory = itemView.findViewById(R.id.textCategory);
            textCurrentBid = itemView.findViewById(R.id.textCurrentBid);
            textStartingPrice = itemView.findViewById(R.id.textStartingPrice);
            textTimeRemaining = itemView.findViewById(R.id.textTimeRemaining);
            textEndTime = itemView.findViewById(R.id.textEndTime);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            buttonBid = itemView.findViewById(R.id.buttonBid);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
