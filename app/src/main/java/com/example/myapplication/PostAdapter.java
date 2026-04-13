package com.example.myapplication;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<PostModel> posts;

    public PostAdapter(List<PostModel> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostModel post = posts.get(position);
        holder.tvAuthor.setText(post.getAuthorName());
        holder.tvTitle.setText(post.getTitle());
        holder.tvDate.setText(post.getDate());
        
        // Extended Fields
        holder.tvPrice.setText(String.format(Locale.getDefault(), "₱%.2f/kg", post.getAskingPrice()));
        holder.tvGrade.setText("Grade " + (post.getGrade() != null ? post.getGrade() : "N/A"));
        holder.tvMoisture.setText(post.getMoisture() + "% Moisture");
        holder.tvStatus.setText(post.getStatus());
        
        // Mock Distance
        double distance = post.calculateDistance(14.5995, 120.9842); // Reference to Bataan
        holder.tvDistance.setText(String.format(Locale.getDefault(), "📍 %.1f km", distance));

        // Status Badge Color
        if ("Verified".equals(post.getStatus())) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_bubble_sent); // Assuming green bubble
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_bubble_received); // Assuming gray/orange bubble
        }

        // Display Image if available
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            holder.ivPostImage.setVisibility(View.VISIBLE);
            try {
                holder.ivPostImage.setImageURI(Uri.parse(post.getImageUrl()));
            } catch (Exception e) {
                holder.ivPostImage.setImageResource(R.drawable.ic_image);
            }
        } else {
            holder.ivPostImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvTitle, tvDate, tvPrice, tvGrade, tvMoisture, tvStatus, tvDistance;
        ImageView ivPostImage;

        ViewHolder(View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_post_author);
            tvTitle = itemView.findViewById(R.id.tv_post_title);
            tvDate = itemView.findViewById(R.id.tv_post_date);
            tvPrice = itemView.findViewById(R.id.tv_item_price);
            tvGrade = itemView.findViewById(R.id.tv_item_grade);
            tvMoisture = itemView.findViewById(R.id.tv_item_moisture);
            tvStatus = itemView.findViewById(R.id.tv_status_badge);
            tvDistance = itemView.findViewById(R.id.tv_item_distance);
            ivPostImage = itemView.findViewById(R.id.iv_post_image);
        }
    }
}
