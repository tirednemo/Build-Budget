package com.example.buildbudget;

public class PlannedPayment {
    private String id;
    private double amount;
    private String description;
    private long date;

    public PlannedPayment() {
        // Required empty constructor for Firebase Realtime Database
    }

    public PlannedPayment(String id, double amount, String description, long date) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}

