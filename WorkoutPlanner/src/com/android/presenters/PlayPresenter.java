package com.android.presenters;

import android.widget.Toast;
import com.android.dal.XMLWorkoutReader;
import com.android.global.Consts;
import com.android.global.Globals;
import com.android.views.PlayActivity;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public class PlayPresenter extends APresenter {

	private PlayActivity mCurrentView;

	public PlayPresenter(PlayActivity context) {
		// A presenter must hold a reference to its view
		mCurrentView = context;
	}
	
	public void loadWorkoutData(String workoutName) {
		// READING FILE
		try {
			mCurrentView.mWorkout = XMLWorkoutReader.ReadFile(mCurrentView.openFileInput(workoutName + Consts.FILE_EXTENSION));

			// Set id
			for (int i = 0; i < Globals.sFatherSet.getElements().size(); i++) {
				Globals.sFatherSet.getElements().get(i).setId(i);
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
}
