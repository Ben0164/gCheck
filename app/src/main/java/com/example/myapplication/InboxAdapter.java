package com.example.myapplication;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private List<Message> messages;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Message message);
    }

    public InboxAdapter(List<Message> messages, OnItemClickListener listener) {
        this.messages = messages;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.tvTitle.setText(message.getTitle());
        holder.tvDesc.setText(message.getDescription());
        holder.tvTimestamp.setText(message.getTimestamp());
        
        if (message.isRead()) {
            holder.viewIndicator.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.viewIndicator.setBackgroundColor(message.getColor());
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void markAllAsRead() {
        for (Message message : messages) {
            message.setRead(true);
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvTimestamp;
        View viewIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_inbox_title);
            tvDesc = itemView.findViewById(R.id.tv_inbox_desc);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            viewIndicator = itemView.findViewById(R.id.view_indicator);
        }
    }
}
