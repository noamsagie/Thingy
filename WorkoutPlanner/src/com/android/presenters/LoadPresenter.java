package com.android.presenters;

import com.android.views.LoadDialog;
import java.io.File;

public class LoadPresenter extends APresenter {

	private String[] m_arrWorkouts;
	private LoadDialog m_currentView;

	public LoadPresenter(LoadDialog context) {
		// A presenter must hold a reference to its view
		m_currentView = context;

		// Loading the list of available workouts
		LoadWorkoutList();
	}

	private void LoadWorkoutList() {
		// Length of file extension characters
		final int FILE_EXTENSION = 4;

		// Get access to the app storage file
		File workoutFile = new File(m_currentView.getActivity().getFilesDir()
				.getPath());

		String[] workoutFiles = workoutFile.list();

		// If there are any workouts at all
		if (workoutFiles != null) {
			// Initialize array of workouts
			m_arrWorkouts = new String[workoutFiles.length];

			// Get all workout names from file
			for (int i = 0; i < workoutFiles.length; i++) {
				// Add current workout and remove ".xml" from its name
				m_arrWorkouts[i] = workoutFiles[i].substring(0,
						workoutFiles[i].length() - FILE_EXTENSION);
			}
		}
	}

	public String[] getWorkouts() {
		return m_arrWorkouts;
	}

	public String getWorkout(int index) {
		return m_arrWorkouts[index];
	}
}
