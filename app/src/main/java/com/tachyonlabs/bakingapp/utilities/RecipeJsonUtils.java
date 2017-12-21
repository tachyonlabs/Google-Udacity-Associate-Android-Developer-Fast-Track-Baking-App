package com.tachyonlabs.bakingapp.utilities;

import com.tachyonlabs.bakingapp.models.Recipe;
import com.tachyonlabs.bakingapp.models.RecipeIngredient;
import com.tachyonlabs.bakingapp.models.RecipeStep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class RecipeJsonUtils {
    private static final String TAG = RecipeJsonUtils.class.getSimpleName();

    public static Recipe[] getRecipesFromJson(Context context, String recipesJsonString) throws JSONException {
        JSONArray recipesArray = new JSONArray(recipesJsonString);
        Recipe[] recipes = new Recipe[recipesArray.length()];

        for (int i = 0; i < recipesArray.length(); i++) {
            JSONObject recipe = recipesArray.getJSONObject(i);
            String recipeName = recipe.getString("name");
            int recipeServings = recipe.getInt("servings");

            JSONArray ingredients = recipe.getJSONArray("ingredients");
            RecipeIngredient[] recipeIngredients = getIngredientsFromJson(context, ingredients);

            JSONArray steps = recipe.getJSONArray("steps");
            RecipeStep[] recipeSteps = getStepsFromJson(context, steps);
            Log.d(TAG, "recipeSteps length: " + recipeSteps.length);

            Recipe card = new Recipe(i, recipeName, recipeIngredients, recipeSteps, recipeServings, "");
            recipes[i] = card;
        }
        return recipes;
    }

    private static RecipeIngredient[] getIngredientsFromJson(Context context, JSONArray ingredientsArray) throws JSONException {
        RecipeIngredient[] ingredients = new RecipeIngredient[ingredientsArray.length()];

        for (int i = 0; i < ingredientsArray.length(); i++) {
            JSONObject ingredientJson = ingredientsArray.getJSONObject(i);
            String ingredientName = ingredientJson.getString("ingredient");
            int ingredientQuantity = ingredientJson.getInt("quantity");
            String ingredientMeasurementUnit = ingredientJson.getString("measure");

            RecipeIngredient ingredient = new RecipeIngredient(ingredientName, ingredientQuantity, ingredientMeasurementUnit);
            ingredients[i] = ingredient;
        }
        return ingredients;
    }

    private static RecipeStep[] getStepsFromJson(Context context, JSONArray stepsArray) throws JSONException {
        RecipeStep[] steps = new RecipeStep[stepsArray.length()];

        for (int i = 0; i < stepsArray.length(); i++) {
            JSONObject stepJson = stepsArray.getJSONObject(i);
            int stepId = stepJson.getInt("id");
            String stepShortDescription = stepJson.getString("shortDescription");
            String stepDescription = stepJson.getString("description");
            String stepVideoUrl = stepJson.getString("videoURL");
            String stepThumbnailUrl = stepJson.getString("thumbnailURL");

            RecipeStep step = new RecipeStep(stepId, stepShortDescription, stepDescription, stepVideoUrl, stepThumbnailUrl);
            steps[i] = step;
        }
        return steps;
    }

}
