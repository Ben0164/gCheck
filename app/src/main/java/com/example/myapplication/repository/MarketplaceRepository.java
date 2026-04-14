package com.example.myapplication.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.model.MarketplaceItem;
import com.example.myapplication.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MarketplaceRepository {
    private static MarketplaceRepository instance;
    private final MutableLiveData<List<MarketplaceItem>> allItems = new MutableLiveData<>();
    private final MutableLiveData<MarketplaceItem> currentItem = new MutableLiveData<>();
    private List<MarketplaceItem> itemsList = new ArrayList<>();
    private User currentUser;

    private MarketplaceRepository() {
        // Initialize with sample data
        initializeSampleItems();
    }

    public static MarketplaceRepository getInstance() {
        if (instance == null) {
            instance = new MarketplaceRepository();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void initializeSampleItems() {
        // Sample marketplace items for testing
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date nextWeek = calendar.getTime();

        itemsList.add(new MarketplaceItem(
            UUID.randomUUID().toString(),
            "Fresh Organic Tomatoes",
            "High-quality organic tomatoes grown without pesticides. Perfect for salads and cooking.",
            "seller1",
            "Green Valley Farm",
            50.0,
            nextWeek
        ));

        itemsList.add(new MarketplaceItem(
            UUID.randomUUID().toString(),
            "Premium Rice Seeds",
            "High-yield rice seeds suitable for tropical climate. Disease resistant variety.",
            "seller2",
            "Golden Harvest Seeds",
            200.0,
            nextWeek
        ));

        itemsList.add(new MarketplaceItem(
            UUID.randomUUID().toString(),
            "Used Tractor - Good Condition",
            "Well-maintained tractor with 500 hours of use. Perfect for medium-sized farms.",
            "seller3",
            "Farm Equipment Co.",
            5000.0,
            nextWeek
        ));

        allItems.setValue(itemsList);
    }

    public LiveData<List<MarketplaceItem>> getAllItems() {
        return allItems;
    }

    public LiveData<MarketplaceItem> getCurrentItem() {
        return currentItem;
    }

    public void createItem(String title, String description, double startingPrice, 
                          Date endTime, String category, String location, int quantity, String unit) {
        if (currentUser == null) return;

        MarketplaceItem newItem = new MarketplaceItem(
            UUID.randomUUID().toString(),
            title,
            description,
            currentUser.getId(),
            currentUser.getName(),
            startingPrice,
            endTime
        );
        
        newItem.setCategory(category);
        newItem.setLocation(location);
        newItem.setQuantity(quantity);
        newItem.setUnit(unit);
        
        itemsList.add(0, newItem); // Add to beginning of list
        allItems.setValue(itemsList);
    }

    public void updateItem(String itemId, String title, String description, double startingPrice, 
                          Date endTime, String category, String location, int quantity, String unit) {
        for (int i = 0; i < itemsList.size(); i++) {
            MarketplaceItem item = itemsList.get(i);
            if (item.getId().equals(itemId)) {
                item.setTitle(title);
                item.setDescription(description);
                item.setStartingPrice(startingPrice);
                item.setEndTime(endTime);
                item.setCategory(category);
                item.setLocation(location);
                item.setQuantity(quantity);
                item.setUnit(unit);
                
                allItems.setValue(itemsList);
                currentItem.setValue(item);
                break;
            }
        }
    }

    public void deleteItem(String itemId) {
        for (int i = 0; i < itemsList.size(); i++) {
            if (itemsList.get(i).getId().equals(itemId)) {
                itemsList.remove(i);
                allItems.setValue(itemsList);
                break;
            }
        }
    }

    public boolean placeBid(String itemId, double bidAmount) {
        if (currentUser == null) return false;

        for (MarketplaceItem item : itemsList) {
            if (item.getId().equals(itemId)) {
                // Check if bid is higher than current bid
                if (bidAmount > item.getCurrentBid()) {
                    MarketplaceItem.Bid newBid = new MarketplaceItem.Bid(
                        UUID.randomUUID().toString(),
                        currentUser.getId(),
                        currentUser.getName(),
                        bidAmount
                    );
                    
                    item.addBid(newBid);
                    allItems.setValue(itemsList);
                    currentItem.setValue(item);
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public void getItemById(String itemId) {
        for (MarketplaceItem item : itemsList) {
            if (item.getId().equals(itemId)) {
                currentItem.setValue(item);
                break;
            }
        }
    }

    public List<MarketplaceItem> getItemsByCategory(String category) {
        List<MarketplaceItem> filteredItems = new ArrayList<>();
        for (MarketplaceItem item : itemsList) {
            if (category == null || category.equals("All") || 
                (item.getCategory() != null && item.getCategory().equals(category))) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    public List<MarketplaceItem> getItemsBySeller(String sellerId) {
        List<MarketplaceItem> sellerItems = new ArrayList<>();
        for (MarketplaceItem item : itemsList) {
            if (item.getSellerId().equals(sellerId)) {
                sellerItems.add(item);
            }
        }
        return sellerItems;
    }

    public List<MarketplaceItem> getMyBids() {
        List<MarketplaceItem> myBidItems = new ArrayList<>();
        if (currentUser == null) return myBidItems;

        for (MarketplaceItem item : itemsList) {
            for (MarketplaceItem.Bid bid : item.getBidHistory()) {
                if (bid.getBidderId().equals(currentUser.getId())) {
                    myBidItems.add(item);
                    break;
                }
            }
        }
        return myBidItems;
    }

    public void searchItems(String query) {
        List<MarketplaceItem> searchResults = new ArrayList<>();
        String lowercaseQuery = query.toLowerCase();
        
        for (MarketplaceItem item : itemsList) {
            if (item.getTitle().toLowerCase().contains(lowercaseQuery) ||
                item.getDescription().toLowerCase().contains(lowercaseQuery) ||
                item.getSellerName().toLowerCase().contains(lowercaseQuery) ||
                (item.getCategory() != null && item.getCategory().toLowerCase().contains(lowercaseQuery))) {
                searchResults.add(item);
            }
        }
        
        allItems.setValue(searchResults);
    }

    public void refreshItems() {
        allItems.setValue(itemsList);
    }

    public void endAuction(String itemId) {
        for (MarketplaceItem item : itemsList) {
            if (item.getId().equals(itemId)) {
                item.setActive(false);
                allItems.setValue(itemsList);
                break;
            }
        }
    }
}
