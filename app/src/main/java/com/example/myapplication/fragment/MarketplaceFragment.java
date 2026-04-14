package com.example.myapplication.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activity.CreateMarketplaceItemActivity;
import com.example.myapplication.adapter.MarketplaceAdapter;
import com.example.myapplication.model.MarketplaceItem;
import com.example.myapplication.repository.MarketplaceRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MarketplaceFragment extends Fragment implements MarketplaceAdapter.OnMarketplaceItemClickListener {
    private RecyclerView recyclerView;
    private MarketplaceAdapter marketplaceAdapter;
    private FloatingActionButton fabCreateItem;
    private MarketplaceRepository marketplaceRepository;
    private List<MarketplaceItem> items;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marketplace, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupObservers();
        
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewMarketplace);
        fabCreateItem = view.findViewById(R.id.fabCreateItem);
        
        marketplaceRepository = MarketplaceRepository.getInstance();
        
        fabCreateItem.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateMarketplaceItemActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        marketplaceAdapter = new MarketplaceAdapter(getContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(marketplaceAdapter);
    }

    private void setupObservers() {
        marketplaceRepository.getAllItems().observe(getViewLifecycleOwner(), new Observer<List<MarketplaceItem>>() {
            @Override
            public void onChanged(List<MarketplaceItem> itemList) {
                items = itemList;
                marketplaceAdapter.setItems(items);
            }
        });
    }

    @Override
    public void onItemClick(MarketplaceItem item) {
        marketplaceRepository.getItemById(item.getId());
        // TODO: Navigate to MarketplaceItemDetailActivity
    }

    @Override
    public void onBidClick(MarketplaceItem item) {
        showBidDialog(item);
    }

    @Override
    public void onEditClick(MarketplaceItem item) {
        Intent intent = new Intent(getActivity(), CreateMarketplaceItemActivity.class);
        intent.putExtra("itemId", item.getId());
        intent.putExtra("title", item.getTitle());
        intent.putExtra("description", item.getDescription());
        intent.putExtra("startingPrice", item.getStartingPrice());
        intent.putExtra("category", item.getCategory());
        intent.putExtra("location", item.getLocation());
        intent.putExtra("quantity", item.getQuantity());
        intent.putExtra("unit", item.getUnit());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(MarketplaceItem item) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this marketplace item?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    marketplaceRepository.deleteItem(item.getId());
                    Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showBidDialog(MarketplaceItem item) {
        // Create a custom dialog for bidding
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Place Bid");
        
        // Create a simple input dialog (you can create a custom layout for better UI)
        android.widget.EditText bidInput = new android.widget.EditText(getContext());
        bidInput.setHint("Enter bid amount");
        bidInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        bidInput.setText(String.valueOf(item.getCurrentBid() + 10)); // Suggest higher bid
        
        builder.setView(bidInput);
        
        builder.setPositiveButton("Place Bid", (dialog, which) -> {
            try {
                double bidAmount = Double.parseDouble(bidInput.getText().toString());
                if (marketplaceRepository.placeBid(item.getId(), bidAmount)) {
                    Toast.makeText(getContext(), "Bid placed successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Bid must be higher than current bid", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid bid amount", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Cancel", null);
        
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh items when fragment resumes
        marketplaceRepository.refreshItems();
    }
}
