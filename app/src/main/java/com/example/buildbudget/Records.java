package com.example.buildbudget;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Records {
    String category;
    String type;
    Double amount;
    String title;
    String payee;
    String account;
    String date;
    String note;
    String invoice;

    Records(String type, String account, Double amount)
    {
        this.type = type;
        this.account = account;
        this.amount = amount;
        this.date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now());
    }
    Records(String type, String account, String title, Double amount)
    {
        this.type = type;
        this.account = account;
        this.title = title;
        this.amount = amount;
        this.date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now());
    }

    void setTitle(String title){
        this.title = title;
    }
    void setCategory(String category){
        this.category = category;
    }
    void setPayee(String payee){
        this.payee = payee;
    }

}