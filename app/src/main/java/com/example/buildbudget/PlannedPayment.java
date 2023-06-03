package com.example.buildbudget;

public class PlannedPayment {
    private String name;


    private String id;
    private double amount;
    private String date;

    public PlannedPayment() {
        // Required empty constructor for Firebase Realtime Database
    }

    public PlannedPayment(String name, double amount,  String date,String id) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.id=id;
    }

    // Getters and setters

    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

