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
        this.photo = "https://firebasestorage.googleapis.com/v0/b/build-budget-71a7f.appspot.com/o/test%20account.png?alt=media&token=b9cd2260-4aa7-462c-a058-cf30e644f8fb";
    }

    public void setPhoto(String photouri) {
        this.photo = photouri;
    }
}
