package com.example.myapplication.palay.ui.marketplace;

public class Bid {
    private String buyerName;
    private double price;
    private double distance;
    private double netOffer;

    public Bid(String buyerName, double price, double distance, double netOffer) {
        this.buyerName = buyerName;
        this.price = price;
        this.distance = distance;
        this.netOffer = netOffer;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public double getPrice() {
        return price;
    }

    public double getDistance() {
        return distance;
    }

    public double getNetOffer() {
        return netOffer;
    }
}
