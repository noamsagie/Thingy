package com.android.presenters;

import com.android.global.Consts;

import com.android.views.SelectWorkoutActivity;
import java.io.File;

public class SelectWorkoutPresenter extends APresenter {

	private String[] mArrWorkouts;
	private SelectWorkoutActivity mCurrentView;

	public SelectWorkoutPresenter(SelectWorkoutActivity context) {
		// A presenter must hold a reference to its view
		mCurrentView = context;

		// Loading the list of available workouts
		loadWorkoutList();
	}

	private void loadWorkoutList() {
		// Length of file extension characters
		final int FILE_EXTENSION = 4;

		// Get access to the app storage file
		File workoutFile = new File(mCurrentView.getFilesDir()
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
		// Reread files and return
		loadWorkoutList();
		return mArrWorkouts;
	}

	public String getWorkout(int index) {
		return mArrWorkouts[index];
	}
	
	public void deleteFile() {
		// Remove existing file if there is
		mCurrentView.deleteFile(mCurrentView.mSelectedFile + Consts.FILE_EXTENSION);  
	}
}
