package com.example.formular_cookie;

import android.net.Uri;

import java.io.Serializable;

public class Recipe implements Serializable {
    private String title;
    private String ingredients;
    private String steps;
    private Uri selectedImageUri;
    public Recipe(String title, String ingredients, String steps, Uri selectedImageUri) {
        this.title = title;
        this.ingredients = ingredients;
        this.steps = steps;
        this.selectedImageUri = selectedImageUri;
    }

    public String getTitle() {
        return title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public Uri getSelectedImageUri() {
        return selectedImageUri;
    }

    public void setTitle(String title) {
        this.title=title;
    }

    public void setIngredients(String ingredients) {
        this.ingredients=ingredients;
    }

    public void setSteps(String steps) {
        this.steps=steps;
    }

    public void setSelectedImageUri(Uri selectedImageUri) {
        this.selectedImageUri=selectedImageUri;
    }
}
