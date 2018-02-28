package com.tachyonlabs.bakingapp.activities;

import com.tachyonlabs.bakingapp.R;
import com.tachyonlabs.bakingapp.fragments.RecipeDetailFragment;
import com.tachyonlabs.bakingapp.fragments.RecipeStepFragment;
import com.tachyonlabs.bakingapp.models.Recipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.widget.TextView;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnStepClickListener {
    private static final String TAG = RecipeDetailActivity.class.getSimpleName();
    private Recipe recipe;
    private boolean mTwoPane;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Intent callingIntent = getIntent();
        recipe = callingIntent.getParcelableExtra(getString(R.string.recipe_key));

        // if we're on a tablet, we're doing a two-pane layout
        if (findViewById(R.id.recipe_detail_fragment_for_tablet) != null) {
            mTwoPane = true;
            TextView recipeDetailName = findViewById(R.id.tv_recipe_detail_name);
            recipeDetailName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        } else {
            mTwoPane = false;
        }
    }

    public void onStepSelected(int whichStep) {
        if (mTwoPane) {
            RecipeStepFragment recipeStepFragment = (RecipeStepFragment) getSupportFragmentManager().findFragmentById(R.id.recipe_step_fragment_for_tablet);
            recipeStepFragment.updateStepNumberDescriptionAndVideo(whichStep);
        } else {
            Intent intent = new Intent(this, RecipeStepActivity.class);
            intent.putExtra(getString(R.string.recipe_key), recipe);
            intent.putExtra(getString(R.string.which_step_key), whichStep);
            startActivity(intent);
        }
    }
}
