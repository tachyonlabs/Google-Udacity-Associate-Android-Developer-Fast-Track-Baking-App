package com.tachyonlabs.bakingapp.fragments;

import com.tachyonlabs.bakingapp.R;
import com.tachyonlabs.bakingapp.adapters.RecipeCardAdapter;
import com.tachyonlabs.bakingapp.models.Recipe;
import com.tachyonlabs.bakingapp.utilities.NetworkUtils;
import com.tachyonlabs.bakingapp.utilities.RecipeJsonUtils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;


public class RecipeListFragment extends Fragment implements RecipeCardAdapter.RecipeCardAdapterOnClickHandler {
    private final static String TAG = RecipeListFragment.class.getSimpleName();
    // Define a new interface OnStepClickListener that triggers a callback in the host activity
    RecipeListFragment.OnRecipeClickListener mRecipeCallback;
    private RecyclerView mRecyclerView;
    private RecipeCardAdapter mRecipeCardAdapter;
    private TextView tvErrorMessageDisplay;
    private ProgressBar pbLoadingIndicator;
    private Recipe[] recipes;

    public RecipeListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mRecipeCallback = (RecipeListFragment.OnRecipeClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnRecipeClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        mRecyclerView = rootView.findViewById(R.id.rv_recipe_cards);

        // set the number of columns according to the dp width of the device's screen and rotation
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int dpWidth = Math.round(displayMetrics.widthPixels / displayMetrics.density);
        int columns = Math.max(1, (int) Math.floor(dpWidth / 300));
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), columns);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecipeCardAdapter = new RecipeCardAdapter(this);
        mRecyclerView.setAdapter(mRecipeCardAdapter);
        tvErrorMessageDisplay = rootView.findViewById(R.id.tv_error_message_display);
        pbLoadingIndicator = rootView.findViewById(R.id.pb_loading_indicator);
        pbLoadingIndicator.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        if (savedInstanceState == null) {
            loadRecipes();
        } else {
            recipes = (Recipe[]) (savedInstanceState.getParcelableArray(getString(R.string.recipes_key)));
            mRecipeCardAdapter.setRecipeCardData(recipes);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(getString(R.string.recipes_key), recipes);
        super.onSaveInstanceState(outState);
    }

    private void loadRecipes() {
        new FetchRecipesTask().execute();
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
        mRecipeCallback.onRecipeSelected(recipe);
    }

    // OnStepClickListener interface, calls a method in the host activity named onStepSelected
    public interface OnRecipeClickListener {
        void onRecipeSelected(Recipe recipe);
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
                return RecipeJsonUtils.getRecipesFromJson(getActivity(), jsonRecipesResponse);

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

}
