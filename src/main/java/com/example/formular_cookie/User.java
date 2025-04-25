package com.example.formular_cookie;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String id;
    private String name;
    private String email;
    private List<String> likedRecipies;


    public User() {
        // Required empty constructor for Firestore
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String name, String email, List<String> likedRecipies) {
        this.name = name;
        this.email = email;
        this.likedRecipies = likedRecipies;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getLikedRecipies() {
        return likedRecipies;
    }

    public void setLikedRecipes(List<String> likedRecipes) {
        this.likedRecipies = likedRecipes;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}