package com.android.presenters;

import android.widget.Toast;
import com.android.dal.XMLWorkoutReader;
import com.android.element.Set;
import com.android.views.PreviewActivity;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public class PreviewPresenter extends APresenter {

	private PreviewActivity mCurrentView;
	private Set mWorkoutFatherSet = new Set();
	
	public PreviewPresenter(PreviewActivity context)
	{
		// A presenter must hold a reference to its view
		mCurrentView = context;
	}
	
	public void updateToolbar()
	{
		// TODO Complete
	}
	
	public void save()
	{
		// TODO Complete
	}
	
	public void updateElementData()
	{
		// TODO Complete
	}
	
	public void loadWorkoutData(String workoutName)
	{
		// READING FILE
		try {
			mWorkoutFatherSet = XMLWorkoutReader.ReadFile(mCurrentView.openFileInput(workoutName + ".xml"));
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
	
	public void createNewWorkout()
	{
		// Creating the first set for the user to work on
		mWorkoutFatherSet.getElements().add(new Set());
	}
	
	public void removeElement()
	{
		// TODO Complete
	}
	
	public void addElement()
	{
		// TODO Complete
	}
	
	// XXX Make sure this is necessary - This getter is for the view to use the data from the presenter to create the UI wigets & such.
	// If the presenter should be the one setting up wigets as well as taking care of logic this method is not needed and methods
	// Such as CreateNewWorkout and LoadWorkoutData in PreviewActivity are to be removed! 
	public Set getSet()
	{
		return mWorkoutFatherSet;
	}
}
