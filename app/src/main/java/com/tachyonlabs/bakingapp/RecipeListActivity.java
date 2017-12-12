package com.tachyonlabs.bakingapp;

import com.tachyonlabs.bakingapp.databinding.ActivityRecipeListBinding;
import com.tachyonlabs.bakingapp.models.RecipeIngredient;
import com.tachyonlabs.bakingapp.models.Recipe;
import com.tachyonlabs.bakingapp.models.RecipeStep;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeListActivity extends AppCompatActivity implements RecipeCardAdapter.RecipeCardAdapterOnClickHandler {
    ActivityRecipeListBinding mBinding;
    private RecyclerView mRecyclerView;
    private RecipeCardAdapter mRecipeCardAdapter;
    private TextView tvErrorMessageDisplay;
    private ProgressBar pbLoadingIndicator;
    private Recipe[] recipes;

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
        recipes = getTestData();
        mRecipeCardAdapter.setRecipeCardData(recipes);
    }

    private Recipe[] getTestData() {
        String[] names = {"Nutella Pie", "Brownies", "Yellow Cake", "Cheesecake"};
        Recipe[] cards = new Recipe[8];
        for (int i = 1; i < 9; i++) {
            Recipe card = new Recipe(i, names[(i - 1) % 4], new RecipeIngredient[1], new RecipeStep[1], i, "");
            cards[i - 1] = card;
        }
        return cards;
    }

    @Override
    public void onClick(Recipe recipe) {
        Toast.makeText(this, recipe.getName(), Toast.LENGTH_LONG).show();
    }
}
