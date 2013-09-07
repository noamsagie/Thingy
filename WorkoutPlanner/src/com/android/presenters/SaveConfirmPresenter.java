package com.android.presenters;

import com.android.views.R;

import android.widget.Toast;

import android.content.Context;
import com.android.dal.XMLWorkoutWriter;
import com.android.global.Consts;
import com.android.global.Globals;
import com.android.views.SaveConfirmDialog;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public class SaveConfirmPresenter {

	private SaveConfirmDialog mCurrentView;

	public SaveConfirmPresenter(SaveConfirmDialog context) {
		// A presenter must hold a reference to its view
		mCurrentView = context;
	}

	// Saves the file
	public void save() {
		// Remove existing file if there is
		mCurrentView.getActivity().deleteFile(mCurrentView.getWorkoutName() + Consts.FILE_EXTENSION);
		
		// Setting name to fatherSet
		Globals.sFatherExercise.setName(mCurrentView.getWorkoutName());
		
		// WRITING FILE
		try {
			XMLWorkoutWriter.WriteFile(
					// XXX Decide if the current solution to the problem of
					// getting the father Exercise is a decent one
					Globals.sFatherExercise,
					mCurrentView.getActivity().openFileOutput(
							mCurrentView.getWorkoutName()
									+ Consts.FILE_EXTENSION,
							Context.MODE_APPEND));
			Toast.makeText(mCurrentView.getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();
			
			// Change file name to the new name so saving would overwrite new file
			Globals.sFatherExercise.setName(mCurrentView.getWorkoutName());
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
}
