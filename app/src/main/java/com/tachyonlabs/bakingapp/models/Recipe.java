package com.tachyonlabs.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Recipe implements Parcelable {
    public static final Parcelable.Creator<Recipe> CREATOR
            = new Parcelable.Creator<Recipe>() {

        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
    private int id;
    private String name;
    private RecipeIngredient[] ingredients;
    private RecipeStep[] steps;
    private int servings;
    private String image;

    private Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        ingredients = in.createTypedArray(RecipeIngredient.CREATOR);
        steps = in.createTypedArray(RecipeStep.CREATOR);
        servings = in.readInt();
        image = in.readString();
    }

    public Recipe(int id, String name, RecipeIngredient[] recipeIngredients, RecipeStep[] recipeSteps, int servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = recipeIngredients;
        this.steps = recipeSteps;
        this.servings = servings;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RecipeIngredient[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(RecipeIngredient[] recipeIngredients) {
        this.ingredients = recipeIngredients;
    }

    public int getIngredientsCount() {
        return ingredients.length;
    }

    public RecipeStep[] getSteps() {
        return steps;
    }

    public void setSteps(RecipeStep[] recipeSteps) {
        this.steps = recipeSteps;
    }

    public int getStepsCount() {
        return steps.length;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeTypedArray(ingredients, flags);
        parcel.writeTypedArray(steps, flags);
        parcel.writeInt(servings);
        parcel.writeString(image);
    }
}
