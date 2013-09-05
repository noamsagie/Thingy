package com.android.presenters;

import android.content.Context;
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

	private SaveDialog mCurrentView;

	public SavePresenter(SaveDialog context) {
		// A presenter must hold a reference to its view
		mCurrentView = context;
	}

	// Requires name input if empty. Requires confirmation if
	// file already exists
	public void processRequest() {
		// Check if name was given and not just spaces
		if (!mCurrentView.getWorkoutName().equals("")) {
			// Check if file already exists
			if (fileExists()) {
				SaveConfirmDialog dialog = SaveConfirmDialog.newInstance(mCurrentView.getWorkoutName());

				// Show confirmation dialog
				dialog.show(mCurrentView.getFragmentManager(), null);
			} else {
				// Save the file
				save();
				
				// Call save complete listeners to run their override
				mCurrentView.mCallback.onSaveCompleted(mCurrentView.getWorkoutName());
			}
		} else {
			// If no name was given, require one
			Toast.makeText(mCurrentView.getActivity(),
					R.string.enter_file_name, Toast.LENGTH_SHORT).show();
		}
	}

	// Saves the file
	private void save() {
		// Remove existing file if there is
		mCurrentView.getActivity().deleteFile(mCurrentView.getWorkoutName() + Consts.FILE_EXTENSION);  
		
		// Setting name to fatherExercise
		Globals.sFatherExercise.setName(mCurrentView.getWorkoutName());
		
		// WRITING FILE
		try {
			XMLWorkoutWriter.WriteFile(
					// XXX Decide if the current solution to the problem is
					// a decent one
					Globals.sFatherExercise,
					mCurrentView.getActivity().openFileOutput(
							mCurrentView.getWorkoutName()
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
		
		Toast.makeText(mCurrentView.getActivity(), Consts.SAVE_SUCCESS, Toast.LENGTH_SHORT).show();
	}

	// Returns true if file exists
	private boolean fileExists() {

		boolean result = false;

		// Get access to the file
		File workoutFile = new File(mCurrentView.getActivity().getFilesDir()
				.getPath());

		// Get list of workouts
		String[] workoutFiles = workoutFile.list();

		// Check if there are any files at all
		if (workoutFiles != null) {
			for (String currentWorkout : workoutFiles) {
				// If current workout has the same name as input Exercise true and
				// exit
				if (currentWorkout.equals(mCurrentView.getWorkoutName()
						+ Consts.FILE_EXTENSION)) {
					result = true;
				}
			}
		}

		return (result);
	}
}
