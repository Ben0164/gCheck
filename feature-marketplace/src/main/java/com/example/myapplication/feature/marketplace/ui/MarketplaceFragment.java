package com.example.myapplication.feature.marketplace.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.core.common.FeatureNavigationHost;
import com.example.myapplication.core.common.SessionManager;
import com.example.myapplication.core.data.entity.UserEntity;
import com.example.myapplication.feature.marketplace.R;
import com.example.myapplication.feature.marketplace.repository.MarketplaceRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MarketplaceFragment extends Fragment {
    private MarketplaceRepository repository;
    private MarketplaceAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marketplace, container, false);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_marketplace);
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() instanceof FeatureNavigationHost) {
                ((FeatureNavigationHost) getActivity()).selectHomeTab();
            }
        });
        repository = new MarketplaceRepository(requireContext());
        RecyclerView rv = view.findViewById(R.id.rv_marketplace);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MarketplaceAdapter(this::showBidDialog);
        rv.setAdapter(adapter);
        ExtendedFloatingActionButton fabPost = view.findViewById(R.id.fab_post_product);
        fabPost.setOnClickListener(v -> {
            if (getActivity() instanceof FeatureNavigationHost) {
                ((FeatureNavigationHost) getActivity()).openCreatePostScreen();
            }
        });
        observeListings();
        return view;
    }

    private void observeListings() {
        repository.getListings().observe(getViewLifecycleOwner(), listings -> {
            if (listings != null) adapter.submitList(listings);
        });
    }

    private void showBidDialog(MarketplaceRepository.ListingItem item) {
        UserEntity currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Login first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!"buyer".equalsIgnoreCase(currentUser.getRole())) {
            Toast.makeText(requireContext(), R.string.role_buyer_only, Toast.LENGTH_SHORT).show();
            return;
        }
        BidDialogFragment dialog = BidDialogFragment.newInstance(item.floorPrice, bidAmount -> {
            repository.placeBid(item.productId, currentUser.getId(), bidAmount, message -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        observeListings();
                    });
                }
            });
        });
        dialog.show(getChildFragmentManager(), "bid_dialog");
    }
}
