package com.example.myapplication.feature.expense;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PhaseAdapter extends RecyclerView.Adapter<PhaseAdapter.PhaseViewHolder> {
    private final List<PhaseItem> phaseList;
    private final OnPhaseClickListener listener;
    public interface OnPhaseClickListener { void onPhaseClick(PhaseItem phase); }
    public PhaseAdapter(List<PhaseItem> phaseList, OnPhaseClickListener listener) {
        this.phaseList = phaseList;
        this.listener = listener;
    }
    @NonNull @Override
    public PhaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phase, parent, false);
        return new PhaseViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull PhaseViewHolder holder, int position) {
        PhaseItem item = phaseList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());
        holder.itemView.setOnClickListener(v -> listener.onPhaseClick(item));
    }
    @Override public int getItemCount() { return phaseList.size(); }
    static class PhaseViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;
        public PhaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_phase_title);
            tvSubtitle = itemView.findViewById(R.id.tv_phase_subtitle);
        }
    }
    public static class PhaseItem {
        private final String title;
        private final String subtitle;
        public PhaseItem(String title, String subtitle) { this.title = title; this.subtitle = subtitle; }
        public String getTitle() { return title; }
        public String getSubtitle() { return subtitle; }
    }
}
