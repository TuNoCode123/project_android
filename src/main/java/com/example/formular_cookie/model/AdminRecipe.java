package com.example.formular_cookie.model;

import java.io.Serializable;
import java.util.List;

public class AdminRecipe implements Serializable {
    private String id;
    private String title;
    private List<String> steps;
    private Boolean status;
    private String imageUrl;
    private String authorID;
    private String description;

    private List<AdminIngredient> ingredients;

    public AdminRecipe() {
    }

    public AdminRecipe(String title, List<String> steps, Boolean status, String imageUrl, List<AdminIngredient> ingredients, String description) {
        this.title = title;
        this.steps = steps;
        this.status = status;
        this.imageUrl = imageUrl;
        this.ingredients = ingredients;
        this.description = description;
    }

    public AdminRecipe(String title, List<String> steps, Boolean status, String imageUrl) {
        this.title = title;
        this.steps = steps;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AdminIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<AdminIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getSteps() {
        return steps;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}