package com.tachyonlabs.bakingapp.activities;

import com.tachyonlabs.bakingapp.R;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class RecipeListActivity extends AppCompatActivity {

    private final static String TAG = RecipeListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
    }

}
