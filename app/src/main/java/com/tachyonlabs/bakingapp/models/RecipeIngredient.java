package com.tachyonlabs.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class RecipeIngredient implements Parcelable {
    public static final Parcelable.Creator<RecipeIngredient> CREATOR
            = new Parcelable.Creator<RecipeIngredient>() {

        @Override
        public RecipeIngredient createFromParcel(Parcel in) {
            return new RecipeIngredient(in);
        }

        @Override
        public RecipeIngredient[] newArray(int size) {
            return new RecipeIngredient[size];
        }
    };
    private String name;
    private int quantity;
    private String measurementUnit;

    private RecipeIngredient(Parcel in) {
        name = in.readString();
        quantity = in.readInt();
        measurementUnit = in.readString();
    }

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

    public String getQuantityUnitNameString() {
        return String.format("%s %s %s", getQuantity(), getMeasurementUnit().toLowerCase(), getName());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(quantity);
        parcel.writeString(measurementUnit);
    }
}
