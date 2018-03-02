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
import com.tachyonlabs.bakingapp.utilities.NetworkUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
    private View rootView;
    private Button btnNextStep;
    private Button btnPreviousStep;
    private long exoplayerPosition = 0;

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

        if (savedInstanceState == null) {
            Intent callingIntent = getActivity().getIntent();
            if (callingIntent.hasExtra(getString(R.string.recipe_key))) {
                recipe = (Recipe) callingIntent.getParcelableExtra(getString(R.string.recipe_key));
                whichStep = callingIntent.getIntExtra(getString(R.string.which_step_key), 0);
            }
        } else {
            recipe = savedInstanceState.getParcelable(getString(R.string.recipe_key));
            whichStep = savedInstanceState.getInt(getString(R.string.which_step_key));
            exoplayerPosition = savedInstanceState.getLong(getString(R.string.exoplayer_position_key));
        }

        recipeName = recipe.getName();
        recipeSteps = recipe.getSteps();
        recipeStep = recipeSteps[whichStep];
        updateStepNumberDescriptionAndVideo(whichStep);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(getString(R.string.recipe_key), recipe);
        outState.putInt(getString(R.string.which_step_key), whichStep);
        outState.putLong(getString(R.string.exoplayer_position_key), simpleExoPlayer.getCurrentPosition());
        super.onSaveInstanceState(outState);
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
        // if the user taps Next or Previous
        if (view == btnPreviousStep) {
            whichStep--;
        } else {
            whichStep++;
        }
        updateStepNumberDescriptionAndVideo(whichStep);
    }

    public void updateStepNumberDescriptionAndVideo(int step) {
        // stop any currently-playing video
        simpleExoPlayer.stop();

        whichStep = step;
        recipeStep = recipeSteps[step];

        if (step == 0) {
            btnPreviousStep.setEnabled(false);
            btnPreviousStep.setClickable(false);
            btnPreviousStep.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDisabled));
        } else {
            btnPreviousStep.setEnabled(true);
            btnPreviousStep.setClickable(true);
            btnPreviousStep.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        if (step == recipeSteps.length - 1) {
            btnNextStep.setEnabled(false);
            btnNextStep.setClickable(false);
            btnNextStep.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDisabled));
        } else {
            btnNextStep.setEnabled(true);
            btnNextStep.setClickable(true);
            btnNextStep.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        // update the step number and description for the selected step
        // the recipe introduction gets grouped with the steps but
        // doesn't have a step number in its description
        String stepNumberString = step > 0 ? String.format(" - Step %d", step) : " - Introduction";
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle(recipeName + stepNumberString);
        TextView tvStepDescription = rootView.findViewById(R.id.tv_step_description);
        String stepDescription = (step > 0 ? "Step " : "") + recipeStep.getDescription();
        tvStepDescription.setText(stepDescription);

        // update the video for the selected step
        if (recipeStep.getVideoUrl().equals("")) {
            // if the selected step has no video, don't show the player
            simpleExoPlayerView.setVisibility(View.GONE);
        } else {
            if (!NetworkUtils.isNetworkAvailable(getContext())) {
                // check for network before trying to download video -- if there's no connection,
                // ExoPlayer just sits there trying to load without ever giving any feedback
                fixYourInternetConnectionDialog();
                simpleExoPlayerView.setVisibility(View.GONE);
            } else {
                simpleExoPlayerView.setVisibility(View.VISIBLE);
                String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
                MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(recipeStep.getVideoUrl()), new DefaultDataSourceFactory(
                        getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
                simpleExoPlayer.prepare(mediaSource);
                // to maintain time position in case of savedInstanceState != null; otherwise it's just 0
                simpleExoPlayer.seekTo(exoplayerPosition);
                simpleExoPlayer.setPlayWhenReady(true);
            }
        }
    }

    public void fixYourInternetConnectionDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getContext());
        }
        builder.setTitle(getString(R.string.no_internet_connection_cant_load_videos))
                .setMessage(getString(R.string.fix_internet_connection_or_cant_load_videos))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // just return
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert).show();
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
                .setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
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

    private void releasePlayer() {
        simpleExoPlayer.stop();
        simpleExoPlayer.release();
        simpleExoPlayer = null;
    }
}
