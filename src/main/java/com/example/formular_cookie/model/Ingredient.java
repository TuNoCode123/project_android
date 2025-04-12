package com.example.formular_cookie.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Ingredient implements Parcelable {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("amount")
    private double amount;

    @SerializedName("unit")
    private String unit;

    @SerializedName("image")
    private String image;

    @SerializedName("original")
    private String original;

    // Default constructor
    public Ingredient() {
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public String getUnit() {
        return unit;
    }

    public String getImage() {
        return image;
    }

    public String getOriginal() {
        return original;
    }

    // Parcelable implementation
    protected Ingredient(Parcel in) {
        id = in.readInt();
        name = in.readString();
        amount = in.readDouble();
        unit = in.readString();
        image = in.readString();
        original = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(amount);
        dest.writeString(unit);
        dest.writeString(image);
        dest.writeString(original);
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
