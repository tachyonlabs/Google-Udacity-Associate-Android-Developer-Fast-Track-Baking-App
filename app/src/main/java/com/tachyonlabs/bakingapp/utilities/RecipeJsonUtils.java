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
    private static final String JSON_RECIPE_NAME_KEY = "name";
    private static final String JSON_RECIPE_SERVINGS_KEY = "servings";
    private static final String JSON_RECIPE_IMAGE_KEY = "image";
    private static final String JSON_RECIPE_INGREDIENTS_KEY = "ingredients";
    private static final String JSON_RECIPE_INGREDIENT_KEY = "ingredient";
    private static final String JSON_RECIPE_STEPS_KEY = "steps";
    private static final String JSON_RECIPE_QUANTITY_KEY = "quantity";
    private static final String JSON_RECIPE_MEASURE_KEY = "measure";
    private static final String JSON_RECIPE_ID_KEY = "id";
    private static final String JSON_RECIPE_SHORT_DESCRIPTION_KEY = "shortDescription";
    private static final String JSON_RECIPE_DESCRIPTION_KEY = "description";
    private static final String JSON_RECIPE_VIDEO_URL_KEY = "videoURL";
    private static final String JSON_RECIPE_THUMBNAIL_URL_KEY = "thumbnailURL";
    private static final String MP4_FILE_TYPE = ".mp4";
    private static final String LARGE_PHOTO_TYPE = "z.jpg";
    private static final String THUMBNAIL_PHOTO_TYPE = "s.jpg";

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
        photoUrls.put("Nutella Pie", new String[]{"https://c1.staticflickr.com/5/4001/4328677446_ecdd0479a3_", "As easy to make as it is delicious and decadent."});
        photoUrls.put("Brownies", new String[]{"https://c2.staticflickr.com/6/5224/5727350257_72a6008cc9_", "Your family will love these rich, fudgy brownies."});
        photoUrls.put("Yellow Cake", new String[]{"https://c2.staticflickr.com/4/3177/2584637478_bc89ae4a1d_", "This beloved classic is my favorite dessert."});
        photoUrls.put("Cheesecake", new String[]{"https://c2.staticflickr.com/4/3015/2699220126_cc964a2cd2_", "Elegant, easy to make, and everyone loves it."});

        for (int id = 0; id < recipesArray.length(); id++) {
            JSONObject recipe = recipesArray.getJSONObject(id);
            String recipeName = recipe.getString(JSON_RECIPE_NAME_KEY);
            int recipeServings = recipe.getInt(JSON_RECIPE_SERVINGS_KEY);
            String recipeImageUrl = recipe.getString(JSON_RECIPE_IMAGE_KEY);

            JSONArray ingredients = recipe.getJSONArray(JSON_RECIPE_INGREDIENTS_KEY);
            RecipeIngredient[] recipeIngredients = getIngredientsFromJson(context, ingredients);

            JSONArray steps = recipe.getJSONArray(JSON_RECIPE_STEPS_KEY);
            RecipeStep[] recipeSteps = getStepsFromJson(context, steps);

            if (recipeImageUrl.equals("")) {
                recipeImageUrl = photoUrls.get(recipeName)[0] + LARGE_PHOTO_TYPE;
            }
            String recipethumbnailUrl = photoUrls.get(recipeName)[0] + THUMBNAIL_PHOTO_TYPE;
            String blurb = photoUrls.get(recipeName)[1];

            Recipe card = new Recipe(id,
                    recipeName,
                    recipeIngredients,
                    recipeSteps,
                    recipeServings,
                    recipeImageUrl,
                    recipethumbnailUrl,
                    blurb);
            recipes[id] = card;
        }

        return recipes;
    }

    private static RecipeIngredient[] getIngredientsFromJson(Context context, JSONArray ingredientsArray) throws JSONException {
        RecipeIngredient[] ingredients = new RecipeIngredient[ingredientsArray.length()];

        for (int i = 0; i < ingredientsArray.length(); i++) {
            JSONObject ingredientJson = ingredientsArray.getJSONObject(i);
            String ingredientName = ingredientJson.getString(JSON_RECIPE_INGREDIENT_KEY);
            // some of the ingredients don't have a space between a word and a following open paren
            ingredientName = ingredientName.replaceAll("(\\S)\\(", "$1 \\(");
            int ingredientQuantity = ingredientJson.getInt(JSON_RECIPE_QUANTITY_KEY);
            String ingredientMeasurementUnit = ingredientJson.getString(JSON_RECIPE_MEASURE_KEY);

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
            int stepId = stepJson.getInt(JSON_RECIPE_ID_KEY);
            String stepShortDescription = stepJson.getString(JSON_RECIPE_SHORT_DESCRIPTION_KEY);
            String stepDescription = stepJson.getString(JSON_RECIPE_DESCRIPTION_KEY);
            String stepVideoUrl = stepJson.getString(JSON_RECIPE_VIDEO_URL_KEY);
            String stepThumbnailUrl = stepJson.getString(JSON_RECIPE_THUMBNAIL_URL_KEY);

            // There's one step where the video URL is in the wrong JSON field, and the commented-
            // out lines below are how I originally handled this. However, when I submitted this
            // project, my reviewer said that I was required to change this, specifically
            // "thumbnailUrl should be handled only as an image. Though some thumbnailUrl contain
            // .mp4 link, it is an intentional error to induce error handling thinking."
            // Personally, I think what I did below was error handling, but I'm required to change
            // it then I'm required to change it. :-)
//            if (stepVideoUrl.equals("") && stepThumbnailUrl.endsWith(MP4_FILE_TYPE)) {
//                stepVideoUrl = stepThumbnailUrl;
//                stepThumbnailUrl = "";
//            }

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
