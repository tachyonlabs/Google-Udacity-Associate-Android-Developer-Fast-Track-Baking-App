package com.tachyonlabs.bakingapp;

import com.squareup.picasso.Picasso;
import com.tachyonlabs.bakingapp.models.Recipe;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class RecipeCardAdapter extends RecyclerView.Adapter<RecipeCardAdapter.RecipeCardAdapterViewHolder> {
    private static final String TAG = RecipeCardAdapter.class.getSimpleName();
    final private RecipeCardAdapterOnClickHandler mClickHandler;
    private Recipe[] mRecipes;
    private HashMap<String, String> photoUrls = new HashMap<>();

    public RecipeCardAdapter(RecipeCardAdapterOnClickHandler recipeCardAdapterOnClickHandler) {
        mClickHandler = recipeCardAdapterOnClickHandler;
        /*
        The JSON for the recipes contains URLs for videos illustrating some of the steps, but no
        links to photos. I would say that in "real life" no one who went to all the trouble to make
        multiple videos for each of their recipes would neglect the very basic step of taking at
        least one photograph of the finished products, so to make this app look more like an actual
        product as well, I am using the following Flickr photos, which the photographers have made
        available for use under the Creative Commons license:

        * For Nutella Pie: "[I invented this](https://www.flickr.com/photos/leedav/4328677446/)", by [Lee Davenport](https://www.flickr.com/people/leedav/)
        * For Brownies: "[Chocolate-Mint Brownies](https://www.flickr.com/photos/theryn/5727350257/)", by [Theryn Fleming](https://www.flickr.com/people/theryn/)
        * For Yellow Cake: "[yellow cake](https://www.flickr.com/photos/stuart_spivack/2584637478/)", by [Stuart Spivack](https://www.flickr.com/people/stuart_spivack/)
        * For Cheesecake: "[Cheesecake Supreme](https://www.flickr.com/photos/cuttingboard/2699220126)", by [Emily Carlin](https://www.flickr.com/people/cuttingboard/)
        */
        photoUrls.put("Nutella Pie", "https://c1.staticflickr.com/5/4001/4328677446_ecdd0479a3_z.jpg");
        photoUrls.put("Brownies", "https://c2.staticflickr.com/6/5224/5727350257_72a6008cc9_z.jpg");
        photoUrls.put("Yellow Cake", "https://c2.staticflickr.com/4/3177/2584637478_bc89ae4a1d_z.jpg");
        photoUrls.put("Cheesecake", "https://c2.staticflickr.com/4/3015/2699220126_cc964a2cd2_z.jpg");
    }

    @Override
    public RecipeCardAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_recipe_card;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        RecipeCardAdapterViewHolder viewHolder = new RecipeCardAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecipeCardAdapterViewHolder holder, int position) {
        String recipeTitle = mRecipes[position].getName();
        String recipeServings = mRecipes[position].getServings() + " serving" + (mRecipes[position].getServings() > 1 ? "s" : "");

        // TODO make placeholder and error images later too
        Picasso.with(holder.ivRecipePhoto.getContext())
                .load(photoUrls.get(recipeTitle))
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.ivRecipePhoto);
        holder.tvRecipeName.setText(recipeTitle);
        holder.tvRecipeServings.setText(recipeServings);
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null) {
            return 0;
        } else {
            return mRecipes.length;
        }
    }

    public void setRecipeCardData(Recipe[] recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    public interface RecipeCardAdapterOnClickHandler {
        void onClick(Recipe recipe);
    }

    public class RecipeCardAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView ivRecipePhoto;
        public final TextView tvRecipeName;
        public final TextView tvRecipeServings;

        public RecipeCardAdapterViewHolder(View itemView) {
            super(itemView);
            ivRecipePhoto = (ImageView) itemView.findViewById(R.id.iv_recipe_photo);
            tvRecipeName = (TextView) itemView.findViewById(R.id.tv_recipe_name);
            tvRecipeServings = (TextView) itemView.findViewById(R.id.tv_recipe_servings);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Recipe recipe = mRecipes[getAdapterPosition()];
            mClickHandler.onClick(recipe);
        }
    }
}
