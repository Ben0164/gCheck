package com.example.myapplication.feature.collaboration;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import java.util.List;

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
        holder.tvCaption.setText(post.getDescription());
        holder.tvTime.setText(post.getDate());

        // Display Image if available
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            holder.ivPostImage.setVisibility(View.VISIBLE);
            try {
                holder.ivPostImage.setImageURI(Uri.parse(post.getImageUrl()));
            } catch (Exception e) {
                holder.ivPostImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            holder.ivPostImage.setVisibility(View.GONE);
        }

        // Like count
        holder.tvLikeCount.setText(String.valueOf(post.getLikes()));

        // Comment count
        holder.tvCommentCount.setText(String.valueOf(post.getComments()));

        // Audience badge
        holder.tvAudienceBadge.setText("Public");
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvCaption, tvTime, tvLikeCount, tvCommentCount, tvAudienceBadge;
        ImageView ivPostImage;
        Chip chipPhase;

        ViewHolder(View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_post_author);
            tvCaption = itemView.findViewById(R.id.tv_post_caption);
            tvTime = itemView.findViewById(R.id.tv_post_time);
            ivPostImage = itemView.findViewById(R.id.iv_post_image);
            chipPhase = itemView.findViewById(R.id.chip_phase);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
            tvAudienceBadge = itemView.findViewById(R.id.tv_audience_badge);
        }
    }
}
