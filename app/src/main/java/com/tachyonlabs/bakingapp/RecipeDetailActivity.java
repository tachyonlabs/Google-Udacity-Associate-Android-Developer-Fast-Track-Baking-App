package com.tachyonlabs.bakingapp;

import com.squareup.picasso.Picasso;
import com.tachyonlabs.bakingapp.databinding.ActivityRecipeDetailBinding;
import com.tachyonlabs.bakingapp.models.Recipe;
import com.tachyonlabs.bakingapp.models.RecipeIngredient;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class RecipeDetailActivity extends AppCompatActivity {
    ActivityRecipeDetailBinding mBinding;

    private static final String TAG = RecipeDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_detail);

        ImageView ivRecipeDetailPhoto = mBinding.ivRecipeDetailPhoto;
        TextView tvRecipeName = mBinding.tvRecipeDetailName;
        TextView tvIngredientsList = mBinding.tvIngredientsList;

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
            tvIngredientsList.setText(TextUtils.join("\n", ingredients));

        }
    }
}
