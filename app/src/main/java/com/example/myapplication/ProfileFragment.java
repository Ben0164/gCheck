package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        View btnEditAvatar = view.findViewById(R.id.btn_edit_avatar);
        MaterialButton btnViewPosts = view.findViewById(R.id.btn_view_posts);
        MaterialButton btnViewHistory = view.findViewById(R.id.btn_view_history);
        MaterialCardView cardSignOut = view.findViewById(R.id.card_sign_out);

        if (btnEditAvatar != null) {
            btnEditAvatar.setOnClickListener(v -> openFragment(new EditProfileFragment()));
        }

        if (btnViewPosts != null) {
            btnViewPosts.setOnClickListener(v -> openFragment(new UserPostsFragment()));
        }

        if (btnViewHistory != null) {
            btnViewHistory.setOnClickListener(v -> openFragment(new UserHistoryFragment()));
        }

        if (cardSignOut != null) {
            cardSignOut.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).loadFragment(new LoginFragment());
                }
            });
        }

        return view;
    }

    private void openFragment(Fragment fragment) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).loadFragment(fragment);
        }
    }
}
