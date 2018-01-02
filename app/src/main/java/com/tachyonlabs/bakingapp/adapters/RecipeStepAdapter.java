package com.tachyonlabs.bakingapp.adapters;

import com.tachyonlabs.bakingapp.R;
import com.tachyonlabs.bakingapp.models.RecipeStep;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeStepAdapterViewHolder> {
    private static final String TAG = RecipeStepAdapter.class.getSimpleName();
    final private RecipeStepAdapter.RecipeStepAdapterOnClickHandler mClickHandler;
    private RecipeStep[] mRecipeSteps;

    public RecipeStepAdapter(RecipeStepAdapter.RecipeStepAdapterOnClickHandler recipeStepAdapterOnClickHandler) {
        mClickHandler = recipeStepAdapterOnClickHandler;
    }

    @Override
    public RecipeStepAdapter.RecipeStepAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_step_card;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        RecipeStepAdapter.RecipeStepAdapterViewHolder viewHolder = new RecipeStepAdapter.RecipeStepAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecipeStepAdapter.RecipeStepAdapterViewHolder holder, int position) {
        int recipeStepNumber = mRecipeSteps[position].getId();
        // the recipe introduction gets grouped with the steps but doesn't have a step number in its description
        String stepNumberString = recipeStepNumber > 0 ? String.format("Step %d: ", recipeStepNumber) : "";
        String recipeShortDescription = mRecipeSteps[position].getShortDescription();

        holder.tvStepNumberAndShortDescription.setText(String.format(stepNumberString + recipeShortDescription));
    }

    @Override
    public int getItemCount() {
        if (mRecipeSteps == null) {
            return 0;
        } else {
            return mRecipeSteps.length;
        }
    }

    public void setRecipeStepData(RecipeStep[] recipeSteps) {
        mRecipeSteps = recipeSteps;
        notifyDataSetChanged();
    }

    public interface RecipeStepAdapterOnClickHandler {
        void onClick(int whichStep);
    }

    public class RecipeStepAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView tvStepNumberAndShortDescription;

        public RecipeStepAdapterViewHolder(View itemView) {
            super(itemView);
            tvStepNumberAndShortDescription = itemView.findViewById(R.id.tv_step_number_and_short_description);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int whichStep = getAdapterPosition();
            mClickHandler.onClick(whichStep);
        }
    }
}
