package com.tachyonlabs.bakingapp;

import com.tachyonlabs.bakingapp.databinding.ActivityRecipeListBinding;
import com.tachyonlabs.bakingapp.models.RecipeCard;

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
    private RecipeCard[] recipeCards;

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
        recipeCards = getTestData();
        mRecipeCardAdapter.setRecipeCardData(recipeCards);
    }

    private RecipeCard[] getTestData() {
        RecipeCard[] cards = new RecipeCard[10];
        for (int i = 1; i < 11; i++) {
            RecipeCard card = new RecipeCard();
            card.setTitle("Testing " + i);
            cards[i - 1] = card;
        }
        return cards;
    }

    @Override
    public void onClick(RecipeCard recipeCard) {
        Toast.makeText(this, recipeCard.getTitle(), Toast.LENGTH_LONG).show();
    }
}
