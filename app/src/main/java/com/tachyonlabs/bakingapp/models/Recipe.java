package com.tachyonlabs.bakingapp.models;

public class Recipe {
    private int id;
    private String name;
    private RecipeIngredient[] ingredients;
    private RecipeStep[] steps;
    private int servings;
    private String image;

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

    public RecipeStep[] getSteps() {
        return steps;
    }

    public void setSteps(RecipeStep[] recipeSteps) {
        this.steps = recipeSteps;
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
}
