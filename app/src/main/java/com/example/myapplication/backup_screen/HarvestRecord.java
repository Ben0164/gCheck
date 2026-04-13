package com.example.myapplication.backup_screen;

public class HarvestRecord {
    private long id;
    private String title;
    private String subtitle;
    private String date;
    private String profit;

    public HarvestRecord(long id, String title, String subtitle, String date, String profit) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.date = date;
        this.profit = profit;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getDate() { return date; }
    public String getProfit() { return profit; }
}
