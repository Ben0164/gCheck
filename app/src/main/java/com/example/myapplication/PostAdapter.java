package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
        holder.tvTitle.setText(post.getTitle());
        holder.tvDesc.setText(post.getDescription());
        holder.tvDate.setText(post.getDate());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvTitle, tvDesc, tvDate;

        ViewHolder(View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_post_author);
            tvTitle = itemView.findViewById(R.id.tv_post_title);
            tvDesc = itemView.findViewById(R.id.tv_post_desc);
            tvDate = itemView.findViewById(R.id.tv_post_date);
        }
    }
}
