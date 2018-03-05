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

import com.squareup.picasso.Picasso;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RecipeStepFragment extends Fragment implements ExoPlayer.EventListener {
    private static final String TAG = RecipeStepFragment.class.getSimpleName();
    private Recipe mRecipe;
    private RecipeStep[] mRecipeSteps;
    private RecipeStep mRecipeStep;
    private String mRecipeName;
    private int mWhichStep;
    private SimpleExoPlayer mSimpleExoPlayer;
    private SimpleExoPlayerView mSimpleExoPlayerView;
    private View mRootView;
    private Button mBtnNextStep;
    private Button mBtnPreviousStep;
    private ImageView mIvStepThumbnail;
    private long mExoplayerPosition = 0;

    public RecipeStepFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);
        mSimpleExoPlayerView = mRootView.findViewById(R.id.xo_recipe_step_videos);
        mSimpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.black_rectangle));

        // Initialize the player.
        initializePlayer();
//        mSimpleExoPlayer.addListener(this);

        // set up next and previous buttons to navigate between steps
        mBtnNextStep = mRootView.findViewById(R.id.btn_next_step);
        mBtnPreviousStep = mRootView.findViewById(R.id.btn_previous_step);
        mBtnNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextOrPreviousStep(v);
            }
        });
        mBtnPreviousStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextOrPreviousStep(v);
            }
        });

        // and the thumbnail ImageView
        mIvStepThumbnail = mRootView.findViewById(R.id.iv_recipe_step_thumbnail);

        if (savedInstanceState == null) {
            Intent callingIntent = getActivity().getIntent();
            if (callingIntent.hasExtra(getString(R.string.recipe_key))) {
                mRecipe = callingIntent.getParcelableExtra(getString(R.string.recipe_key));
                mWhichStep = callingIntent.getIntExtra(getString(R.string.which_step_key), 0);
            }
        } else {
            mRecipe = savedInstanceState.getParcelable(getString(R.string.recipe_key));
            mWhichStep = savedInstanceState.getInt(getString(R.string.which_step_key));
            mExoplayerPosition = savedInstanceState.getLong(getString(R.string.exoplayer_position_key));
        }

        mRecipeName = mRecipe.getName();
        mRecipeSteps = mRecipe.getSteps();
        mRecipeStep = mRecipeSteps[mWhichStep];
        updateStepNumberDescriptionAndVideo(mWhichStep);

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(getString(R.string.recipe_key), mRecipe);
        outState.putInt(getString(R.string.which_step_key), mWhichStep);
        if (mSimpleExoPlayer != null) {
            outState.putLong(getString(R.string.exoplayer_position_key), mSimpleExoPlayer.getCurrentPosition());
        } else {
            outState.putLong(getString(R.string.exoplayer_position_key), mExoplayerPosition);
        }
        super.onSaveInstanceState(outState);
    }

    private void initializePlayer() {
        if (mSimpleExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mSimpleExoPlayerView.setPlayer(mSimpleExoPlayer);
            mSimpleExoPlayer.addListener(this);
        }
    }

    public void nextOrPreviousStep(View view) {
        // if the user taps Next or Previous
        if (view == mBtnPreviousStep) {
            mWhichStep--;
        } else {
            mWhichStep++;
        }
        // the next or previous step will have a different video or no video
        mExoplayerPosition = 0;

        updateStepNumberDescriptionAndVideo(mWhichStep);
    }

    public void updateStepNumberDescriptionAndVideo(int step) {
        // stop any currently-playing video
        mSimpleExoPlayer.stop();

        mWhichStep = step;
        mRecipeStep = mRecipeSteps[step];

        if (step == 0) {
            mBtnPreviousStep.setEnabled(false);
            mBtnPreviousStep.setClickable(false);
            mBtnPreviousStep.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDisabled));
        } else {
            mBtnPreviousStep.setEnabled(true);
            mBtnPreviousStep.setClickable(true);
            mBtnPreviousStep.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        if (step == mRecipeSteps.length - 1) {
            mBtnNextStep.setEnabled(false);
            mBtnNextStep.setClickable(false);
            mBtnNextStep.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDisabled));
        } else {
            mBtnNextStep.setEnabled(true);
            mBtnNextStep.setClickable(true);
            mBtnNextStep.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        // update the step number and description for the selected step
        // the recipe introduction gets grouped with the steps but
        // doesn't have a step number in its description
        String stepNumberString = step > 0 ? String.format(" - Step %d", step) : " - Introduction";
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle(mRecipeName + stepNumberString);
        TextView tvStepDescription = mRootView.findViewById(R.id.tv_step_description);
        String stepDescription = (step > 0 ? "Step " : "") + mRecipeStep.getDescription();
        tvStepDescription.setText(stepDescription);

        // update the video for the selected step
        if (mRecipeStep.getVideoUrl().isEmpty()) {
            // if the selected step has no video, show the thumbnail instead of ExoPlayer
            mSimpleExoPlayerView.setVisibility(View.GONE);
            showThumbnail();

        } else {
            // if the selected step has a video, don't show a thumbnail
            mIvStepThumbnail.setVisibility(View.GONE);

            // check for network before trying to download video -- if there's no connection,
            // ExoPlayer just sits there trying to load without ever giving any feedback
            if (!NetworkUtils.isNetworkAvailable(getContext())) {
                displayFixYourInternetConnectionAlertDialog();
                mSimpleExoPlayerView.setVisibility(View.GONE);
                showThumbnail();
            } else {
                mSimpleExoPlayerView.setVisibility(View.VISIBLE);
                String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
                MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(mRecipeStep.getVideoUrl()), new DefaultDataSourceFactory(
                        getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
                mSimpleExoPlayer.prepare(mediaSource);
                // to maintain time position if the device is rotated or on lost focus; otherwise it's just 0
                mSimpleExoPlayer.seekTo(mExoplayerPosition);
                mSimpleExoPlayer.setPlayWhenReady(true);
            }
        }
    }

    // if a recipe step has no video, or can't load videos due to no network connection
    public void showThumbnail() {
        mIvStepThumbnail.setVisibility(View.VISIBLE);
        String thumbnailUrl = mRecipeStep.getThumbnailUrl();

        // show the thumbnail if (1) there is a thumbnail URL, (2) there is a network connection,
        // and (3) it's a valid image file -- otherwise show the Baking Time placeholder image
        if (!thumbnailUrl.isEmpty()) {
            Picasso.with(getContext())
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.baking_time)
                    .error(R.drawable.baking_time)
                    .into(mIvStepThumbnail);
        } else {
            Picasso.with(getContext())
                    .load(R.drawable.baking_time)
                    .into(mIvStepThumbnail);
        }
    }

    public void displayFixYourInternetConnectionAlertDialog() {
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
        ProgressBar pbVideoLoadingIndicator = mRootView.findViewById(R.id.pb_video_loading_indicator);
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
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23 && mSimpleExoPlayer == null) {
            initializePlayer();
            updateStepNumberDescriptionAndVideo(mWhichStep);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 && mSimpleExoPlayer == null) {
            initializePlayer();
            updateStepNumberDescriptionAndVideo(mWhichStep);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSimpleExoPlayer != null) {
            mExoplayerPosition = mSimpleExoPlayer.getCurrentPosition();
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSimpleExoPlayer != null) {
            mExoplayerPosition = mSimpleExoPlayer.getCurrentPosition();
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSimpleExoPlayer != null) {
            mExoplayerPosition = mSimpleExoPlayer.getCurrentPosition();
            releasePlayer();
        }
    }

    private void releasePlayer() {
        mSimpleExoPlayer.stop();
        mSimpleExoPlayer.release();
        mSimpleExoPlayer = null;
    }
}
