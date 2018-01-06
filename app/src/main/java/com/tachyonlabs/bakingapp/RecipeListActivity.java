package com.tachyonlabs.bakingapp;

import com.tachyonlabs.bakingapp.adapters.RecipeCardAdapter;
import com.tachyonlabs.bakingapp.databinding.ActivityRecipeListBinding;
import com.tachyonlabs.bakingapp.models.Recipe;
import com.tachyonlabs.bakingapp.utilities.NetworkUtils;
import com.tachyonlabs.bakingapp.utilities.RecipeJsonUtils;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;

public class RecipeListActivity extends AppCompatActivity implements RecipeCardAdapter.RecipeCardAdapterOnClickHandler {
    ActivityRecipeListBinding mBinding;
    private RecyclerView mRecyclerView;
    private RecipeCardAdapter mRecipeCardAdapter;
    private TextView tvErrorMessageDisplay;
    private ProgressBar pbLoadingIndicator;
    private Recipe[] recipes;

    private final static String TAG = RecipeListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_list);
        mRecyclerView = mBinding.rvRecipeCards;
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecipeCardAdapter = new RecipeCardAdapter(this);
        mRecyclerView.setAdapter(mRecipeCardAdapter);
        tvErrorMessageDisplay = mBinding.tvErrorMessageDisplay;
        pbLoadingIndicator = mBinding.pbLoadingIndicator;
        pbLoadingIndicator.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );
        if (savedInstanceState == null) {
            loadRecipes();
        } else {
            recipes = (Recipe[]) (savedInstanceState.getParcelableArray("recipes"));
            mRecipeCardAdapter.setRecipeCardData(recipes);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray("recipes", recipes);
        super.onSaveInstanceState(outState);
    }

    private void loadRecipes() {
        new FetchRecipesTask().execute();
    }

    public class FetchRecipesTask extends AsyncTask<String, Void, Recipe[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Recipe[] doInBackground(String... params) {
            try {
                URL recipesJsonUrl = NetworkUtils.getRecipesJsonUrl();
                String jsonRecipesResponse = NetworkUtils.getResponseFromHttpUrl(recipesJsonUrl);
                return RecipeJsonUtils.getRecipesFromJson(RecipeListActivity.this, jsonRecipesResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Recipe[] theRecipes) {
            pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if (theRecipes != null) {
                recipes = theRecipes;
                showRecipeCards();
                mRecipeCardAdapter.setRecipeCardData(recipes);
            } else {
                showErrorMessage(getString(R.string.no_data_received));
            }
        }
    }

    private void showRecipeCards() {
        tvErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(String errorMessage) {
        mRecyclerView.setVisibility(View.INVISIBLE);
        tvErrorMessageDisplay.setText(errorMessage);
        tvErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipe", recipe);
        startActivity(intent);
    }
}
