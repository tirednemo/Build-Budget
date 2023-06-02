package com.example.buildbudget;

public class Account {
    public String Type;
    public  String Number;
    public String Title;
    public  String Currency;
    public  Double Balance;


    public String Holder; //account/cardholder name
    public String Provider; //visa/mastercard
    public String Validity;
    public  String CVV;


    public Account() {
    }
    public Account(String type, String title, Double balance) {
        this.Type = type;
        this.Title = title;
        this.Balance = balance;
    }

    public Account(String type, String title, String number, String currency, Double balance) {
        this.Type = type;
        this.Title = title;
        this.Number = number;
        this.Currency = currency;
        this.Balance = balance;
    }
}
