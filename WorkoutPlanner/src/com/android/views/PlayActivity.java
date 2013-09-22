package com.android.views;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.element.AElement;
import com.android.element.Exercise;
import com.android.element.Rest;
import com.android.element.TimeExercise;
import com.android.global.Consts;
import com.android.presenters.PlayPresenter;
import java.util.Timer;

public class PlayActivity extends Activity {

	PlayPresenter mPresenter;
	public AElement mCurrentElement;
	public boolean mIsPaused = true;
	public MediaPlayer mCurrentSound;
	public Button mCurrentElementFinishButton;
	public ImageButton mPreviousButton, mNextButton, mPauseButton, mPlayButton;
	public TextView mExerciseName, mExerciseComment, mExerciseReps, mPreviousElementName, mPreviousElementText, mPreviousElementReps, mCurrentElementName, mCurrentElementComment, mCurrentElementReps, mCurrentElementWeights, mCurrentElementTimer, mNextElementName, mNextElementText, mNextElementReps;

	// Note, if at some point sets may hold sets, holding the current exercise
	// won't work
	public int mCurrentSet, mCurrentExerciseIndex, mCurrentElementIndex = 0;
	public Exercise mWorkout, mCurrentExercise;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setting up basic activity requirements
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_play);

		// Create presenter
		mPresenter = new PlayPresenter(this);

		// Load the workout data into the father set
		if (getIntent().getExtras() != null) {
			mPresenter.loadWorkoutData(getIntent().getExtras().getString(Consts.SELECT_WORKOUT_TAG));
		}
		else {
			// TODO Set something in it
			mWorkout = null;
		}

		// Initialize current exercise and current element
		if (mWorkout.getElements().size() > 0) {
			mCurrentExercise = (Exercise) mWorkout.getElements().get(0);
		}
		else {
			Toast.makeText(this, R.string.empty_workout, Toast.LENGTH_SHORT).show();
			mCurrentExercise = null;
			mCurrentElement = null;
			finish();
		}

		// If exercise is empty, look for the first one with elements
		if (mCurrentExercise.getElements().size() == 0) {
			if (!mPresenter.searchForNextExerciseWithElements()) {
				mCurrentExercise = null;
				mCurrentElement = null;
				mCurrentElementIndex = 0;
				mCurrentSet = 0;
				mCurrentExerciseIndex = 0;
				Toast.makeText(this, R.string.empty_workout, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
		else {
			// Set first element as current
			mCurrentElement = mCurrentExercise.getElements().get(mCurrentElementIndex);
		}

		// If current element and exercise are found, finish creating the rest
		// of the activity
		if (mCurrentExercise != null && mCurrentElement != null) {
			// Set views
			setViews();

			// Set events
			setEvents();

			// Populate fields for the first time
			mPresenter.populateViews();
		}
	}

	private void setViews() {
		mPreviousButton = (ImageButton) findViewById(R.id.prevElement);
		mNextButton = (ImageButton) findViewById(R.id.nextElement);
		mPauseButton = (ImageButton) findViewById(R.id.pauseWorkout);
		mPlayButton = (ImageButton) findViewById(R.id.playWorkout);

		mCurrentElementFinishButton = (Button) findViewById(R.id.currentElementButton);

		mExerciseComment = (TextView) findViewById(R.id.exComment);
		mExerciseName = (TextView) findViewById(R.id.exName);
		mExerciseReps = (TextView) findViewById(R.id.exReps);
		mPreviousElementName = (TextView) findViewById(R.id.prevElementName);
		mPreviousElementText = (TextView) findViewById(R.id.prevElementText);
		mPreviousElementReps = (TextView) findViewById(R.id.prevElementReps);
		mCurrentElementComment = (TextView) findViewById(R.id.currElementComment);
		mCurrentElementName = (TextView) findViewById(R.id.currElementName);
		mCurrentElementReps = (TextView) findViewById(R.id.currElementReps);
		mCurrentElementWeights = (TextView) findViewById(R.id.currElementWeights);
		mCurrentElementTimer = (TextView) findViewById(R.id.currentElementTimer);
		mNextElementName = (TextView) findViewById(R.id.nextElementName);
		mNextElementText = (TextView) findViewById(R.id.nextElementText);
		mNextElementReps = (TextView) findViewById(R.id.nextElementReps);
	}

	private void setEvents() {
		mPreviousButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Attempt to pause
				mPresenter.resetTimer();

				// Move to previous element
				mPresenter.previousElement();

				// Re populate
				mPresenter.populateViews();

				// Check if current element is using a timer
				if (mCurrentElement instanceof TimeExercise || mCurrentElement instanceof Rest) {
					// Cancel play button & enable pause
					mPlayButton.setClickable(false);
					mPlayButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_av_play_disable));
					mPauseButton.setClickable(true);
					mPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_av_pause_enable));
				}
			}
		});
		mNextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Attempt to pause
				mPresenter.resetTimer();

				// Move to previous element
				mPresenter.nextElement();

				// Re populate
				mPresenter.populateViews();

				// Check if current element is using a timer
				if (mCurrentElement instanceof TimeExercise || mCurrentElement instanceof Rest) {
					// Cancel play button & enable pause
					mPlayButton.setClickable(false);
					mPlayButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_av_play_disable));
					mPauseButton.setClickable(true);
					mPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_av_pause_enable));
				}
			}
		});
		mPauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Check if current element is using a timer
				if (mCurrentElement instanceof TimeExercise || mCurrentElement instanceof Rest) {
					mPresenter.mTimer.cancel();
					mPresenter.mTimer.purge();

					mPresenter.disablePauseEnablePlay();
				}
			}
		});
		mPlayButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Check if current element is using a timer
				if (mCurrentElement instanceof TimeExercise || mCurrentElement instanceof Rest) {
					// Restart timer
					mPresenter.mTimer = new Timer();

					// Reset timer task
					if (mCurrentElement instanceof TimeExercise) {
						mPresenter.setTimerTask(((TimeExercise) mCurrentElement).getTime(), false);
					}
					else if (mCurrentElement instanceof Rest) {
						mPresenter.setTimerTask(((Rest) mCurrentElement).getTime(), true);
					}

					// Restart timer
					mPresenter.mTimer.schedule(mPresenter.mTask, 0, mPresenter.INTERVAL_TIME_TIMER);

					mPresenter.disablePlayEnablePause();
				}
			}
		});
		mCurrentElementFinishButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Make sound
				synchronized (this) {
					mCurrentSound.start();
				}

				// Move to the next element and re-populate
				mPresenter.nextElement();
				mPresenter.populateViews();
			}
		});
	}

	@Override
	protected void onDestroy() {
		// Attempt to "pause" to stop timer from continuing
		if (mPauseButton != null) {
			mPauseButton.performClick();
		}
		super.onDestroy();
	}
}
