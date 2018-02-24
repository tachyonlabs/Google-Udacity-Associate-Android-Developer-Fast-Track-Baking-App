package com.tachyonlabs.bakingapp.activities;

import com.tachyonlabs.bakingapp.R;
import com.tachyonlabs.bakingapp.fragments.RecipeListFragment;
import com.tachyonlabs.bakingapp.models.Recipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class RecipeListActivity extends AppCompatActivity implements RecipeListFragment.OnRecipeClickListener {

    private final static String TAG = RecipeListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
    }

    public void onRecipeSelected(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(getString(R.string.recipe_key), recipe);
        startActivity(intent);
    }

}
