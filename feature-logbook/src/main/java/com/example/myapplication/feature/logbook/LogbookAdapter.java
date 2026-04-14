package com.example.myapplication.feature.logbook;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.core.common.PhaseCalculator;
import com.example.myapplication.core.data.entity.BatchEntity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogbookAdapter extends RecyclerView.Adapter<LogbookAdapter.ViewHolder> {
    private List<BatchEntity> batches = new ArrayList<>();
    private OnItemClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public interface OnItemClickListener { void onItemClick(BatchEntity batch); }
    public LogbookAdapter(OnItemClickListener listener) { this.listener = listener; }

    public void setBatches(List<BatchEntity> batches) {
        this.batches = batches;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_logbook, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BatchEntity batch = batches.get(position);
        holder.tvTitle.setText(batch.getName());
        String phase;
        if (batch.isCompleted()) {
            phase = "Completed";
            holder.tvStatusBadge.setText("DONE");
            updateBadgeColor(holder.tvStatusBadge, "#4CAF50");
        } else {
            if (batch.isManualOverride()) phase = batch.getManualPhase();
            else phase = PhaseCalculator.calculatePhase(batch.getStartDate(), System.currentTimeMillis()).phaseName;
            holder.tvStatusBadge.setText("IN PROGRESS");
            updateBadgeColor(holder.tvStatusBadge, "#FF9800");
        }
        holder.tvSubtitle.setText("Phase: " + phase);
        holder.tvDate.setText("Started: " + dateFormat.format(new Date(batch.getStartDate())));
        holder.tvProfit.setText("Target: " + String.format(Locale.getDefault(), "%.0f kg", batch.getTargetYield()));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(batch));
    }

    private void updateBadgeColor(TextView view, String colorCode) {
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        drawable.setColor(Color.parseColor(colorCode));
    }

    @Override
    public int getItemCount() { return batches.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle, tvDate, tvProfit, tvStatusBadge;
        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubtitle = itemView.findViewById(R.id.tv_subtitle);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvProfit = itemView.findViewById(R.id.tv_profit);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
        }
    }
}
