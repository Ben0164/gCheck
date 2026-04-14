package com.example.myapplication.core.common;

import androidx.fragment.app.Fragment;

public interface FeatureNavigationHost {
    void openFragment(Fragment fragment);
    void selectHomeTab();
    void selectScanTab();
    void openCreatePostScreen();
    void openLogbook();
    void openCreateBatchScreen();
    void openExpenseForBatch(long batchId);
    void publishMarketplaceListing(
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
    );
}
