package com.example.myapplication.feature.marketplace.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myapplication.core.common.ProfitCalculator;
import com.example.myapplication.core.common.SessionManager;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.dao.TransactionDao;
import com.example.myapplication.core.data.entity.AnalysisEntity;
import com.example.myapplication.core.data.entity.BidEntity;
import com.example.myapplication.core.data.entity.ProductEntity;
import com.example.myapplication.core.data.entity.TransactionEntity;
import com.example.myapplication.core.data.entity.UserEntity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MarketplaceRepository {
    public static class ListingItem {
        public final long productId;
        public final String grade;
        public final double quantity;
        public final double floorPrice;
        public final double buyNowPrice;
        public final Double highestBid;
        public final long deadline;
        public final boolean isSold;
        public final String postType; // "AI_VERIFIED" or "DEMO"
        public final boolean isVerified;
        public final String verificationMethod;

        public ListingItem(long productId, String grade, double quantity, double floorPrice, double buyNowPrice,
                           Double highestBid, long deadline, boolean isSold, String postType, boolean isVerified, String verificationMethod) {
            this.productId = productId;
            this.grade = grade;
            this.quantity = quantity;
            this.floorPrice = floorPrice;
            this.buyNowPrice = buyNowPrice;
            this.highestBid = highestBid;
            this.deadline = deadline;
            this.isSold = isSold;
            this.postType = postType;
            this.isVerified = isVerified;
            this.verificationMethod = verificationMethod;
        }
    }

    public static class SmartBid {
        public final BidEntity bid;
        public final double distance;
        public final double netOffer;
        public final String maskedBuyerName;
        public final boolean isBestValue;
        public final boolean isHighest;

        public SmartBid(BidEntity bid, double distance, double netOffer, String maskedBuyerName, boolean isBestValue, boolean isHighest) {
            this.bid = bid;
            this.distance = distance;
            this.netOffer = netOffer;
            this.maskedBuyerName = maskedBuyerName;
            this.isBestValue = isBestValue;
            this.isHighest = isHighest;
        }
    }

    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<ListingItem>> listings = new MutableLiveData<>(new ArrayList<>());

    // Regional price for Bataan (example: ₱25/kg for rice)
    private static final double REGIONAL_PRICE_BATAAN = 25.0;
    // Default profit margin (10%)
    private static final double DEFAULT_PROFIT_MARGIN = 0.10;

    public MarketplaceRepository(Context context) {
        this.db = AppDatabase.getInstance(context);
        refreshListings();
    }

    public LiveData<List<ListingItem>> getListings() {
        refreshListings();
        return listings;
    }

    public LiveData<AnalysisEntity> getLatestAnalysis() {
        return db.analysisDao().getLatestLiveData();
    }

    public void postProduct(long farmerId, String farmerName, double quantity, String grade,
                            double totalExpenses, double buyNowPrice, long analysisId, long deadline,
                            double latitude, double longitude, long batchId, String postType, 
                            boolean isVerified, String verificationMethod) {
        executor.execute(() -> {
            // Calculate floor price using ProfitCalculator
            double calculatedFloorPrice = ProfitCalculator.calculateFloorPrice(totalExpenses, quantity, DEFAULT_PROFIT_MARGIN);
            
            ProductEntity p = new ProductEntity(
                    farmerId, farmerName, quantity, grade, calculatedFloorPrice, buyNowPrice,
                    analysisId, System.currentTimeMillis(), deadline, latitude, longitude, batchId
            );
            
            // Set hybrid posting fields
            p.setPostType(postType != null ? postType : (analysisId > 0 ? "AI_VERIFIED" : "MANUAL"));
            p.setVerified(isVerified);
            p.setVerificationMethod(verificationMethod);
            
            // Set initial bidding fields
            p.setListingStatus("ACTIVE");
            p.setCurrentHighestBid(0);
            p.setCurrentHighestBidderId(0);
            p.setFinalSalePrice(0);
            p.setWinningBuyerId(0);
            
            db.productDao().insert(p);
            refreshListings();
        });
    }

    public void placeBid(long productId, long buyerId, double bidAmount, Consumer<String> callback) {
        executor.execute(() -> {
            ProductEntity p = db.productDao().getById(productId);
            String message;
            
            if (p == null) {
                message = "Listing not found.";
            } else if (!"ACTIVE".equals(p.getListingStatus())) {
                message = "Bidding is closed for this listing.";
            } else if (p.getDeadline() < System.currentTimeMillis()) {
                message = "Bidding deadline has passed.";
            } else if (bidAmount < p.getPrice()) {
                message = "Bid is below floor price (₱" + String.format("%.2f", p.getPrice()) + ").";
            } else if (bidAmount <= p.getCurrentHighestBid()) {
                message = "Bid must be higher than current highest bid (₱" + String.format("%.2f", p.getCurrentHighestBid()) + ").";
            } else {
                // Place the bid
                db.bidDao().insert(new BidEntity(productId, buyerId, bidAmount, System.currentTimeMillis()));
                
                // Update product's highest bid
                db.productDao().updateHighestBid(productId, bidAmount, buyerId);
                
                message = "Bid placed successfully!";
            }
            refreshListings();
            callback.accept(message);
        });
    }

    public void getRankedBids(long productId, double productLat, double productLng, Consumer<List<SmartBid>> callback) {
        executor.execute(() -> {
            List<BidEntity> bids = db.bidDao().getBidsForProduct(productId);
            ProductEntity product = db.productDao().getById(productId);
            List<SmartBid> output = new ArrayList<>();
            double maxBid = 0.0;
            double bestNet = Double.NEGATIVE_INFINITY;

            // Get total expenses from batch if available
            double totalExpenses = 0.0;
            if (product != null && product.getBatchId() > 0) {
                List<com.example.myapplication.core.data.entity.ExpenseEntity> expenses = db.expenseDao().getExpensesByBatchSync(product.getBatchId());
                if (expenses != null) {
                    com.example.myapplication.core.common.ExpenseSummaryHelper helper = new com.example.myapplication.core.common.ExpenseSummaryHelper(expenses);
                    totalExpenses = helper.getGrandTotal();
                }
            }

            for (BidEntity bid : bids) {
                UserEntity buyer = findUserById(bid.getBuyerId());
                // Use buyer location from BidEntity if available, otherwise fallback to default
                double buyerLat = bid.getBuyerLatitude() != 0 ? bid.getBuyerLatitude() : 14.5995;
                double buyerLng = bid.getBuyerLongitude() != 0 ? bid.getBuyerLongitude() : 120.9842;
                
                double distance = estimateDistanceKm(productLat, productLng, buyerLat, buyerLng);
                
                // Calculate net profit using ProfitCalculator: (bidPrice × quantity) - haulingCost - totalExpenses
                double quantity = product != null ? product.getQuantity() : 0;
                double haulingCost = ProfitCalculator.calculateFulfillmentHauling(
                        java.math.BigDecimal.valueOf(quantity),
                        distance
                ).doubleValue();
                
                double revenue = bid.getBidAmount() * quantity;
                double netProfit = revenue - haulingCost - totalExpenses;
                
                if (bid.getBidAmount() > maxBid) maxBid = bid.getBidAmount();
                if (netProfit > bestNet) bestNet = netProfit;

                String name = buyer != null && buyer.getName() != null ? buyer.getName() : "Buyer";
                output.add(new SmartBid(
                        bid, distance, netProfit, maskName(name), false, false
                ));
            }

            List<SmartBid> finalized = new ArrayList<>();
            for (SmartBid sb : output) {
                boolean isHighest = sb.bid.getBidAmount() >= maxBid;
                boolean isBest = sb.netOffer >= bestNet;
                finalized.add(new SmartBid(sb.bid, sb.distance, sb.netOffer, sb.maskedBuyerName, isBest, isHighest));
            }

            finalized.sort(Comparator.comparingDouble((SmartBid b) -> b.netOffer).reversed());
            callback.accept(finalized);
        });
    }

    public void acceptBid(long productId, long bidId, Consumer<String> callback) {
        executor.execute(() -> {
            ProductEntity product = db.productDao().getById(productId);
            String message;
            
            if (product == null) {
                message = "Listing not found.";
            } else if (!"ACTIVE".equals(product.getListingStatus())) {
                message = "Listing is not active.";
            } else {
                // Get the winning bid
                BidEntity winningBid = db.bidDao().getBidsForProduct(productId).stream()
                        .max(Comparator.comparingDouble(BidEntity::getBidAmount))
                        .orElse(null);
                
                if (winningBid == null) {
                    message = "No bids to accept.";
                } else {
                    // Dynamic Price Selection: Use higher of bid price or regional price
                    double finalPrice = ProfitCalculator.calculateDynamicPrice(
                            winningBid.getBidAmount(), 
                            REGIONAL_PRICE_BATAAN
                    );
                    
                    // Calculate logistics cost (hauling)
                    double logisticsCost = ProfitCalculator.calculateFulfillmentHauling(
                            java.math.BigDecimal.valueOf(product.getQuantity()),
                            10.0 // Default distance - should be calculated based on buyer location
                    ).doubleValue();
                    
                    // Mark product as sold
                    db.productDao().markAsSold(productId, "SOLD", finalPrice, winningBid.getBuyerId());
                    
                    // Create transaction record
                    TransactionEntity transaction = new TransactionEntity(
                            productId,
                            winningBid.getBuyerId(),
                            product.getFarmerId(),
                            finalPrice,
                            product.getQuantity(),
                            System.currentTimeMillis()
                    );
                    db.transactionDao().insert(transaction);
                    
                    message = "Transaction completed! Final price: ₱" + String.format("%.2f", finalPrice) + "/kg";
                }
            }
            refreshListings();
            callback.accept(message);
        });
    }

    private void refreshListings() {
        executor.execute(() -> {
            List<ProductEntity> products = db.productDao().getAll();
            List<ListingItem> items = new ArrayList<>();
            for (ProductEntity p : products) {
                // Only show active listings
                if ("ACTIVE".equals(p.getListingStatus())) {
                    Double highest = db.bidDao().getHighestBid(p.getId());
                    items.add(new ListingItem(
                            p.getId(),
                            p.getGrade(),
                            p.getQuantity(),
                            p.getPrice(),
                            p.getBuyNowPrice(),
                            highest,
                            p.getDeadline(),
                            p.isSold(),
                            p.getPostType(),
                            p.isVerified(),
                            p.getVerificationMethod()
                    ));
                }
            }
            listings.postValue(items);
        });
    }

    public LiveData<List<ProductEntity>> getActiveListings() {
        return db.productDao().getActiveListings();
    }

    private UserEntity findUserById(long userId) {
        UserEntity current = SessionManager.getCurrentUser();
        if (current != null && current.getId() == userId) {
            return current;
        }
        return null;
    }

    private static String maskName(String name) {
        if (name == null || name.isEmpty()) return "Buyer";
        char first = name.charAt(0);
        return String.format(Locale.getDefault(), "Buyer %c***", first);
    }

    private static double estimateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = lat1 - lat2;
        double dLon = lon1 - lon2;
        return Math.sqrt((dLat * dLat) + (dLon * dLon)) * 111.0;
    }
}
