package com.example.buildbudget;

public class Transaction {
    public String TxID;
    public Double Amount;
    public String Payee;
    public String Date;
    public String Category;
    public Integer Icon;
    public String Account;
    public String Note;
    public String Status;
    public String Invoice;

    public Transaction() {
    }

    public Transaction(String ID, String date, String category, String payee, String note, String status, String invoice, Double amount) {
        this.TxID = ID;
        this.Date = date;
        this.Category = category;
        this.Payee = payee;
        this.Note = note;
        this.Status = status;
        this.Invoice = invoice;
        this.Amount = amount;
    }
}
