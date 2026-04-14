package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.myapplication.HomeFragment;
import com.example.myapplication.PriceFragment;
import com.example.myapplication.ExpenseFragment;
import com.example.myapplication.MapFragment;
import com.example.myapplication.core.common.SessionManager;
import com.example.myapplication.core.common.FeatureNavigationHost;
import com.example.myapplication.feature.auth.ui.login.LoginFragment;
import com.example.myapplication.feature.auth.ui.signup.SignupFragment;
import com.example.myapplication.feature.logbook.CreateBatchActivity;

public class MainActivity extends AppCompatActivity implements FeatureNavigationHost {
    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabScan = findViewById(R.id.fab_scan);

        // Check authentication status and load appropriate fragment
        if (savedInstanceState == null) {
            if (SessionManager.isLoggedIn()) {
                loadFragment(new HomeFragment());
            } else {
                loadFragment(new LoginFragment());
            }
        }

        // Setup bottom navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.navigation_market) {
                loadFragment(new PriceFragment());
                return true;
            } else if (itemId == R.id.navigation_scan) {
                loadFragment(new MapFragment());
                return true;
            } else if (itemId == R.id.navigation_inbox) {
                // TODO: Create InboxFragment for messaging
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // TODO: Create ProfileFragment
                return true;
            }
            return false;
        });

        // Setup scan FAB
        fabScan.setOnClickListener(v -> {
            loadFragment(new MapFragment());
        });

        // Handle back press with modern API
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof LoginFragment || currentFragment instanceof SignupFragment) {
                    // If user is not logged in and presses back on login/signup, exit app
                    if (!SessionManager.isLoggedIn()) {
                        finish();
                    } else {
                        setEnabled(false);
                        getOnBackPressedDispatcher().onBackPressed();
                    }
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();

        // Hide bottom navigation for auth fragments
        if (fragment instanceof LoginFragment || fragment instanceof SignupFragment) {
            bottomNavigationView.setVisibility(android.view.View.GONE);
            fabScan.setVisibility(android.view.View.GONE);
        } else {
            bottomNavigationView.setVisibility(android.view.View.VISIBLE);
            fabScan.setVisibility(android.view.View.VISIBLE);
        }
    }

    public void navigateToHome() {
        loadFragment(new HomeFragment());
        bottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
    }

    public void navigateToMarketplace() {
        loadFragment(new PriceFragment());
        bottomNavigationView.getMenu().findItem(R.id.navigation_market).setChecked(true);
    }

    public void navigateToExpense() {
        loadFragment(new ExpenseFragment());
        bottomNavigationView.getMenu().findItem(R.id.navigation_market).setChecked(true);
    }

    public void navigateToScan() {
        loadFragment(new MapFragment());
        bottomNavigationView.getMenu().findItem(R.id.navigation_scan).setChecked(true);
    }

    public void setBottomNavSelection(int navItemId) {
        bottomNavigationView.getMenu().findItem(navItemId).setChecked(true);
    }

    // Authentication navigation methods
    public void navigateToLogin() {
        loadFragment(new LoginFragment());
    }

    public void navigateToSignup() {
        loadFragment(new SignupFragment());
    }

    public void onLoginSuccess() {
        // Clear back stack and navigate to home
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        loadFragment(new HomeFragment());
        bottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
    }

    public void onLogout() {
        SessionManager.logout();
        navigateToLogin();
    }

    // FeatureNavigationHost interface implementation
    @Override
    public void openFragment(Fragment fragment) {
        loadFragment(fragment);
    }

    @Override
    public void selectHomeTab() {
        try {
            loadFragment(new HomeFragment());
            if (bottomNavigationView != null) {
                bottomNavigationView.setVisibility(android.view.View.VISIBLE);
                bottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
            }
            if (fabScan != null) {
                fabScan.setVisibility(android.view.View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: just load the fragment
            try {
                loadFragment(new HomeFragment());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void selectScanTab() {
        bottomNavigationView.getMenu().findItem(R.id.navigation_scan).setChecked(true);
    }

    @Override
    public void openCreatePostScreen() {
        // Navigate to create post activity
        Intent intent = new Intent(this, com.example.myapplication.feature.collaboration.ui.CreatePostActivity.class);
        startActivity(intent);
    }

    @Override
    public void openLogbook() {
        // Navigate to logbook fragment
        loadFragment(new com.example.myapplication.feature.logbook.LogbookFragment());
        bottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
    }

    @Override
    public void openCreateBatchScreen() {
        // Navigate to create batch activity
        Intent intent = new Intent(this, CreateBatchActivity.class);
        startActivity(intent);
    }

    @Override
    public void openExpenseForBatch(long batchId) {
        // Navigate to ExpenseFragment (Production Ledger) with batch ID
        ExpenseFragment fragment = new ExpenseFragment();
        Bundle args = new Bundle();
        args.putLong("BATCH_ID", batchId);
        fragment.setArguments(args);
        loadFragment(fragment);
    }

    @Override
    public void publishMarketplaceListing(
            long userId,
            String userName,
            double quantity,
            String grade,
            double floorPrice,
            double buyNowPrice,
            long analysisId,
            long deadline,
            double latitude,
            double longitude,
            long batchId
    ) {
        // Navigate to marketplace listing creation
        Intent intent = new Intent(this, com.example.myapplication.activity.CreateMarketplaceItemActivity.class);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("USER_NAME", userName);
        intent.putExtra("QUANTITY", quantity);
        intent.putExtra("GRADE", grade);
        intent.putExtra("FLOOR_PRICE", floorPrice);
        intent.putExtra("BUY_NOW_PRICE", buyNowPrice);
        intent.putExtra("ANALYSIS_ID", analysisId);
        intent.putExtra("DEADLINE", deadline);
        intent.putExtra("LATITUDE", latitude);
        intent.putExtra("LONGITUDE", longitude);
        intent.putExtra("BATCH_ID", batchId);
        startActivity(intent);
    }
}
