package com.example.formular_cookie.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeDetailResponse extends Recipe {
    // This class extends Recipe and adds any additional fields
    // that might be present in the detailed response

    @SerializedName("vegetarian")
    private boolean vegetarian;

    @SerializedName("vegan")
    private boolean vegan;

    @SerializedName("glutenFree")
    private boolean glutenFree;

    @SerializedName("dairyFree")
    private boolean dairyFree;

    @SerializedName("veryHealthy")
    private boolean veryHealthy;

    @SerializedName("cheap")
    private boolean cheap;

    @SerializedName("veryPopular")
    private boolean veryPopular;

    @SerializedName("sustainable")
    private boolean sustainable;

    @SerializedName("weightWatcherSmartPoints")
    private int weightWatcherSmartPoints;

    @SerializedName("dishTypes")
    private List<String> dishTypes;

    @SerializedName("diets")
    private List<String> diets;

    // Getters
    public boolean isVegetarian() {
        return vegetarian;
    }

    public boolean isVegan() {
        return vegan;
    }

    public boolean isGlutenFree() {
        return glutenFree;
    }

    public boolean isDairyFree() {
        return dairyFree;
    }

    public boolean isVeryHealthy() {
        return veryHealthy;
    }

    public boolean isCheap() {
        return cheap;
    }

    public boolean isVeryPopular() {
        return veryPopular;
    }

    public boolean isSustainable() {
        return sustainable;
    }

    public int getWeightWatcherSmartPoints() {
        return weightWatcherSmartPoints;
    }

    public List<String> getDishTypes() {
        return dishTypes;
    }

    public List<String> getDiets() {
        return diets;
    }
}

