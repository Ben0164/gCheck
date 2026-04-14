package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.feature.logbook.LogbookFragment;
import com.example.myapplication.feature.collaboration.CollaborationFragment;
import com.example.myapplication.feature.collaboration.CreatePostFragment;
import com.example.myapplication.feature.collaboration.PostAdapter;
import com.example.myapplication.feature.collaboration.PostModel;
import com.example.myapplication.feature.collaboration.PostRepository;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.ExpenseEntity;
import com.example.myapplication.core.data.entity.UserEntity;
import com.example.myapplication.core.common.SessionManager;
import com.example.myapplication.feature.marketplace.repository.MarketplaceRepository;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private PostAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        UserEntity currentUser = SessionManager.getCurrentUser();
        long userId = (currentUser != null) ? currentUser.getId() : -1;

        // Dashboard Summary Data
        TextView tvTotalExpenses = view.findViewById(R.id.tv_total_expenses_home);
        if (tvTotalExpenses != null) {
            tvTotalExpenses.setText("₱0.00");
        }

        if (userId != -1) {
            try {
                AppDatabase.getInstance(requireContext()).expenseDao().getExpensesByUser(userId).observe(getViewLifecycleOwner(), expenses -> {
                    if (expenses != null && !expenses.isEmpty()) {
                        double total = 0;
                        for (ExpenseEntity e : expenses) {
                            total += e.getTotalCost();
                        }
                        if (tvTotalExpenses != null) {
                            tvTotalExpenses.setText(String.format(Locale.getDefault(), "₱%.2f", total));
                        }
                    }
                });
            } catch (Exception e) {
                // Handle database error gracefully
                if (tvTotalExpenses != null) {
                    tvTotalExpenses.setText("₱0.00");
                }
            }
        }

        // Quick Actions
        View cardPostAction = view.findViewById(R.id.card_post_action);
        View cardMarket = view.findViewById(R.id.card_market);
        View cardCollaboration = view.findViewById(R.id.card_collaboration);
        View cardCalculator = view.findViewById(R.id.card_calculator);

        if (cardPostAction != null) {
            cardPostAction.setOnClickListener(v -> openFragment(new CreatePostFragment(), -1));
        }
        if (cardMarket != null) {
            cardMarket.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).setBottomNavSelection(R.id.navigation_market);
                }
            });
        }
        if (cardCollaboration != null) {
            cardCollaboration.setOnClickListener(v -> openFragment(new CollaborationFragment(), -1));
        }
        if (cardCalculator != null) {
            cardCalculator.setOnClickListener(v -> openFragment(new LogbookFragment(), -1));
        }

        // Latest Analysis Section
        View cardLatestAnalysis = view.findViewById(R.id.card_latest_analysis);
        TextView tvHomeGood = view.findViewById(R.id.tv_home_good);
        TextView tvHomeGrade = view.findViewById(R.id.tv_home_grade);
        TextView tvHomePrice = view.findViewById(R.id.tv_home_price);
        
        MarketplaceRepository marketplaceRepository = new MarketplaceRepository(requireContext());
        marketplaceRepository.getLatestAnalysis().observe(getViewLifecycleOwner(), latest -> {
            if (latest != null) {
                if (cardLatestAnalysis != null) cardLatestAnalysis.setVisibility(View.VISIBLE);
                if (tvHomeGood != null) tvHomeGood.setText(String.format(Locale.getDefault(), "%d%% Good", latest.getGoodPercentage()));
                if (tvHomeGrade != null) tvHomeGrade.setText(latest.getGrade());
                if (tvHomePrice != null) tvHomePrice.setText(String.format(Locale.getDefault(), "₱%.2f/kg", latest.getPrice()));
            } else {
                if (cardLatestAnalysis != null) cardLatestAnalysis.setVisibility(View.GONE);
            }
        });

        // Recent Market Activity Section
        View tvHomePostsTitle = view.findViewById(R.id.tv_home_posts_title);
        RecyclerView rvHomePosts = view.findViewById(R.id.rv_home_posts);
        
        List<PostModel> posts = PostRepository.getAllPosts();
        if (posts.isEmpty()) {
            if (tvHomePostsTitle != null) tvHomePostsTitle.setVisibility(View.GONE);
            if (rvHomePosts != null) rvHomePosts.setVisibility(View.GONE);
        } else {
            if (tvHomePostsTitle != null) tvHomePostsTitle.setVisibility(View.VISIBLE);
            if (rvHomePosts != null) {
                rvHomePosts.setVisibility(View.VISIBLE);
                rvHomePosts.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter = new PostAdapter(posts);
                rvHomePosts.setAdapter(adapter);
            }
        }

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
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.loadFragment(fragment);
            if (navItemId != -1) {
                mainActivity.setBottomNavSelection(navItemId);
            }
        }
    }
}
