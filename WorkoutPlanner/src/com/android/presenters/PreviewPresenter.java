package com.android.presenters;

import android.widget.Toast;
import com.android.dal.XMLWorkoutReader;
import com.android.element.Set;
import com.android.views.PreviewActivity;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public class PreviewPresenter extends APresenter {

	private PreviewActivity m_currentView;
	private Set m_workoutFatherSet = new Set();
	
	public PreviewPresenter(PreviewActivity context)
	{
		// A presenter must hold a reference to its view
		m_currentView = context;
	}
	
	public void UpdateToolbar()
	{
		// TODO Complete
	}
	
	public void Save()
	{
		// TODO Complete
	}
	
	public void UpdateElementData()
	{
		// TODO Complete
	}
	
	public void LoadWorkoutData(String workoutName)
	{
		// READING FILE
		try {
			m_workoutFatherSet = XMLWorkoutReader.ReadFile(m_currentView.openFileInput(workoutName + ".xml"));
		} catch (XmlPullParserException e) {
			Toast.makeText(m_currentView, "Error reading file. Writing to log...", Toast.LENGTH_SHORT).show();
			// TODO Write to log
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(m_currentView, "Error reading file. Writing to log...", Toast.LENGTH_SHORT).show();
			// TODO Write to log
			e.printStackTrace();
		} catch (NumberFormatException e) {
			Toast.makeText(m_currentView, "Error reading file. Writing to log...", Toast.LENGTH_SHORT).show();
			// TODO Write to log
			e.printStackTrace();
		}
	}
	
	public void CreateNewWorkout()
	{
		// Creating the first set for the user to work on
		m_workoutFatherSet.getElements().add(new Set());
	}
	
	public void RemoveElement()
	{
		// TODO Complete
	}
	
	public void AddElement()
	{
		// TODO Complete
	}
	
	// XXX Make sure this is necessary - This getter is for the view to use the data from the presenter to create the UI wigets & such.
	// If the presenter should be the one setting up wigets as well as taking care of logic this method is not needed and methods
	// Such as CreateNewWorkout and LoadWorkoutData in PreviewActivity are to be removed! 
	public Set getSet()
	{
		return m_workoutFatherSet;
	}
}
