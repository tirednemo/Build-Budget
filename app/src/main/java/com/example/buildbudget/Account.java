package com.example.buildbudget;

public class Account {
    String type;
    String name;
    Double balance;
    String owner;

    Account(String type, String name, Double balance){
        this.type = type;
        this.name = name;
        this.balance = balance;
    }
}
