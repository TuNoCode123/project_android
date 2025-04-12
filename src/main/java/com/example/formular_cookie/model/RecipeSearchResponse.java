package com.example.formular_cookie.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeSearchResponse {
    @SerializedName("results")
    private List<Recipe> recipes;

    @SerializedName("offset")
    private int offset;

    @SerializedName("number")
    private int number;

    @SerializedName("totalResults")
    private int totalResults;

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public int getOffset() {
        return offset;
    }

    public int getNumber() {
        return number;
    }

    public int getTotalResults() {
        return totalResults;
    }
}
