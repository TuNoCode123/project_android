package com.example.formular_cookie;

public class User {
    private String name;
    private String email;

    public User() {
        // Required empty constructor for Firestore
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
