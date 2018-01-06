package com.tachyonlabs.bakingapp;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import com.tachyonlabs.bakingapp.databinding.ActivityRecipeStepBinding;
import com.tachyonlabs.bakingapp.models.Recipe;
import com.tachyonlabs.bakingapp.models.RecipeStep;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RecipeStepActivity extends AppCompatActivity implements ExoPlayer.EventListener {
    private static final String TAG = RecipeStepActivity.class.getSimpleName();
    private static MediaSessionCompat mediaSession;
    ActivityRecipeStepBinding mBinding;
    private Recipe recipe;
    private RecipeStep[] recipeSteps;
    private RecipeStep recipeStep;
    private String recipeName;
    private int whichStep;
    private SimpleExoPlayer simpleExoPlayer;
    private SimpleExoPlayerView simpleExoPlayerView;
    private PlaybackStateCompat.Builder stateBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_step);

        simpleExoPlayerView = mBinding.xoRecipeStepVideos;
        simpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.black_rectangle));

        // Initialize the player.
        initializePlayer();
        simpleExoPlayer.addListener(this);

        Intent callingIntent = getIntent();
        if (callingIntent.hasExtra("recipe")) {
            recipe = callingIntent.getParcelableExtra("recipe");
            whichStep = callingIntent.getIntExtra("whichStep", 0);
            recipeName = recipe.getName();
            recipeSteps = recipe.getSteps();
            recipeStep = recipeSteps[whichStep];
            updateStepNumberDescriptionAndVideo();
        }
    }

    private void initializePlayer() { //Uri mediaUri) {
        if (simpleExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            simpleExoPlayerView.setPlayer(simpleExoPlayer);
        }
    }


    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        simpleExoPlayer.stop();
        simpleExoPlayer.release();
        simpleExoPlayer = null;
    }

    public void nextOrPreviousStep(View view) {
        // if the user taps Next or Previous, update the UI accordingly
        if (view == mBinding.btnPreviousStep) {
            whichStep--;
            mBinding.btnNextStep.setEnabled(true);
            mBinding.btnNextStep.setClickable(true);
            mBinding.btnNextStep.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            if (whichStep == 0) {
                mBinding.btnPreviousStep.setEnabled(false);
                mBinding.btnPreviousStep.setClickable(false);
                mBinding.btnPreviousStep.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDisabled));
            }
        } else {
            whichStep++;
            mBinding.btnPreviousStep.setEnabled(true);
            mBinding.btnPreviousStep.setClickable(true);
            mBinding.btnPreviousStep.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            if (whichStep == recipeSteps.length - 1) {
                mBinding.btnNextStep.setEnabled(false);
                mBinding.btnNextStep.setClickable(false);
                mBinding.btnNextStep.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDisabled));
            }
        }
        recipeStep = recipeSteps[whichStep];
        updateStepNumberDescriptionAndVideo();
    }

    private void updateStepNumberDescriptionAndVideo() {
        // stop any currently-playing video
        simpleExoPlayer.stop();

        // update the step number and description for the selected step
        // the recipe introduction gets grouped with the steps but
        // doesn't have a step number in its description
        String stepNumberString = whichStep > 0 ? String.format(" - Step %d", whichStep) : " - Introduction";
        ActionBar ab = getSupportActionBar();
        ab.setTitle(recipeName + stepNumberString);
        TextView tvStepDescription = mBinding.tvStepDescription;
        String stepDescription = (whichStep > 0 ? "Step " : "") + recipeStep.getDescription();
        tvStepDescription.setText(stepDescription);

        // update the video for the selected step
        if (recipeStep.getVideoUrl().equals("")) {
            simpleExoPlayerView.setVisibility(View.GONE);
        } else {
            simpleExoPlayerView.setVisibility(View.VISIBLE);
            String userAgent = Util.getUserAgent(this, "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(recipeStep.getVideoUrl()), new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        ProgressBar pbVideoLoadingIndicator = mBinding.pbVideoLoadingIndicator;
        pbVideoLoadingIndicator.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );
        if (playbackState == SimpleExoPlayer.STATE_BUFFERING) {
            pbVideoLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            pbVideoLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
}
