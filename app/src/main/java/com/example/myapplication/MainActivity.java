package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton fabScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        fabScan = findViewById(R.id.fab_scan);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.navigation_inbox) {
                loadFragment(new InboxFragment());
                return true;
            } else if (itemId == R.id.navigation_scan) {
                loadFragment(new GrainCheckFragment());
                return true;
            } else if (itemId == R.id.navigation_calculator) {
                loadFragment(new PriceFragment());
                return true;
            } else if (itemId == R.id.navigation_profile) {
                loadFragment(new ProfileFragment());
                return true;
            }
            return false;
        });

        fabScan.setOnClickListener(v -> {
            bottomNav.setSelectedItemId(R.id.navigation_scan);
        });

        // Start with Login screen
        if (savedInstanceState == null) {
            loadFragment(new LoginFragment());
            setNavigationVisibility(false);
        }
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        
        // Show/Hide bottom nav and FAB based on fragment
        if (fragment instanceof LoginFragment || fragment instanceof SignupFragment) {
            setNavigationVisibility(false);
        } else {
            setNavigationVisibility(true);
        }
    }

    private void setNavigationVisibility(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        bottomAppBar.setVisibility(visibility);
        fabScan.setVisibility(visibility);
        // Also adjust fragment container margin if needed, but for now just hide.
    }
    
    public void setBottomNavSelection(int itemId) {
        bottomNav.setSelectedItemId(itemId);
    }
}
