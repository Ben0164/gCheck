package com.example.myapplication.feature.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.myapplication.R;
import com.example.myapplication.core.data.entity.UserEntity;
import com.example.myapplication.palay.data.repository.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvUsername, tvBio;
    private TextView tvStatListings, tvStatBatches, tvStatConnections;
    private ImageView ivProfileImage;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        loadUserData();

        MaterialButton btnEdit = view.findViewById(R.id.btn_edit_profile);
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                // Navigation will be handled by the hosting activity or via a shared interface
            });
        }

        return view;
    }

    private void initViews(View v) {
        tvProfileName = v.findViewById(R.id.tv_profile_name);
        tvUsername = v.findViewById(R.id.tv_profile_username);
        tvBio = v.findViewById(R.id.tv_profile_bio);
        ivProfileImage = v.findViewById(R.id.iv_profile_avatar);
        
        tvStatListings = v.findViewById(R.id.tv_stat_listings);
        tvStatBatches = v.findViewById(R.id.tv_stat_batches);
        tvStatConnections = v.findViewById(R.id.tv_stat_connections);

        viewPager = v.findViewById(R.id.view_pager_profile);
        tabLayout = v.findViewById(R.id.tab_layout_profile);
    }

    private void loadUserData() {
        UserEntity user = SessionManager.getCurrentUser();
        if (user != null) {
            if (tvProfileName != null) tvProfileName.setText(user.getName());
            if (tvUsername != null) tvUsername.setText(user.getUsername() != null ? "@" + user.getUsername() : "@user");
            if (tvBio != null) tvBio.setText(user.getBio() != null ? user.getBio() : "No bio yet.");
            
            // Set stats
            if (tvStatListings != null) tvStatListings.setText("0");
            if (tvStatBatches != null) tvStatBatches.setText("0");
            if (tvStatConnections != null) tvStatConnections.setText("0");
        }
    }
}
