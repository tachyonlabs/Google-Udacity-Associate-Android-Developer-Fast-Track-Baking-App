package com.tachyonlabs.bakingapp;

import com.squareup.picasso.Picasso;
import com.tachyonlabs.bakingapp.adapters.RecipeStepAdapter;
import com.tachyonlabs.bakingapp.models.Recipe;
import com.tachyonlabs.bakingapp.models.RecipeIngredient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;


public class RecipeDetailFragment extends Fragment implements RecipeStepAdapter.RecipeStepAdapterOnClickHandler {
    private static final String TAG = RecipeDetailActivity.class.getSimpleName();
    private RecyclerView mRecipeStepsRecyclerView;
    private RecipeStepAdapter mRecipeStepAdapter;
    private Recipe recipe;

    public RecipeDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ImageView ivRecipeDetailPhoto = (ImageView) rootView.findViewById(R.id.iv_recipe_detail_photo);
        TextView tvRecipeName = (TextView) rootView.findViewById(R.id.tv_recipe_detail_name);
        TextView tvRecipeServings = (TextView) rootView.findViewById(R.id.tv_recipe_servings);
        TextView tvIngredientsList = (TextView) rootView.findViewById(R.id.tv_ingredients_list);
        ScrollView svRecipeDetail = (ScrollView) rootView.findViewById(R.id.sv_recipe_detail);

        // set up recyclerview and adapter to display the steps
        mRecipeStepsRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_recipe_steps);
        LinearLayoutManager stepsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecipeStepsRecyclerView.setLayoutManager(stepsLayoutManager);
        mRecipeStepAdapter = new com.tachyonlabs.bakingapp.adapters.RecipeStepAdapter(this);
        mRecipeStepsRecyclerView.setAdapter(mRecipeStepAdapter);

        if (savedInstanceState == null) {
            Intent callingIntent = getActivity().getIntent();
            recipe = callingIntent.getParcelableExtra("recipe");
        } else {
            recipe = savedInstanceState.getParcelable("recipe");
        }

        Picasso.with(getContext())
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
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle(recipe.getName());

        mRecipeStepAdapter.setRecipeStepData(recipe.getSteps());

        // start with the scrollview scrolled all the way up if we're not restoring a savedInstanceState
        if (savedInstanceState == null) {
            svRecipeDetail.smoothScrollTo(0, 0);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("recipe", recipe);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(int whichStep) {
        Intent intent = new Intent(getActivity(), RecipeStepActivity.class);
        intent.putExtra("recipe", recipe);
        intent.putExtra("whichStep", whichStep);
        startActivity(intent);
    }
}
