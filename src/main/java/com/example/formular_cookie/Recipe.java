package com.example.formular_cookie;


import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class Recipe implements Serializable {
    private String id;
    private String title;

    private String imageUrl;
    private String ingredients;
    private String steps;

    public Recipe(String steps, String ingredients, String imageUrl, String title) {
        this.steps = steps;
        this.ingredients = ingredients;
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public Recipe(){

    }
    public Recipe(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter và Setter cho imageUrl
    @PropertyName("image") // Ánh xạ trường "image" từ Firebase vào trường "imageUrl" trong Java
    public String getImageUrl() {
        return imageUrl;
    }

    @PropertyName("image") // Ánh xạ trường "image" từ Firebase vào trường "imageUrl" trong Java
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
