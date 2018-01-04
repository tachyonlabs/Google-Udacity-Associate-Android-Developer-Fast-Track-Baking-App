package com.tachyonlabs.bakingapp.utilities;

import com.tachyonlabs.bakingapp.models.Recipe;
import com.tachyonlabs.bakingapp.models.RecipeIngredient;
import com.tachyonlabs.bakingapp.models.RecipeStep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import java.util.HashMap;

public class RecipeJsonUtils {
    private static final String TAG = RecipeJsonUtils.class.getSimpleName();

    public static Recipe[] getRecipesFromJson(Context context, String recipesJsonString) throws JSONException {
        JSONArray recipesArray = new JSONArray(recipesJsonString);
        Recipe[] recipes = new Recipe[recipesArray.length()];
        HashMap<String, String[]> photoUrls = new HashMap<>();
        /*
        The JSON for the recipes contains URLs for videos illustrating some of the steps, but no
        links to photos, and no recipe descriptions. I would say that in "real life" no one who went
        to all the trouble to make multiple videos for each of their recipes would neglect the very
        basic steps of taking at least one photograph and writing a short blurb/tagline for each of
        the finished products, so to make this app look more like an actual product as well, I wrote
        some blurbs, and am using the following Flickr photos, which the photographers have made
        available for use under the Creative Commons license:

        * For Nutella Pie: "[I invented this](https://www.flickr.com/photos/leedav/4328677446/)", by [Lee Davenport](https://www.flickr.com/people/leedav/)
        * For Brownies: "[Chocolate-Mint Brownies](https://www.flickr.com/photos/theryn/5727350257/)", by [Theryn Fleming](https://www.flickr.com/people/theryn/)
        * For Yellow Cake: "[yellow cake](https://www.flickr.com/photos/stuart_spivack/2584637478/)", by [Stuart Spivack](https://www.flickr.com/people/stuart_spivack/)
        * For Cheesecake: "[Cheesecake Supreme](https://www.flickr.com/photos/cuttingboard/2699220126)", by [Emily Carlin](https://www.flickr.com/people/cuttingboard/)
        */
        photoUrls.put("Nutella Pie", new String[] {"https://c1.staticflickr.com/5/4001/4328677446_ecdd0479a3_z.jpg", "As easy to make as it is delicious and decadent!"});
        photoUrls.put("Brownies",  new String[] {"https://c2.staticflickr.com/6/5224/5727350257_72a6008cc9_z.jpg", "Your family will love these rich, gooey brownies."});
        photoUrls.put("Yellow Cake",  new String[] {"https://c2.staticflickr.com/4/3177/2584637478_bc89ae4a1d_z.jpg", "An updated version of the beloved classic."});
        photoUrls.put("Cheesecake",  new String[] {"https://c2.staticflickr.com/4/3015/2699220126_cc964a2cd2_z.jpg", "My grandmother's prize-winning recipe."});

        for (int id = 0; id < recipesArray.length(); id++) {
            JSONObject recipe = recipesArray.getJSONObject(id);
            String recipeName = recipe.getString("name");
            int recipeServings = recipe.getInt("servings");

            JSONArray ingredients = recipe.getJSONArray("ingredients");
            RecipeIngredient[] recipeIngredients = getIngredientsFromJson(context, ingredients);

            JSONArray steps = recipe.getJSONArray("steps");
            RecipeStep[] recipeSteps = getStepsFromJson(context, steps);

            String photoUrl = photoUrls.get(recipeName)[0];
            String blurb = photoUrls.get(recipeName)[1];

            Recipe card = new Recipe(id,
                    recipeName,
                    recipeIngredients,
                    recipeSteps,
                    recipeServings,
                    photoUrl,
                    blurb);
            recipes[id] = card;
        }
        return recipes;
    }

    private static RecipeIngredient[] getIngredientsFromJson(Context context, JSONArray ingredientsArray) throws JSONException {
        RecipeIngredient[] ingredients = new RecipeIngredient[ingredientsArray.length()];

        for (int i = 0; i < ingredientsArray.length(); i++) {
            JSONObject ingredientJson = ingredientsArray.getJSONObject(i);
            String ingredientName = ingredientJson.getString("ingredient");
            // some of the ingredients don't have a space between a word and a following open paren
            ingredientName = ingredientName.replaceAll("(\\S)\\(", "$1 \\(");
            int ingredientQuantity = ingredientJson.getInt("quantity");
            String ingredientMeasurementUnit = ingredientJson.getString("measure");

            RecipeIngredient ingredient = new RecipeIngredient(ingredientName,
                    ingredientQuantity,
                    ingredientMeasurementUnit);
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
            // There's one step where the video URL is in the wrong JSON field
            if (stepVideoUrl.equals("") && stepThumbnailUrl.endsWith(".mp4")) {
                stepVideoUrl = stepThumbnailUrl;
                stepThumbnailUrl = "";
            }

            RecipeStep step = new RecipeStep(stepId,
                    stepShortDescription,
                    stepDescription,
                    stepVideoUrl,
                    stepThumbnailUrl);
            steps[i] = step;
        }
        return steps;
    }

}
