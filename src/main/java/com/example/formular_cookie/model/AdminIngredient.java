package com.example.formular_cookie.model;

public class AdminIngredient {
    private String name;
    private String amount;
    private String unit;

    public AdminIngredient() {
    }

    public AdminIngredient(String name, String amount, String unit) {
        this.name = name;
        this.unit = unit;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public String getAmount() {
        return amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}