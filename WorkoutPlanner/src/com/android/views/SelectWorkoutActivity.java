package com.android.views;

import com.android.global.Consts.resultActivities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.android.element.Set;
import com.android.global.Consts;
import com.android.global.Globals;
import com.android.presenters.SelectWorkoutPresenter;
import java.util.ArrayList;
import java.util.List;

public class SelectWorkoutActivity extends ListActivity {

	ListView mItemsList;
	SelectWorkoutAdapter mAdapter;
	ArrayList<Workout> mWorkouts;

	public SelectWorkoutPresenter mPresenter;
	public String mSelectedFile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_workout);

		// Set up list and title
		mItemsList = (ListView) findViewById(android.R.id.list);
		setTitle(R.string.select_workout_title);

		// Set up presenter and load workouts
		mPresenter = new SelectWorkoutPresenter(this);
		mWorkouts = new ArrayList<Workout>();
		loadWorkouts();

		// Set up adapter
		mAdapter = new SelectWorkoutAdapter(this, R.layout.workout_layout, mWorkouts);
		mItemsList.setAdapter(mAdapter);
	}

	public void loadWorkouts() {
		// Loading list of workouts
		String[] workoutNames = mPresenter.getWorkouts();

		mWorkouts.clear();
		
		for (String name : workoutNames) {
			mWorkouts.add(new Workout(name));
		}
	}

	public void deleteWorkout() {
		// Delete file and update list
		mPresenter.deleteFile();
		loadWorkouts();
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.select_workout_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_create_new_workout:
			// Start empty preview activity
			Globals.sFatherSet = new Set(-1);
			startActivityForResult(new Intent(this, PreviewActivity.class), resultActivities.PREVIEW_ACTIVITY.ordinal());
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Refresh workouts after result
		loadWorkouts();
		mItemsList.setEnabled(true);
		mAdapter.notifyDataSetChanged();
	}

	class SelectWorkoutAdapter extends ArrayAdapter<Workout> {
		SelectWorkoutActivity mContext;

		public SelectWorkoutAdapter(Context context, int textViewResourceId, List<Workout> objects) {
			super(context, textViewResourceId, objects);
			mContext = (SelectWorkoutActivity) context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// Inflate
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.workout_layout, parent, false);
			}

			// Populate
			final int index = position;
			TextView workoutName = ((TextView) convertView.findViewById(R.id.workoutName));

			workoutName.setText(((Workout) getItem(position)).mWorkoutName);
			workoutName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getApplicationContext(), PlayActivity.class);
					
					// Put the name of the selected workout in the intent
					i.putExtra(Consts.SELECT_WORKOUT_TAG, ((Workout) getItem(index)).mWorkoutName);
					startActivity(i);
				}
			});

			((ImageButton) convertView.findViewById(R.id.editWorkout)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, PreviewActivity.class);

					// Setting the name of the selected workout in the
					// intent's bundle
					intent.putExtra(Consts.SELECT_WORKOUT_TAG, ((Workout) getItem(index)).mWorkoutName);

					// Disable all listeners and start preview activity
					mContext.mItemsList.setEnabled(false);
					mContext.startActivityForResult(intent, resultActivities.PREVIEW_ACTIVITY.ordinal());
				}
			});

			((ImageButton) convertView.findViewById(R.id.deleteWorkout)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Setting selected file name and asking for confirmation
					mContext.mSelectedFile = ((Workout) getItem(index)).mWorkoutName;
					ConfirmDialog.newInstance(mContext.getString(R.string.delete_workout_title), mContext.getString(R.string.delete_workout_context) + " " + mContext.mSelectedFile + "?", "deleteWorkout", SelectWorkoutActivity.class).show(mContext.getFragmentManager(), null);
				}
			});

			return convertView;
		}
	}

	class Workout {
		public String mWorkoutName;

		public Workout(String name) {
			mWorkoutName = name;
		}
	}
}
