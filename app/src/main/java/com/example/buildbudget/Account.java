package com.example.buildbudget;

public class Account {
    String type;
    String title;
    Double balance;

    String number;
    String holder;
    String provider;

    String year;
    String cvv;

    Account(String type, String title, Double balance) {
        this.type = type;
        this.title = title;
        this.balance = balance;
    }

    public void setInfo() {
        if (type == "cash") {

        } else if (type == "bank") {

        } else if (type == "card") {

        } else if (type == "mobile") {

        }
    }
}
