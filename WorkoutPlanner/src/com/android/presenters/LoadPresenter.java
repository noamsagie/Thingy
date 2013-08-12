package com.android.presenters;

import com.android.views.LoadDialog;
import java.io.File;

public class LoadPresenter extends APresenter {

	private String[] mArrWorkouts;
	private LoadDialog mCurrentView;

	public LoadPresenter(LoadDialog context) {
		// A presenter must hold a reference to its view
		mCurrentView = context;

		// Loading the list of available workouts
		loadWorkoutList();
	}

	private void loadWorkoutList() {
		// Length of file extension characters
		final int FILE_EXTENSION = 4;

		// Get access to the app storage file
		File workoutFile = new File(mCurrentView.getActivity().getFilesDir()
				.getPath());

		String[] workoutFiles = workoutFile.list();

		// If there are any workouts at all
		if (workoutFiles != null) {
			// Initialize array of workouts
			mArrWorkouts = new String[workoutFiles.length];

			// Get all workout names from file
			for (int i = 0; i < workoutFiles.length; i++) {
				// Add current workout and remove ".xml" from its name
				mArrWorkouts[i] = workoutFiles[i].substring(0,
						workoutFiles[i].length() - FILE_EXTENSION);
			}
		}
	}

	public String[] getWorkouts() {
		return mArrWorkouts;
	}

	public String getWorkout(int index) {
		return mArrWorkouts[index];
	}
}
