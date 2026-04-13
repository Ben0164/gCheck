package com.example.myapplication.palay.ui.marketplace;

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
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.HomeFragment;
import com.example.myapplication.CreatePostFragment;
import com.example.myapplication.core.data.entity.UserEntity;
import com.example.myapplication.palay.data.repository.MarketplaceRepository;
import com.example.myapplication.palay.data.repository.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MarketplaceFragment extends Fragment {

    private MarketplaceRepository repository;
    private MarketplaceAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marketplace, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_marketplace);
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.loadFragment(new HomeFragment());
                activity.setBottomNavSelection(R.id.navigation_home);
            }
        });

        repository = new MarketplaceRepository(requireContext());
        RecyclerView rv = view.findViewById(R.id.rv_marketplace);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MarketplaceAdapter(this::showBidDialog);
        rv.setAdapter(adapter);

        ExtendedFloatingActionButton fabPost = view.findViewById(R.id.fab_post_product);
        fabPost.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new CreatePostFragment());
            }
        });

        observeListings();
        return view;
    }

    private void observeListings() {
        repository.getListings().observe(getViewLifecycleOwner(), listings -> {
            if (listings != null) {
                adapter.submitList(listings);
            }
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
