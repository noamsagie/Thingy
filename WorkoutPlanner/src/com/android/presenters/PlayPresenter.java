package com.android.presenters;

import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.dal.XMLWorkoutReader;
import com.android.element.AElement;
import com.android.element.Exercise;
import com.android.element.RepetitionExercise;
import com.android.element.Rest;
import com.android.element.TimeExercise;
import com.android.global.Consts;
import com.android.global.Globals;
import com.android.views.PlayActivity;
import com.android.views.R;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import org.xmlpull.v1.XmlPullParserException;

public class PlayPresenter extends APresenter {

	private PlayActivity mCurrentView;

	public final long INTERVAL_TIME_TIMER = 100;
	private final double ONE_SECOND_IN_ML = 1000;

	public Timer mTimer;
	public TimerTask mTask;
	public double mTimeCount = 0;

	public PlayPresenter(PlayActivity context) {
		// A presenter must hold a reference to its view
		mCurrentView = context;
	}

	// Populate views
	public void populateViews() {
		// Current Exercise:
		// Make sure there is a name. If none, put default: Exercise - index
		// value (+1 for readability)
		if (mCurrentView.mCurrentExercise.getName().equals("")) {
			mCurrentView.mExerciseName.setText(mCurrentView.getString(R.string.default_exercise_name) + " " + Integer.toString(mCurrentView.mCurrentExercise.getId() + 1));
		}
		else {
			mCurrentView.mExerciseName.setText(mCurrentView.mCurrentExercise.getName());
		}

		mCurrentView.mExerciseComment.setText(mCurrentView.mCurrentExercise.getComment());
		mCurrentView.mExerciseReps.setText((mCurrentView.mCurrentSet + 1) + "/" + Integer.toString(mCurrentView.mCurrentExercise.getSets()));

		int currentElementIndex = mCurrentView.mCurrentExercise.getElements().indexOf(mCurrentView.mCurrentElement);
		int currentExerciseIndex = mCurrentView.mWorkout.getElements().indexOf(mCurrentView.mCurrentExercise);

		// Previous element - show the data of the previous exercise instead of
		// the previous element in current exercise if current element is index
		// 0. Shows none if first exercise as well
		if (currentElementIndex > 0) {
			AElement prevElement = mCurrentView.mCurrentExercise.getElements().get(currentElementIndex - 1);
			// Make sure there is a name. If none, put default
			if (prevElement.getName().equals("")) {
				// Find the type of element
				if (prevElement instanceof TimeExercise) {
					mCurrentView.mPreviousElementName.setText(R.string.new_element_time);
				}
				else if (prevElement instanceof Rest) {
					mCurrentView.mPreviousElementName.setText(R.string.new_element_rest);
				}
				if (prevElement instanceof RepetitionExercise) {
					mCurrentView.mPreviousElementName.setText(R.string.new_element_repetitions);
				}
			}

			mCurrentView.mPreviousElementName.setText(prevElement.getName());
			mCurrentView.mPreviousElementReps.setText("");
			mCurrentView.mPreviousElementText.setText(R.string.prev_element_text);
		}
		else {
			// If not first exercise
			if (currentExerciseIndex > 0) {
				Exercise prevExercise = (Exercise) mCurrentView.mWorkout.getElements().get(currentExerciseIndex - 1);

				// Make sure there is a name. If none, put default
				if (prevExercise.getName().equals("")) {
					mCurrentView.mPreviousElementName.setText(mCurrentView.getString(R.string.default_exercise_name) + " " + Integer.toString(prevExercise.getId() + 1));
				}
				else {
					mCurrentView.mPreviousElementName.setText(prevExercise.getName());
				}

				mCurrentView.mPreviousElementReps.setText(Integer.toString(prevExercise.getSets()));
			}
			else {
				mCurrentView.mPreviousElementName.setText(R.string.none_exercise_text);
				mCurrentView.mPreviousElementReps.setText("");
			}

			mCurrentView.mPreviousElementText.setText(R.string.prev_exercise_text);
		}

		// Set sound
		try {
			// Create new and set the data source
			mCurrentView.mCurrentSound = new MediaPlayer();
			AssetFileDescriptor afd;

			// Set current element sound unless it's the last set and the last
			// element
			if ((mCurrentView.mCurrentExercise.getElements().size() == currentElementIndex + 1) && (mCurrentView.mCurrentExercise.getSets() == mCurrentView.mCurrentSet + 1)) {
				afd = mCurrentView.getAssets().openFd("sounds/" + mCurrentView.mCurrentExercise.getSound());
			}
			else {
				afd = mCurrentView.getAssets().openFd("sounds/" + mCurrentView.mCurrentElement.getSound());
			}

			mCurrentView.mCurrentSound.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

			// Set event for completion
			mCurrentView.mCurrentSound.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					if (mp.isPlaying())
						mp.pause();

				}
			});

			// Prepare media player after setting data source
			mCurrentView.mCurrentSound.prepare();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Error", e.getLocalizedMessage());
		}

		mCurrentView.mCurrentElementComment.setText(mCurrentView.mCurrentElement.getComment());

		// Set timer/button. Check for element type first
		if (mCurrentView.mCurrentElement instanceof RepetitionExercise) {
			// Show button & hide timer
			mCurrentView.mCurrentElementFinishButton.setVisibility(View.VISIBLE);
			mCurrentView.mCurrentElementTimer.setVisibility(View.GONE);
		}
		else {
			// Hide button & show timer - set time
			mCurrentView.mCurrentElementFinishButton.setVisibility(View.GONE);
			mCurrentView.mCurrentElementTimer.setVisibility(View.VISIBLE);

			// Set time in view
			if (mCurrentView.mCurrentElement instanceof TimeExercise) {
				// Do not use offset for Time exercise
				setTime(((TimeExercise) mCurrentView.mCurrentElement).getTime(), false);
			}
			else {
				// Use offset for Time exercise
				setTime(((Rest) mCurrentView.mCurrentElement).getTime(), true);
			}
		}

		// Make sure there is a name. If none, put default
		if (mCurrentView.mCurrentElement.getName().equals("")) {
			// Find the type of element
			if (mCurrentView.mCurrentElement instanceof TimeExercise) {
				mCurrentView.mCurrentElementName.setText(R.string.new_element_time);
			}
			else if (mCurrentView.mCurrentElement instanceof Rest) {
				mCurrentView.mCurrentElementName.setText(R.string.new_element_rest);
			}
			else if (mCurrentView.mCurrentElement instanceof RepetitionExercise) {
				mCurrentView.mCurrentElementName.setText(R.string.new_element_repetitions);
			}
		}
		else {
			mCurrentView.mCurrentElementName.setText(mCurrentView.mCurrentElement.getName());
		}

		// Set reps and weights if needed
		if (mCurrentView.mCurrentElement instanceof TimeExercise) {
			mCurrentView.mCurrentElementReps.setVisibility(View.GONE);
			mCurrentView.mCurrentElementWeights.setVisibility(View.GONE);
		}
		else if (mCurrentView.mCurrentElement instanceof Rest) {
			mCurrentView.mCurrentElementReps.setVisibility(View.GONE);
			mCurrentView.mCurrentElementWeights.setVisibility(View.GONE);
		}
		else if (mCurrentView.mCurrentElement instanceof RepetitionExercise) {
			mCurrentView.mCurrentElementReps.setVisibility(View.VISIBLE);
			mCurrentView.mCurrentElementWeights.setVisibility(View.VISIBLE);

			// If not an endless set, put number of reps for the current set
			if (((RepetitionExercise) mCurrentView.mCurrentElement).getEndlessSets().get(mCurrentView.mCurrentSet).booleanValue() == false) {
				mCurrentView.mCurrentElementReps.setText(mCurrentView.getString(R.string.reps_label) + Integer.toString(((RepetitionExercise) mCurrentView.mCurrentElement).getReps().get(mCurrentView.mCurrentSet)));
			}
			else {
				mCurrentView.mCurrentElementReps.setText(mCurrentView.getString(R.string.reps_label) + mCurrentView.getString(R.string.infinity));
			}

			mCurrentView.mCurrentElementWeights.setText(mCurrentView.getString(R.string.weight_label) + Double.toString(((RepetitionExercise) mCurrentView.mCurrentElement).getWeights().get(mCurrentView.mCurrentSet)));
		}

		// Next element - show the data of the next exercise instead of
		// the next element in current exercise if current element is index
		// 0. Shows none if last exercise as well
		if (currentElementIndex < mCurrentView.mCurrentExercise.getElements().size() - 1) {
			AElement nextElement = mCurrentView.mCurrentExercise.getElements().get(currentElementIndex + 1);
			// Make sure there is a name. If none, put default
			if (nextElement.getName().equals("")) {
				// Find the type of element
				if (nextElement instanceof TimeExercise) {
					mCurrentView.mNextElementName.setText(R.string.new_element_time);
				}
				else if (nextElement instanceof Rest) {
					mCurrentView.mNextElementName.setText(R.string.new_element_rest);
				}
				if (nextElement instanceof RepetitionExercise) {
					mCurrentView.mNextElementName.setText(R.string.new_element_repetitions);
				}
			}
			else {
				mCurrentView.mNextElementName.setText(nextElement.getName());
			}

			mCurrentView.mNextElementReps.setText("");
			mCurrentView.mNextElementText.setText(R.string.next_element_text);
		}
		else {
			// If not last exercise
			if (currentExerciseIndex < mCurrentView.mWorkout.getElements().size() - 1) {
				Exercise nextExercise = (Exercise) mCurrentView.mWorkout.getElements().get(currentExerciseIndex + 1);

				// Make sure there is a name. If none, put default
				if (nextExercise.getName().equals("")) {
					mCurrentView.mNextElementName.setText(mCurrentView.getString(R.string.default_exercise_name) + " " + Integer.toString(nextExercise.getId() + 1));
				}
				else {
					mCurrentView.mNextElementName.setText(mCurrentView.mWorkout.getElements().get(currentExerciseIndex + 1).getName());
				}

				mCurrentView.mNextElementReps.setText(Integer.toString(nextExercise.getSets()));
			}
			else {
				mCurrentView.mNextElementName.setText(R.string.none_exercise_text);
				mCurrentView.mNextElementReps.setText("");
			}

			mCurrentView.mNextElementText.setText(R.string.next_exercise_text);
		}

		// Finally, disable/enable & show/hide views
		disableAndHide();
	}

	// Search for the first exercise with elements, assuming that exercise index
	// is valid
	public boolean searchForNextExerciseWithElements() {
		boolean isFound = false;

		// Search all exercises
		while (mCurrentView.mWorkout.getElements().size() > mCurrentView.mCurrentExerciseIndex + 1 && !isFound) {
			// If current exercise has elements, stop the search and set up the
			// data in the variables
			if (((Exercise) mCurrentView.mWorkout.getElements().get(mCurrentView.mCurrentExerciseIndex)).getElements().size() > 0) {
				isFound = true;

				// Set indexes and current values
				mCurrentView.mCurrentElementIndex = 0;
				mCurrentView.mCurrentSet = 0;
				mCurrentView.mCurrentExercise = ((Exercise) mCurrentView.mWorkout.getElements().get(mCurrentView.mCurrentExerciseIndex));
				mCurrentView.mCurrentElement = mCurrentView.mCurrentExercise.getElements().get(mCurrentView.mCurrentElementIndex);
			}
			else {
				// Increase exercise index
				mCurrentView.mCurrentExerciseIndex++;
			}
		}

		return isFound;
	}

	private boolean searchForPreviousExerciseWithElements() {
		boolean isFound = false;
		
		// Search all exercises
		while (mCurrentView.mCurrentExerciseIndex >= 0 && !isFound) {
			// If current exercise has elements, stop the search and set up the
			// data in the variables
			if (((Exercise) mCurrentView.mWorkout.getElements().get(mCurrentView.mCurrentExerciseIndex)).getElements().size() > 0) {
				isFound = true;

				// Set indexes and current values
				mCurrentView.mCurrentExercise = ((Exercise) mCurrentView.mWorkout.getElements().get(mCurrentView.mCurrentExerciseIndex));
				mCurrentView.mCurrentElementIndex = mCurrentView.mCurrentExercise.getElements().size() - 1;
				mCurrentView.mCurrentSet = mCurrentView.mCurrentExercise.getSets() - 1;
				mCurrentView.mCurrentElement = mCurrentView.mCurrentExercise.getElements().get(mCurrentView.mCurrentElementIndex);
			}
			else {
				// Decrease exercise index
				mCurrentView.mCurrentExerciseIndex--;
			}
		}

		return isFound;
	}

	// Note: Currently is not made for being called when a timer is paused!
	private void disableAndHide() {
		// Enables or disables buttons and hides or shows fields according to
		// the conditions. Should be implemented by
		// each object but I don't know how to create/add events and such.
		Exercise lastExercise = ((Exercise) mCurrentView.mWorkout.getElements().get(mCurrentView.mWorkout.getElements().size() - 1));
		Exercise firstExercise = ((Exercise) mCurrentView.mWorkout.getElements().get(0));

		// If the current element is the last element of the last exercise,
		// disable next button and change icon. Else do the opposite
		if ((lastExercise.getElements().indexOf(mCurrentView.mCurrentElement)) != -1 && (lastExercise.getElements().indexOf(mCurrentView.mCurrentElement) == lastExercise.getElements().size() - 1)) {
			mCurrentView.mNextButton.setClickable(false);
			mCurrentView.mNextButton.setImageDrawable(mCurrentView.getResources().getDrawable(R.drawable.ic_av_next_disable));
		}
		else {
			mCurrentView.mNextButton.setClickable(true);
			mCurrentView.mNextButton.setImageDrawable(mCurrentView.getResources().getDrawable(R.drawable.ic_av_next_enable));
		}

		// If the current element is the first element of the first exercise and the current set is the first set,
		// disable prev button and change icon. Else do the opposite
		if ((firstExercise.getElements().indexOf(mCurrentView.mCurrentElement) != -1) && 
			(firstExercise.getElements().indexOf(mCurrentView.mCurrentElement) == 0) && 
			(mCurrentView.mCurrentSet == 0)) {
			mCurrentView.mPreviousButton.setClickable(false);
			mCurrentView.mPreviousButton.setImageDrawable(mCurrentView.getResources().getDrawable(R.drawable.ic_av_previous_disable));
		}
		else {
			mCurrentView.mPreviousButton.setClickable(true);
			mCurrentView.mPreviousButton.setImageDrawable(mCurrentView.getResources().getDrawable(R.drawable.ic_av_previous_enable));
		}
		
		// Disable/Enable play/pause buttons
		if (mCurrentView.mCurrentElement != null) {
			disablePlayEnablePause();
		}
	}
	
	// Cancel pause button & enable play
	public void disablePauseEnablePlay() {
		mCurrentView.mPauseButton.setClickable(false);
		mCurrentView.mPauseButton.setImageDrawable(mCurrentView.getResources().getDrawable(R.drawable.ic_av_pause_disable));
		mCurrentView.mPlayButton.setClickable(true);
		mCurrentView.mPlayButton.setImageDrawable(mCurrentView.getResources().getDrawable(R.drawable.ic_av_play_enable));
	}
	
	// Cancel play button & enable pause
	public void disablePlayEnablePause() {
		mCurrentView.mPlayButton.setClickable(false);
		mCurrentView.mPlayButton.setImageDrawable(mCurrentView.getResources().getDrawable(R.drawable.ic_av_play_disable));
		mCurrentView.mPauseButton.setClickable(true);
		mCurrentView.mPauseButton.setImageDrawable(mCurrentView.getResources().getDrawable(R.drawable.ic_av_pause_enable));
	}

	public void loadWorkoutData(String workoutName) {
		// READING FILE
		try {
			mCurrentView.mWorkout = XMLWorkoutReader.ReadFile(mCurrentView.openFileInput(workoutName + Consts.FILE_EXTENSION));

			// Set id
			for (int i = 0; i < Globals.sFatherExercise.getElements().size(); i++) {
				Globals.sFatherExercise.getElements().get(i).setId(i);
			}
		} catch (XmlPullParserException e) {
			Toast.makeText(mCurrentView, "Error reading file. Writing to log...", Toast.LENGTH_SHORT).show();
			// TODO Write to log
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(mCurrentView, "Error reading file. Writing to log...", Toast.LENGTH_SHORT).show();
			// TODO Write to log
			e.printStackTrace();
		} catch (NumberFormatException e) {
			Toast.makeText(mCurrentView, "Error reading file. Writing to log...", Toast.LENGTH_SHORT).show();
			// TODO Write to log
			e.printStackTrace();
		}
	}

	// Sets time in timer
	private void setTime(final double time, boolean bUseRestOffset) {
		mTimer = new Timer(true);

		// Set up timer task
		setTimerTask(time, bUseRestOffset);

		mTimer.schedule(mTask, 0, INTERVAL_TIME_TIMER);
	}

	public void setTimerTask(final double time, boolean bUseRestOffset) {
		SharedPreferences shared = mCurrentView.getSharedPreferences(Consts.PREFS_FILE_TAG, 0);

		// Read rest offset if timer is for rest and rest time is larger than
		// the offset
		final int restOffset = bUseRestOffset && (time - shared.getInt(Consts.REST_OFFSET_TAG, 0) > 0) ? shared.getInt(Consts.REST_OFFSET_TAG, 0) : 0;

		// Set up the timer task for each interval
		mTask = new TimerTask() {

			@Override
			public void run() {
				// Increase sum value
				mTimeCount += INTERVAL_TIME_TIMER;

				// Show time left till end of interval
				new Handler(mCurrentView.getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						String show = Float.toString((float) (time - (float) mTimeCount / ONE_SECOND_IN_ML));
						mCurrentView.mCurrentElementTimer.setText(show.substring(0, show.indexOf(".") + 2));
					}
				});

				// If interval time ended (with rest offset in mind)
				if ((float) mTimeCount / ONE_SECOND_IN_ML >= time - restOffset) {
					// Restart sum and make sound
					mTimeCount = 0;

					mCurrentView.mCurrentSound.start();

					// Move to next element, stop timer, repopulate views
					nextElement();
					mTimer.cancel();
					mTimer.purge();

					// Call populate views on the original thread that called
					// them because it's not allowed otherwise
					mCurrentView.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							populateViews();
						}
					});
				}
			}
		};
	}

	// Move up to the next element
	public void nextElement() {
		// If there are more elements in the current exercise
		if (mCurrentView.mCurrentExercise.getElements().size() > mCurrentView.mCurrentElementIndex + 1) {
			// Next element
			mCurrentView.mCurrentElementIndex++;
		}
		// If there are more sets in the current exercise
		else if (mCurrentView.mCurrentExercise.getSets() > mCurrentView.mCurrentSet + 1) {
			// Reset element counter, next set
			mCurrentView.mCurrentElementIndex = 0;
			mCurrentView.mCurrentSet++;
		}
		// If current exercise is done
		else {
			// Increase exercise index
			mCurrentView.mCurrentExerciseIndex++;

			// If no more exercises with elements found, finish workout
			if (!searchForNextExerciseWithElements()) {
				mCurrentView.mCurrentExercise = null;
				mCurrentView.mCurrentElement = null;
				mCurrentView.mCurrentElementIndex = 0;
				mCurrentView.mCurrentSet = 0;
				mCurrentView.mCurrentExerciseIndex = 0;
				Toast.makeText(mCurrentView, R.string.workout_finished, Toast.LENGTH_SHORT).show();
				mCurrentView.finish();
			}
		}

		// Finally, set exercise and element by new index
		mCurrentView.mCurrentExercise = (Exercise) mCurrentView.mWorkout.getElements().get(mCurrentView.mCurrentExerciseIndex);
		mCurrentView.mCurrentElement = mCurrentView.mCurrentExercise.getElements().get(mCurrentView.mCurrentElementIndex);
	}

	public void previousElement() {
		// If there are previous elements in the current exercise
		if (mCurrentView.mCurrentElementIndex > 0) {
			// Previous element
			mCurrentView.mCurrentElementIndex--;
		}
		// If there are previous sets in the current exercise
		else if (mCurrentView.mCurrentSet > 0) {
			// Set element counter to last index, previous set
			mCurrentView.mCurrentElementIndex = mCurrentView.mCurrentExercise.getElements().size() - 1;
			mCurrentView.mCurrentSet--;
		}
		// If first element and first set of exercise
		else {
			// Decrease exercise index
			mCurrentView.mCurrentExerciseIndex--;

			// If no previous exercises with elements found, set to first valid exercise & element
			if (!searchForPreviousExerciseWithElements()) {
				mCurrentView.mCurrentElementIndex = 0;
				mCurrentView.mCurrentSet = 0;
				mCurrentView.mCurrentExerciseIndex = 0;
				searchForNextExerciseWithElements();
			}
		}

		// Finally, set exercise and element by new index
		mCurrentView.mCurrentExercise = (Exercise) mCurrentView.mWorkout.getElements().get(mCurrentView.mCurrentExerciseIndex);
		mCurrentView.mCurrentElement = mCurrentView.mCurrentExercise.getElements().get(mCurrentView.mCurrentElementIndex);
	}

	public void resetTimer() {
		if (mTimer != null) {
			// If timer isn't null, stop it and reset the time counter (thus
			// canceling the "pause" effect)
			mTimer.cancel();
			mTimer.purge();
			mTimeCount = 0;
		}
	}
}
