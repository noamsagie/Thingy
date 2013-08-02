package com.android.presenters;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;
import com.android.dal.XMLWorkoutWriter;
import com.android.global.Consts;
import com.android.global.Globals;
import com.android.views.R;
import com.android.views.SaveConfirmDialog;
import com.android.views.SaveDialog;
import java.io.File;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public class SavePresenter {

	private SaveDialog m_currentView;

	public SavePresenter(SaveDialog context) {
		// A presenter must hold a reference to its view
		m_currentView = context;
	}

	// Requires name input if empty. Requires confirmation if
	// file already exists
	public void ProcessRequest() {
		// Check if name was given and not just spaces
		if (!m_currentView.getWorkoutName().equals("")) {
			// Check if file already exists
			if (fileExists()) {
				SaveConfirmDialog dialog = new SaveConfirmDialog();

				// Set name of file in bundle before creating the dialog
				Bundle b = new Bundle();

				b.putCharSequence(Consts.WORKOUT_NAME_KEY,
						m_currentView.getWorkoutName());
				dialog.setArguments(b);

				// Show confirmation dialog
				dialog.show(m_currentView.getFragmentManager(), null);
			} else {
				// Save the file
				Save();
			}
		} else {
			// If no name was given, require one
			Toast.makeText(m_currentView.getActivity(),
					R.string.enter_file_name, Toast.LENGTH_SHORT).show();
		}
	}

	// Saves the file
	private void Save() {
		// WRITING FILE
		try {
			XMLWorkoutWriter.WriteFile(
					// XXX Decide if the current solution to the problem is
					// a decent one
					Globals.fatherSet,
					m_currentView.getActivity().openFileOutput(
							m_currentView.getWorkoutName()
									+ Consts.FILE_EXTENSION,
							Context.MODE_APPEND));
		} catch (IllegalArgumentException e1) {
			// TODO Write to log
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Write to log
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Write to log
			e1.printStackTrace();
		} catch (XmlPullParserException e1) {
			// TODO Write to log
			e1.printStackTrace();
		}
	}

	// Returns true if file exists
	private boolean fileExists() {

		boolean result = false;

		// Get access to the file
		File workoutFile = new File(m_currentView.getActivity().getFilesDir()
				.getPath());

		// Get list of workouts
		String[] workoutFiles = workoutFile.list();

		// Check if there are any files at all
		if (workoutFiles != null) {
			for (String currentWorkout : workoutFiles) {
				// If current workout has the same name as input set true and
				// exit
				if (currentWorkout.equals(m_currentView.getWorkoutName()
						+ Consts.FILE_EXTENSION)) {
					result = true;
				}
			}
		}

		return (result);
	}
}
