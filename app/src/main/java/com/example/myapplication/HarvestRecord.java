package com.example.myapplication;

public class HarvestRecord {
    private String title;
    private String subtitle;
    private String date;
    private String profit;

    public HarvestRecord(String title, String subtitle, String date, String profit) {
        this.title = title;
        this.subtitle = subtitle;
        this.date = date;
        this.profit = profit;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getDate() { return date; }
    public String getProfit() { return profit; }
}
