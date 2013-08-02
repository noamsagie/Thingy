package com.android.presenters;

import android.content.Context;
import com.android.dal.XMLWorkoutWriter;
import com.android.global.Consts;
import com.android.global.Globals;
import com.android.views.SaveConfirmDialog;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public class SaveConfirmPresenter {

	private SaveConfirmDialog m_currentView;

	public SaveConfirmPresenter(SaveConfirmDialog context) {
		// A presenter must hold a reference to its view
		m_currentView = context;
	}

	// Saves the file
	public void Save() {
		// WRITING FILE
		try {
			XMLWorkoutWriter.WriteFile(
					// XXX Decide if the current solution to the problem of
					// getting the father set is a decent one
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
}
