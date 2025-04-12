package com.example.formular_cookie.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Instruction implements Parcelable {
    @SerializedName("name")
    private String name;

    @SerializedName("steps")
    private List<Step> steps;

    // Default constructor
    public Instruction() {
        steps = new ArrayList<>();
    }

    // Getters
    public String getName() {
        return name;
    }

    public List<Step> getSteps() {
        return steps;
    }

    // Parcelable implementation
    protected Instruction(Parcel in) {
        name = in.readString();
        steps = new ArrayList<>();
        in.readList(steps, Step.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeList(steps);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Instruction> CREATOR = new Creator<Instruction>() {
        @Override
        public Instruction createFromParcel(Parcel in) {
            return new Instruction(in);
        }

        @Override
        public Instruction[] newArray(int size) {
            return new Instruction[size];
        }
    };

    public static class Step implements Parcelable {
        @SerializedName("number")
        private int number;

        @SerializedName("step")
        private String step;

        // Default constructor
        public Step() {
        }

        // Getters
        public int getNumber() {
            return number;
        }

        public String getStep() {
            return step;
        }

        // Parcelable implementation
        protected Step(Parcel in) {
            number = in.readInt();
            step = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(number);
            dest.writeString(step);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Step> CREATOR = new Creator<Step>() {
            @Override
            public Step createFromParcel(Parcel in) {
                return new Step(in);
            }

            @Override
            public Step[] newArray(int size) {
                return new Step[size];
            }
        };
    }
}

