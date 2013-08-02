package com.android.views;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import com.android.element.AElement;
import com.android.element.Set;
import com.android.global.Consts;
import com.android.presenters.PreviewPresenter;
import java.util.ArrayList;

public class PreviewActivity extends Activity implements IFocusable {

	PreviewPresenter presenter;
	PreviewSetAdapter adapter;
	ListView itemsList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);
		
		// Setting up basic activity requirements
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// Creating the presenter for this view
		presenter = new PreviewPresenter(this);

		// Get selected workout name from intent
		String workoutName = getIntent().getExtras().getString(Consts.SELECT_WORKOUT_TAG);

		// Load workout if exists, else making a new workout
		if (workoutName == null) {
			presenter.CreateNewWorkout();
		}
		else {
			presenter.LoadWorkoutData(workoutName);
		}
		
		// Set sets into list and send 'em
		ArrayList<Set> sets = new ArrayList<Set>();
		
		/*
		 * conversion is always legal because the father set may only include
		 * sets
		 */
		for (AElement currSet : presenter.getSet().getElements()) {
			if (currSet instanceof Set) {
				sets.add((Set) currSet);
			}
			else {
				// If an element of the father set is not a set, inform user and
				// return to previous activity
				Toast.makeText(this, "File is corrupted or is otherwise unreadable.", Toast.LENGTH_SHORT).show();
				onBackPressed();
			}
		}
		
		// Set the adapter in the list view. The adapter should update the listview automatically with it's getView method
		adapter = new PreviewSetAdapter(this, R.layout.gays_element_set, sets);
		itemsList = (ListView) findViewById(R.id.items_list);
		itemsList.setAdapter(adapter);
	}

	@Override
	public void GetFocus() {
		// TODO Complete

	}

	private void SetFocus() {
		// TODO Complete

	}

	private void CreateNewWorkout() {

		presenter.CreateNewWorkout();
		// TODO Complete - load data to the screen
	}

	private void LoadWorkout(String workoutName) {

		presenter.LoadWorkoutData(workoutName);
		// TODO Complete - load data to the screen
	}
}
