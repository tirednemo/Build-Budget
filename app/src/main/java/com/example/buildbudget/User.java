package com.example.buildbudget;

public class User {

    public String name;
    public String email;
    public String photo;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.photo = "https://i.scdn.co/image/ab6761610000e5eb666a1746919918e63fafb413";
    }
}
