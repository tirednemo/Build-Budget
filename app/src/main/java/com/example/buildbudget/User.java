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
    }

    public void setPhoto(String photouri) {
        this.photo = photouri;
    }
}
