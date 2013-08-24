package com.android.presenters;

import android.widget.Toast;
import com.android.dal.XMLWorkoutReader;
import com.android.element.Set;
import com.android.global.Consts;
import com.android.global.Globals;
import com.android.views.PreviewActivity;
import com.android.views.SaveConfirmDialog;
import com.android.views.SaveDialog;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public class PreviewPresenter extends APresenter {

	private PreviewActivity mCurrentView;

	// private Set mWorkoutFatherSet = Globals.sFatherSet;

	public PreviewPresenter(PreviewActivity context) {
		// A presenter must hold a reference to its view
		mCurrentView = context;
	}

	public void updateToolbar() {
		// TODO Complete
	}

	public void save() {
		// Check for set name
		if (Globals.sFatherSet.getName().equals("")) {
			// If no name exists call save dialog to get name for file
			SaveDialog save = new SaveDialog();
			save.show(mCurrentView.getFragmentManager(), null);
		}
		else {
			// Confirm overwrite
			SaveConfirmDialog confirm = SaveConfirmDialog.newInstance(Globals.sFatherSet.getName());
			confirm.show(mCurrentView.getFragmentManager(), null);
		}
	}

	public void saveAs() {
		// Call save dialog to get new name for file
		SaveDialog save = new SaveDialog();
		save.show(mCurrentView.getFragmentManager(), null);
	}

	public void updateElementData() {
		// TODO Complete
	}

	public void loadWorkoutData(String workoutName) {
		// READING FILE
		try {
			Globals.sFatherSet = XMLWorkoutReader.ReadFile(mCurrentView.openFileInput(workoutName + Consts.FILE_EXTENSION));

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

	public void createNewWorkout() {
		// Creating the first set for the user to work on
		Globals.sFatherSet.getElements().add(new Set(0));
	}

	public void removeElement() {
		// TODO Complete
	}

	public void addElement() {
		// TODO Complete
	}

	// XXX Make sure this is necessary - This getter is for the view to use the
	// data from the presenter to create the UI wigets & such.
	// If the presenter should be the one setting up wigets as well as taking
	// care of logic this method is not needed and methods
	// Such as CreateNewWorkout and LoadWorkoutData in PreviewActivity are to be
	// removed!
	public Set getFatherSet() {
		return Globals.sFatherSet;
	}
}
