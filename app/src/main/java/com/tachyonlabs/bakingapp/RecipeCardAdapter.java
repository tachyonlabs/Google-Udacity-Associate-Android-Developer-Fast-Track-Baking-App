package com.tachyonlabs.bakingapp;

import com.tachyonlabs.bakingapp.models.RecipeCard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RecipeCardAdapter extends RecyclerView.Adapter<RecipeCardAdapter.RecipeCardAdapterViewHolder> {
    private static final String TAG = RecipeCardAdapter.class.getSimpleName();
    final private RecipeCardAdapterOnClickHandler mClickHandler;
    private RecipeCard[] mRecipeCards;

    public RecipeCardAdapter(RecipeCardAdapterOnClickHandler recipeCardAdapterOnClickHandler) {
        mClickHandler = recipeCardAdapterOnClickHandler;
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
        String recipeTitle = mRecipeCards[position].getTitle();
        holder.tvTitle.setText(recipeTitle);
    }

    @Override
    public int getItemCount() {
        if (mRecipeCards == null) {
            return 0;
        } else {
            return mRecipeCards.length;
        }
    }

    public void setRecipeCardData(RecipeCard[] recipeCards) {
        mRecipeCards = recipeCards;
        notifyDataSetChanged();
    }

    public interface RecipeCardAdapterOnClickHandler {
        void onClick(RecipeCard clickedItem);
    }

    public class RecipeCardAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView tvTitle;

        public RecipeCardAdapterViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTemp);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            RecipeCard recipeCard = mRecipeCards[getAdapterPosition()];
            mClickHandler.onClick(recipeCard);
        }
    }
}
