package com.tachyonlabs.bakingapp.utilities;

import com.tachyonlabs.bakingapp.models.Recipe;
import com.tachyonlabs.bakingapp.models.RecipeIngredient;
import com.tachyonlabs.bakingapp.models.RecipeStep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class RecipeJsonUtils {
    private static final String TAG = RecipeJsonUtils.class.getSimpleName();

    public static Recipe[] getRecipesFromJson(Context context, String recipesJsonString) throws JSONException {
        JSONArray recipesArray = new JSONArray(recipesJsonString);
        Recipe[] recipes = new Recipe[recipesArray.length()];

        for (int i = 0; i < recipesArray.length(); i++) {
            JSONObject recipe = recipesArray.getJSONObject(i);
            String recipeName = recipe.getString("name");
            int recipeServings = recipe.getInt("servings");

            Recipe card = new Recipe(i, recipeName, new RecipeIngredient[1], new RecipeStep[1], recipeServings, "");
            recipes[i] = card;
        }
//            Recipe recipe = new Recipe();
//            JSONObject result = resultsArray.getJSONObject(i);
//            movie.setTitle(result.getString("title"));
//            movie.setOverview(result.getString("overview"));
//            movie.setPosterUrl(result.getString("poster_path"));
//            movie.setReleaseDate(result.getString("release_date"));
//            movie.setUserRating(String.valueOf(result.getDouble("vote_average")));
//            movie.setId(String.valueOf((int) result.getDouble("id")));
//            recipes[i] = recipe;
        return recipes;
    }

}
