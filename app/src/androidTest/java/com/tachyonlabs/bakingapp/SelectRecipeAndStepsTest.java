package com.tachyonlabs.bakingapp;

import com.tachyonlabs.bakingapp.activities.RecipeListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

@RunWith(AndroidJUnit4.class)
public class SelectRecipeAndStepsTest {

    public static final String BROWNIES = "Brownies";
    public static final String STEP_1 = "Step 1";
    public static final String STEP_2 = "Step 2";
    public static final String RECIPE_INTRODUCTION = "Recipe Introduction";

    @Rule
    public ActivityTestRule<RecipeListActivity> mActivityTestRule = new ActivityTestRule<>(RecipeListActivity.class);

    @Test
    public void browniesSecondInRecipeList() {
        // When the app loads, is Brownies the second recipe?
        onView(withId(R.id.rv_recipe_cards))
                .perform(RecyclerViewActions
                        .scrollToPosition(1));

        onView(withText(BROWNIES)).check(matches(isDisplayed()));
    }

    @Test
    public void selectRecipeAndSteps() {
        // If you tap the Brownies recipe card in RecipeListActivity ...
        onView(withId(R.id.rv_recipe_cards))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(1, click()));

        // ... does it bring up RecipeDetailActivity, with Brownies as the selected recipe?
        onView(withId(R.id.tv_recipe_detail_name)).check(matches(withText(BROWNIES)));

        // And if you then tap Step 1 in the list of steps (which is actually second in the list
        // of steps, following Recipe Introduction ...
        onView(withId(R.id.rv_recipe_steps))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(1, click()));

        // ... does it bring up the instructions for Step 1, either in RecipeStepActivity (phone)
        // or in RecipeStepFragment in RecipeDetailActivity (tablet)?
        onView(withId(R.id.tv_step_description)).check(matches(withText(startsWith(STEP_1))));

        // And if you then tap the Next button ...
        onView(withId(R.id.btn_next_step)).perform(click());

        // ... does it advance to step 2?
        onView(withId(R.id.tv_step_description)).check(matches(withText(startsWith(STEP_2))));

        // And if you then tap the Previous button ...
        onView(withId(R.id.btn_previous_step)).perform(click());

        // ... does it return to step 1?
        onView(withId(R.id.tv_step_description)).check(matches(withText(startsWith(STEP_1))));

        // And if you then tap the Previous button a second time ...
        onView(withId(R.id.btn_previous_step)).perform(click());

        // ... does it go to the Recipe Introduction step?
        onView(withId(R.id.tv_step_description)).check(matches(withText(RECIPE_INTRODUCTION)));

        // And when you're at the Recipe Introduction step, is the Previous button no longer enabled?
        onView(withId(R.id.btn_previous_step)).check(matches(not(isEnabled())));
    }

}
