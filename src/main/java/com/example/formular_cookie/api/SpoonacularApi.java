package com.example.formular_cookie.api;

import com.example.formular_cookie.model.RecipeDetailResponse;
import com.example.formular_cookie.model.RecipeSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpoonacularApi {

        @GET("recipes/complexSearch")
        Call<RecipeSearchResponse> searchRecipes(
                        @Query("apiKey") String apiKey,
                        @Query("query") String query,
                        @Query("number") int number,
                        @Query("addRecipeInformation") boolean addRecipeInformation,
                        @Query("addRecipeInstructions") boolean addRecipeInstructions,
                        @Query("fillIngredients") boolean fillIngredients,
                        @Query("offset") int offset);

        @GET("recipes/{id}/information")
        Call<RecipeDetailResponse> getRecipeDetails(
                        @Path("id") int recipeId,
                        @Query("apiKey") String apiKey,
                        @Query("includeNutrition") boolean includeNutrition);
}
