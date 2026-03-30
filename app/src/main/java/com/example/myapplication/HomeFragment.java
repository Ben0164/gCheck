package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {

    private PostAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MaterialCardView cardCalculator = view.findViewById(R.id.card_calculator);
        MaterialCardView cardInbox = view.findViewById(R.id.card_inbox);
        MaterialCardView cardMap = view.findViewById(R.id.card_map);
        MaterialCardView cardOpenInspector = view.findViewById(R.id.card_open_inspector);

        if (cardCalculator != null) {
            cardCalculator.setOnClickListener(v -> openFragment(new PriceFragment(), R.id.navigation_calculator));
        }
        if (cardInbox != null) {
            cardInbox.setOnClickListener(v -> openFragment(new InboxFragment(), R.id.navigation_inbox));
        }
        if (cardMap != null) {
            cardMap.setOnClickListener(v -> {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).loadFragment(new MapFragment());
                }
            });
        }
        if (cardOpenInspector != null) {
            cardOpenInspector.setOnClickListener(v -> openFragment(new GrainCheckFragment(), R.id.navigation_scan));
        }

        RecyclerView rvHomePosts = view.findViewById(R.id.rv_home_posts);
        rvHomePosts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostAdapter(PostRepository.getAllPosts());
        rvHomePosts.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void openFragment(Fragment fragment, int navItemId) {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).loadFragment(fragment);
            ((MainActivity) getActivity()).setBottomNavSelection(navItemId);
        }
    }
}
