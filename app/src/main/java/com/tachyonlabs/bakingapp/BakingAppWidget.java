package com.tachyonlabs.bakingapp;

import com.squareup.picasso.Picasso;
import com.tachyonlabs.bakingapp.activities.RecipeListActivity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class BakingAppWidget extends AppWidgetProvider {
    final static String TAG = BakingAppWidget.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_app_widget);
        views.setTextViewText(R.id.appwidget_text, context.getString(R.string.app_name));

        //SharedPreferences sharedPreferencesForWidget = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sharedPreferencesForWidget = context.getSharedPreferences(context.getString(R.string.pref_file_name), Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        String recipeName = sharedPreferencesForWidget.getString(context.getString(R.string.recipe_name_key), "Recipe name");
        String recipeIngredients = sharedPreferencesForWidget.getString(context.getString(R.string.recipe_ingredients_key), "");
        String recipeThumbnailUrl = sharedPreferencesForWidget.getString(context.getString(R.string.recipe_thumbnail_url_key), "");

        views.setTextViewText(R.id.tv_widget_recipe_name, recipeName);
        views.setTextViewText(R.id.tv_widget_ingredients_list, recipeIngredients);
        if (!recipeThumbnailUrl.equals("")) {
            Picasso.with(context)
                    .load(recipeThumbnailUrl)
                    .into(views, R.id.iv_widget_recipe_photo, new int[] {appWidgetId});
        }

        // An intent and click handler to launch the app when tapped
        Intent intent = new Intent(context, RecipeListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

