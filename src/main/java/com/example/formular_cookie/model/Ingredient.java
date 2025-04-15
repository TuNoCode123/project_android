package com.example.formular_cookie.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class Ingredient implements Parcelable {
    private String name;
    private String amount;
    private String unit;

    // Default constructor
    public Ingredient() {
    }

    // Constructor with parameters
    public Ingredient(String name, String amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public String getUnit() {
        return unit;
    }


    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    // Convert from Firestore Map to Ingredient object
    public static Ingredient fromMap(Map<String, Object> map) {
        Ingredient ingredient = new Ingredient();

        if (map.containsKey("name")) ingredient.name = (String) map.get("name");
        if (map.containsKey("amount")) ingredient.amount = String.valueOf(map.get("amount"));
        if (map.containsKey("unit")) ingredient.unit = (String) map.get("unit");

        return ingredient;
    }

    // Parcelable implementation
    protected Ingredient(Parcel in) {
        name = in.readString();
        amount = in.readString();
        unit = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(amount);
        dest.writeString(unit);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}

