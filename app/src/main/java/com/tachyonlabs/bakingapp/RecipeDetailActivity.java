package com.tachyonlabs.bakingapp;

import com.squareup.picasso.Picasso;
import com.tachyonlabs.bakingapp.adapters.RecipeStepAdapter;
import com.tachyonlabs.bakingapp.databinding.ActivityRecipeDetailBinding;
import com.tachyonlabs.bakingapp.models.Recipe;
import com.tachyonlabs.bakingapp.models.RecipeIngredient;
import com.tachyonlabs.bakingapp.models.RecipeStep;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeDetailActivity extends AppCompatActivity  implements RecipeStepAdapter.RecipeStepAdapterOnClickHandler {
    ActivityRecipeDetailBinding mBinding;
    private RecyclerView mRecipeStepsRecyclerView;
    private RecipeStepAdapter mRecipeStepAdapter;

    private static final String TAG = RecipeDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_detail);

        ImageView ivRecipeDetailPhoto = mBinding.ivRecipeDetailPhoto;
        TextView tvRecipeName = mBinding.tvRecipeDetailName;
        TextView tvRecipeServings = mBinding.tvRecipeServings;
        TextView tvIngredientsList = mBinding.tvIngredientsList;
        ScrollView svRecipeDetail = mBinding.svRecipeDetail;

        // set up recyclerview and adapter to display the steps
        mRecipeStepsRecyclerView = mBinding.rvRecipeSteps;
        LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecipeStepsRecyclerView.setLayoutManager(trailersLayoutManager);
        mRecipeStepAdapter = new com.tachyonlabs.bakingapp.adapters.RecipeStepAdapter(this);
        mRecipeStepsRecyclerView.setAdapter(mRecipeStepAdapter);

        Intent callingIntent = getIntent();
        if (callingIntent.hasExtra("recipe")) {
            final Recipe recipe = callingIntent.getParcelableExtra("recipe");
            Picasso.with(this)
                    .load(recipe.getImage())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivRecipeDetailPhoto);
            tvRecipeName.setText(recipe.getName());
            RecipeIngredient[] recipeIngredients = recipe.getIngredients();
            String[] ingredients = new String[recipeIngredients.length];
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = recipeIngredients[i].getQuantityUnitNameString();
            }
            String servingsDesc = String.format("%d serving%s", recipe.getServings(), recipe.getServings() != 1 ? "s" : "");
            tvRecipeServings.setText(servingsDesc);
            tvIngredientsList.setText(TextUtils.join("\n", ingredients));
            ActionBar ab = getSupportActionBar();
            ab.setTitle(recipe.getName());

            mRecipeStepAdapter.setRecipeStepData(recipe.getSteps());
            svRecipeDetail.smoothScrollTo(0, 0);        }
    }
    @Override
    public void onClick(RecipeStep recipeStep) {
        Toast.makeText(this, recipeStep.getShortDescription(), Toast.LENGTH_LONG).show();
    }

}
