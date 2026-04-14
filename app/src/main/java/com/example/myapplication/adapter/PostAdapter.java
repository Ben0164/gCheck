package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.Post;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private Context context;
    private List<Post> posts;
    private OnPostClickListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnPostClickListener {
        void onPostClick(Post post);
        void onPostLike(Post post);
        void onPostComment(Post post);
        void onPostShare(Post post);
        void onPostEdit(Post post);
        void onPostDelete(Post post);
    }

    public PostAdapter(Context context, OnPostClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.posts = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        
        holder.textTitle.setText(post.getTitle());
        holder.textContent.setText(post.getContent());
        holder.textAuthor.setText(post.getAuthorName());
        holder.textDate.setText(dateFormat.format(post.getCreatedAt()));
        holder.textLikes.setText(String.valueOf(post.getLikes()));
        holder.textComments.setText(String.valueOf(post.getComments()));
        
        if (post.getCategory() != null) {
            holder.textCategory.setText(post.getCategory());
            holder.textCategory.setVisibility(View.VISIBLE);
        } else {
            holder.textCategory.setVisibility(View.GONE);
        }
        
        // Load post image if available
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            holder.imagePost.setVisibility(View.VISIBLE);
            Picasso.get().load(post.getImageUrl()).into(holder.imagePost);
        } else {
            holder.imagePost.setVisibility(View.GONE);
        }
        
        // Set like button state
        holder.buttonLike.setImageResource(post.isLiked() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        
        // Set click listeners
        holder.itemView.setOnClickListener(v -> listener.onPostClick(post));
        holder.buttonLike.setOnClickListener(v -> listener.onPostLike(post));
        holder.buttonComment.setOnClickListener(v -> listener.onPostComment(post));
        holder.buttonShare.setOnClickListener(v -> listener.onPostShare(post));
        holder.buttonEdit.setOnClickListener(v -> listener.onPostEdit(post));
        holder.buttonDelete.setOnClickListener(v -> listener.onPostDelete(post));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePost;
        TextView textTitle;
        TextView textContent;
        TextView textAuthor;
        TextView textDate;
        TextView textCategory;
        TextView textLikes;
        TextView textComments;
        ImageButton buttonLike;
        ImageButton buttonComment;
        ImageButton buttonShare;
        ImageButton buttonEdit;
        ImageButton buttonDelete;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePost = itemView.findViewById(R.id.imagePost);
            textTitle = itemView.findViewById(R.id.textTitle);
            textContent = itemView.findViewById(R.id.textContent);
            textAuthor = itemView.findViewById(R.id.textAuthor);
            textDate = itemView.findViewById(R.id.textDate);
            textCategory = itemView.findViewById(R.id.textCategory);
            textLikes = itemView.findViewById(R.id.textLikes);
            textComments = itemView.findViewById(R.id.textComments);
            buttonLike = itemView.findViewById(R.id.buttonLike);
            buttonComment = itemView.findViewById(R.id.buttonComment);
            buttonShare = itemView.findViewById(R.id.buttonShare);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
