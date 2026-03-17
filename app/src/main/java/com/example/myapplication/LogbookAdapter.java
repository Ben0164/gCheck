package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LogbookAdapter extends RecyclerView.Adapter<LogbookAdapter.ViewHolder> {

    private List<HarvestRecord> records;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HarvestRecord record);
    }

    public LogbookAdapter(List<HarvestRecord> records, OnItemClickListener listener) {
        this.records = records;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_logbook, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HarvestRecord record = records.get(position);
        holder.tvTitle.setText(record.getTitle());
        holder.tvSubtitle.setText(record.getSubtitle());
        holder.tvDate.setText(record.getDate());
        holder.tvProfit.setText(record.getProfit());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(record));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle, tvDate, tvProfit;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubtitle = itemView.findViewById(R.id.tv_subtitle);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvProfit = itemView.findViewById(R.id.tv_profit);
        }
    }
}
