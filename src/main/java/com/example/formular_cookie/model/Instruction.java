package com.example.formular_cookie.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Instruction implements Parcelable {
    private String name;
    private List<Step> steps;

    // Default constructor
    public Instruction() {
        steps = new ArrayList<>();
    }

    // Constructor with parameters
    public Instruction(String name, List<Step> steps) {
        this.name = name;
        this.steps = steps != null ? steps : new ArrayList<>();
    }

    // Getters
    public String getName() {
        return name;
    }

    public List<Step> getSteps() {
        return steps;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    // Convert from Firestore Map to Instruction object
    public static Instruction fromMap(Map<String, Object> map) {
        Instruction instruction = new Instruction();

        if (map.containsKey("name")) instruction.name = (String) map.get("name");

        if (map.containsKey("steps") && map.get("steps") instanceof List) {
            List<Map<String, Object>> stepsList = (List<Map<String, Object>>) map.get("steps");
            for (Map<String, Object> stepMap : stepsList) {
                Step step = Step.fromMap(stepMap);
                instruction.steps.add(step);
            }
        }

        return instruction;
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
        private int number;
        private String step;

        // Default constructor
        public Step() {
        }

        // Constructor with parameters
        public Step(int number, String step) {
            this.number = number;
            this.step = step;
        }

        // Getters
        public int getNumber() {
            return number;
        }

        public String getStep() {
            return step;
        }

        // Setters
        public void setNumber(int number) {
            this.number = number;
        }

        public void setStep(String step) {
            this.step = step;
        }

        // Convert from Firestore Map to Step object
        public static Step fromMap(Map<String, Object> map) {
            Step step = new Step();

            if (map.containsKey("number")) {
                Object num = map.get("number");
                if (num instanceof Long) {
                    step.number = ((Long) num).intValue();
                } else if (num instanceof Integer) {
                    step.number = (Integer) num;
                }
            }
            if (map.containsKey("step")) step.step = (String) map.get("step");

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
