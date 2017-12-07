package com.tachyonlabs.bakingapp.models;

public class RecipeIngredient {
    private String name;
    private int quantity;
    private String measurementUnit;

    public RecipeIngredient(String ingredient, int quantity, String measure) {
        this.name = ingredient;
        this.quantity = quantity;
        this.measurementUnit = measure;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }
}
