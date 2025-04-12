package com.example.formular_cookie.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Recipe implements Parcelable {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("image")
    private String image;

    @SerializedName("imageType")
    private String imageType;

    @SerializedName("readyInMinutes")
    private int readyInMinutes;

    @SerializedName("servings")
    private int servings;

    @SerializedName("summary")
    private String summary;

    @SerializedName("sourceUrl")
    private String sourceUrl;

    @SerializedName("sourceName")
    private String sourceName;

    @SerializedName("likes")
    private int likes;

    @SerializedName("healthScore")
    private double healthScore;

    @SerializedName("extendedIngredients")
    private List<Ingredient> ingredients;

    @SerializedName("analyzedInstructions")
    private List<Instruction> instructions;

    // Add category field for local data
    private String category;

    // Add authorImageUrl field for local data
    private String authorImageUrl;

    // Add followers field for local data
    private int followers;

    // Default constructor
    public Recipe() {
        ingredients = new ArrayList<>();
        instructions = new ArrayList<>();
    }

    // Constructor for local sample data
    public Recipe(String id, String title, String imageUrl, String authorName,
                  String authorImageUrl, int followers, int likes, String readyTime, String category) {
        this.id = Integer.parseInt(id);
        this.title = title;
        this.image = imageUrl;
        this.sourceName = authorName;
        this.authorImageUrl = authorImageUrl;
        this.followers = followers;
        this.likes = likes;
        // Parse readyTime to minutes (assuming format like "2h" or "30m")
        this.readyInMinutes = parseReadyTime(readyTime);
        this.category = category;
        this.ingredients = new ArrayList<>();
        this.instructions = new ArrayList<>();
    }

    // Helper method to parse ready time string to minutes
    private int parseReadyTime(String readyTime) {
        if (readyTime == null || readyTime.isEmpty()) {
            return 0;
        }

        try {
            if (readyTime.endsWith("h")) {
                // Convert hours to minutes
                return Integer.parseInt(readyTime.substring(0, readyTime.length() - 1)) * 60;
            } else if (readyTime.endsWith("m")) {
                // Already in minutes
                return Integer.parseInt(readyTime.substring(0, readyTime.length() - 1));
            } else {
                // Try to parse as minutes
                return Integer.parseInt(readyTime);
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getFullImageUrl() {
        if (image != null && !image.startsWith("http")) {
            return "https://spoonacular.com/recipeImages/" + image;
        }
        return image;
    }

    public int getReadyInMinutes() {
        return readyInMinutes;
    }

    public int getServings() {
        return servings;
    }

    public String getSummary() {
        return summary;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getSourceName() {
        return sourceName;
    }

    public int getLikes() {
        return likes;
    }

    public double getHealthScore() {
        return healthScore;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    // Add getter for category
    public String getCategory() {
        return category;
    }

    // Add getter for authorImageUrl
    public String getAuthorImageUrl() {
        return authorImageUrl;
    }

    // Add getter for followers
    public int getFollowers() {
        return followers;
    }

    // Parcelable implementation
    protected Recipe(Parcel in) {
        id = in.readInt();
        title = in.readString();
        image = in.readString();
        imageType = in.readString();
        readyInMinutes = in.readInt();
        servings = in.readInt();
        summary = in.readString();
        sourceUrl = in.readString();
        sourceName = in.readString();
        likes = in.readInt();
        healthScore = in.readDouble();
        category = in.readString();
        authorImageUrl = in.readString();
        followers = in.readInt();
        ingredients = new ArrayList<>();
        in.readList(ingredients, Ingredient.class.getClassLoader());
        instructions = new ArrayList<>();
        in.readList(instructions, Instruction.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(imageType);
        dest.writeInt(readyInMinutes);
        dest.writeInt(servings);
        dest.writeString(summary);
        dest.writeString(sourceUrl);
        dest.writeString(sourceName);
        dest.writeInt(likes);
        dest.writeDouble(healthScore);
        dest.writeString(category);
        dest.writeString(authorImageUrl);
        dest.writeInt(followers);
        dest.writeList(ingredients);
        dest.writeList(instructions);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
