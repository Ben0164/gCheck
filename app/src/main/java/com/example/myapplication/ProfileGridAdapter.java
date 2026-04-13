package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProfileGridAdapter extends RecyclerView.Adapter<ProfileGridAdapter.GridViewHolder> {

    private List<Integer> itemImages;

    public ProfileGridAdapter(List<Integer> itemImages) {
        this.itemImages = itemImages;
    }

    public void updateItems(List<Integer> newItems) {
        this.itemImages = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_grid, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        holder.imageView.setImageResource(itemImages.get(position));
    }

    @Override
    public int getItemCount() {
        return itemImages.size();
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_grid_item);
        }
    }
}
