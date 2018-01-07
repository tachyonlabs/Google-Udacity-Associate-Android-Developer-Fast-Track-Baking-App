package com.tachyonlabs.bakingapp.fragments;

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

import com.tachyonlabs.bakingapp.R;
import com.tachyonlabs.bakingapp.models.Recipe;
import com.tachyonlabs.bakingapp.models.RecipeStep;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


public class RecipeStepFragment extends Fragment implements ExoPlayer.EventListener {
    private static final String TAG = RecipeStepFragment.class.getSimpleName();
    private static MediaSessionCompat mediaSession;
    private Recipe recipe;
    private RecipeStep[] recipeSteps;
    private RecipeStep recipeStep;
    private String recipeName;
    private int whichStep;
    private SimpleExoPlayer simpleExoPlayer;
    private SimpleExoPlayerView simpleExoPlayerView;
    private PlaybackStateCompat.Builder stateBuilder;
    private View rootView;
    private Button btnNextStep;
    private Button btnPreviousStep;

    public RecipeStepFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);
        simpleExoPlayerView = rootView.findViewById(R.id.xo_recipe_step_videos);
        simpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.black_rectangle));

        // Initialize the player.
        initializePlayer();
        simpleExoPlayer.addListener(this);

        btnNextStep = rootView.findViewById(R.id.btn_next_step);
        btnPreviousStep = rootView.findViewById(R.id.btn_previous_step);
        btnNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextOrPreviousStep(v);
            }
        });
        btnPreviousStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextOrPreviousStep(v);
            }
        });

        Intent callingIntent = getActivity().getIntent();
        if (callingIntent.hasExtra("recipe")) {
            recipe = callingIntent.getParcelableExtra("recipe");
            whichStep = callingIntent.getIntExtra("whichStep", 0);
            recipeName = recipe.getName();
            recipeSteps = recipe.getSteps();
            recipeStep = recipeSteps[whichStep];
            updateStepNumberDescriptionAndVideo();
        }

        return rootView;
    }

    private void initializePlayer() {
        if (simpleExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            simpleExoPlayerView.setPlayer(simpleExoPlayer);
        }
    }

    public void nextOrPreviousStep(View view) {
        // if the user taps Next or Previous, update the UI accordingly
        if (view == btnPreviousStep) {
            whichStep--;
            btnNextStep.setEnabled(true);
            btnNextStep.setClickable(true);
            btnNextStep.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            if (whichStep == 0) {
                btnPreviousStep.setEnabled(false);
                btnPreviousStep.setClickable(false);
                btnPreviousStep.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDisabled));
            }
        } else {
            whichStep++;
            btnPreviousStep.setEnabled(true);
            btnPreviousStep.setClickable(true);
            btnPreviousStep.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            if (whichStep == recipeSteps.length - 1) {
                btnNextStep.setEnabled(false);
                btnNextStep.setClickable(false);
                btnNextStep.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDisabled));
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
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle(recipeName + stepNumberString);
        TextView tvStepDescription = rootView.findViewById(R.id.tv_step_description);
        String stepDescription = (whichStep > 0 ? "Step " : "") + recipeStep.getDescription();
        tvStepDescription.setText(stepDescription);

        // update the video for the selected step
        if (recipeStep.getVideoUrl().equals("")) {
            simpleExoPlayerView.setVisibility(View.GONE);
        } else {
            simpleExoPlayerView.setVisibility(View.VISIBLE);
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(recipeStep.getVideoUrl()), new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
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
        ProgressBar pbVideoLoadingIndicator = rootView.findViewById(R.id.pb_video_loading_indicator);
        pbVideoLoadingIndicator.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN );
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
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        simpleExoPlayer.stop();
        simpleExoPlayer.release();
        simpleExoPlayer = null;
    }
}
