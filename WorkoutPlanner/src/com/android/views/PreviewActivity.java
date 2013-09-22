package com.android.views;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.android.element.AElement;
import com.android.element.Exercise;
import com.android.element.RepetitionExercise;
import com.android.global.Consts;
import com.android.global.Consts.resultActivities;
import com.android.global.Globals;
import com.android.presenters.PreviewPresenter;
import com.android.views.NumericDialog.onNumberEnteredListener;
import com.android.views.SaveDialog.onSaveCompletedListener;
import com.android.views.SelectPositionDialog.onPositionSelectedListener;
import com.android.views.SetNameDialog.onNameEnteredListener;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

public class PreviewActivity extends ListActivity implements onNumberEnteredListener, onNameEnteredListener, onPositionSelectedListener, onSaveCompletedListener {

	PreviewPresenter mPresenter;
	PreviewExerciseAdapter mAdapter;
	MenuItem mUndoIcon;
	DragSortListView mItemsList;
	DragSortController mController;
	ArrayAdapter<String> mSoundsAdapter;

	public static boolean sIsModified = false;
	public static boolean sEditListMode = false;

	// Keys
	private static final String KEY_SERIALIZABLE_SET = "SerializableSet";
	private static final String KEY_SETS_LIST_SIZE = "SetsListSize";

	// private static final String TAG = "WorkoutPlanner_PreviewActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);

		// Force orientation screen.
		// TODO Technically this solves some unfixed bugs but this is not the
		// solution
		// This is more for saving time when it accidentally flips.. Should
		// cancel that or keep?
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Set drag stuff data
		DragSortListView lv = (DragSortListView) getListView();
		lv.setDropListener(onDrop);
		lv.setRemoveListener(onRemove);
		mController = buildController(lv);

		lv.setFloatViewManager(mController);
		lv.setOnTouchListener(mController);
		lv.setDragEnabled(true);

		// Set up basic activity requirements
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// Create the presenter for this view
		mPresenter = new PreviewPresenter(this);
		
		// If creating for the first time, load or create workout
		if (savedInstanceState == null) {
			// Get selected workout name from intent
			String workoutName = getString(R.string.new_workout);

			// Load workout if exists, else making a new workout
			if (getIntent().getExtras() == null) {
				mPresenter.createNewWorkout();
			}
			else {
				workoutName = getIntent().getExtras().getString(Consts.SELECT_WORKOUT_TAG);
				mPresenter.loadWorkoutData(workoutName);
			}

			// Set activity title and remove icon
			setTitle(workoutName);
			getActionBar().setDisplayShowHomeEnabled(false);

			// Set sort false
			sEditListMode = false;

			/*
			 * conversion is always legal because the father set may only
			 * include sets
			 */
			for (AElement currSet : mPresenter.getFatherSet().getElements()) {
				// Validate data. If one element is not set, exit
				if (!(currSet instanceof Exercise)) {
					// If an element of the father set is not a set, inform user
					// and
					// return to previous activity
					Toast.makeText(this, "File is corrupted or is otherwise unreadable.", Toast.LENGTH_SHORT).show();
					onBackPressed();
				}
			}
		}
		else {
			// Read sets from the saved state via serializable
			int length = savedInstanceState.getInt(KEY_SETS_LIST_SIZE);

			for (int i = 0; i < length; i++) {
				mPresenter.getFatherSet().getElements().add((Exercise) savedInstanceState.getSerializable(KEY_SERIALIZABLE_SET + i));
			}
		}

		// Set the adapter in the list view. The adapter should update the
		// listview automatically with it's getView method
		mAdapter = new PreviewExerciseAdapter(this, R.layout.preview_element_exercise, Globals.sFatherExercise.getElements());
		mItemsList = (DragSortListView) findViewById(android.R.id.list);

		mItemsList.setAdapter(mAdapter);
	}

	// Drag list view data
	@Override
	public DragSortListView getListView() {
		return (DragSortListView) super.getListView();
	}

	// Drag list view data
	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				Exercise item = (Exercise) mAdapter.getItem(from);
				mAdapter.remove(item);
				mAdapter.insert(item, to);

				// Value modified, set flag to true
				sIsModified = true;

				// Reset Ids and re-lock/unlock clickability
				mAdapter.resetIds();
			}
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			// Save to undo before removing
			PreviewExerciseAdapter.sUndoSet = new Exercise((Exercise) mAdapter.getItem(which));
			
			setUndoMode(true);

			// Value modified, set flag to true
			sIsModified = true;

			mAdapter.remove(mAdapter.getItem(which));

			// Reset Ids and re-lock/unlock clickability
			mAdapter.resetIds();
		}
	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save sets onto the bundle via serializable
		for (int i = 0; i < mPresenter.getFatherSet().getElements().size(); i++) {
			outState.putSerializable(KEY_SERIALIZABLE_SET + i, mPresenter.getFatherSet().getElements().get(i));
		}

		outState.putInt(KEY_SETS_LIST_SIZE, mPresenter.getFatherSet().getElements().size());
	}

	/**
	 * Called in onCreateView. Override this to provide a custom
	 * DragSortController.
	 */
	public DragSortController buildController(DragSortListView dslv) {
		DragSortController controller = new DragSortController(dslv);
		controller.setDragHandleId(R.id.drag_handle);
		controller.setRemoveEnabled(false);
		controller.setSortEnabled(true);
		controller.setDragInitMode(DragSortController.ON_DOWN);
		controller.setRemoveMode(DragSortController.FLING_REMOVE);

		return controller;
	}

	@Override
	public void onNumericInputEntered(AElement element) {
		// Check if a set was edited. If so, assuming it was the repetitions
		// value!
		if (element instanceof Exercise) {

			// Check for repetition exercises
			for (AElement curr : ((Exercise) element).getElements()) {
				if (curr instanceof RepetitionExercise) {
					// Add/Remove rep/weight values so the lists' size would
					// match set repetition value
					int length = ((RepetitionExercise) curr).getReps().size() - ((Exercise) element).getSets();
					int listLength = ((RepetitionExercise) curr).getReps().size();
					boolean isListSizeLarger = length >= 0;

					length = Math.abs(length);

					for (int i = 0; i < length; i++) {
						// Add or remove values from the list according to the
						// flag set earlier
						if (isListSizeLarger) {
							// Removing because the list size is larger than the
							// repetition value of the set
							((RepetitionExercise) curr).getReps().remove(listLength - i - 1);
							((RepetitionExercise) curr).getWeights().remove(listLength - i - 1);
							((RepetitionExercise) curr).getEndlessSets().remove(listLength - i - 1);
						}
						else {
							// Adding values to the list because the list size
							// is smaller than the repetition value of the set
							((RepetitionExercise) curr).getReps().add(0);
							((RepetitionExercise) curr).getWeights().add(0.0);
							((RepetitionExercise) curr).getEndlessSets().add(false);
						}
					}
				}
			}
		}

		// Value modified, set flag to true
		sIsModified = true;

		// Update sets
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onNameInputEntered(AElement element, String nameValue) {
		element.setName(nameValue);

		// If the recently modified set was the father set, it's the "Save As"
		// function! So save
		if (mPresenter.getFatherSet().equals(element)) {
			mPresenter.save();
		}

		// Value modified, set flag to true
		sIsModified = true;

		// Update sets
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.preview_menu, menu);

		// Save menu item
		mUndoIcon = menu.findItem(R.id.menu_item_undo);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_item_add_set:
			// Minimize sets for look comfort
			mAdapter.minimizeAll();

			// Add a new set
			Exercise newSet = new Exercise(mAdapter.getCount());
			mAdapter.add(newSet);

			// Scroll to new set
			mItemsList.smoothScrollToPositionFromTop(newSet.getId(), 0);

			// Fill flags at least up to the new set and then turn the new set's
			// flag to true
			mAdapter.fillExpandFlags(newSet.getId());
			PreviewExerciseAdapter.sExpandedViews.set(newSet.getId(), Boolean.valueOf(true));
			
			// Value modified, set flag to true
			sIsModified = true;

			// Notify
			mAdapter.notifyDataSetChanged();

			break;
		case R.id.menu_item_save:
			mPresenter.save();

			// After saving, reset flag.
			sIsModified = false;

			break;
		case R.id.menu_item_save_as:
			// Set new name, then save
			mPresenter.saveAs();

			break;
		case R.id.menu_item_undo:
			if (PreviewExerciseAdapter.sUndoSet != null) {
				// Change undo icon to enable
				mUndoIcon.setIcon(R.drawable.ic_content_undo);

				// Inserting and refreshing
				mAdapter.insert(PreviewExerciseAdapter.sUndoSet, PreviewExerciseAdapter.sUndoSet.getId());

				mAdapter.notifyDataSetChanged();

				// Reset undo set
				PreviewExerciseAdapter.sUndoSet = null;
				
				// Value modified, set flag to true
				sIsModified = true;

				// Set undo to disable
				setUndoMode(false);
			}
			break;
		case R.id.menu_item_sort:
			// Show handlers for edit mode. Then dragging and removing items is
			// allowed, but expanding them is not
			sEditListMode = !sEditListMode;
			mController.setRemoveEnabled(sEditListMode);

			// Resetting all expand flags
			for (int i = 0; i < PreviewExerciseAdapter.sExpandedViews.size(); i++) {
				PreviewExerciseAdapter.sExpandedViews.set(i, false);
			}

			// Also minimize items if entering edit mode
			if (sEditListMode) {
				mAdapter.minimizeAll();
			}

			// Finally update views
			mAdapter.notifyDataSetChanged();

			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public void setUndoMode(boolean enabled) {

		// Set icon to enabled or disabled look
		if (enabled) {
			mUndoIcon.setIcon(R.drawable.ic_content_undo);
		}
		else {
			mUndoIcon.setIcon(R.drawable.ic_content_undo_dark);
		}

		// Set enable/disable
		mUndoIcon.setCheckable(enabled);
	}

	@Override
	public void onPositionSelected(Exercise parent, int from, int to) {
		// Get selected child Element from parent, move it to its new position
		AElement item = (AElement) parent.getElements().get(from);
		parent.getElements().remove(item);
		parent.getElements().add(to, item);

		// Value modified, set flag to true
		sIsModified = true;

		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed() {
		if (sIsModified) {
			// Ask for confirmation.. Will not save
			// Set result and exit if confirmed
			ConfirmDialog.newInstance(getString(R.string.exit_title), getString(R.string.exit_context) + " " + getTitle() + "?", "exitActivity", PreviewActivity.class).show(getFragmentManager(), null);
		}
		else {
			// Simply exit, no questions asked
			exitActivity();
		}
	}

	public void exitActivity() {
		// Set result with no intent, this activity was canceled
		setResult(Activity.RESULT_CANCELED, null);

		// Leaving activity
		finish();
	}

	@Override
	public void finish() {
		// Reset static variables
		sIsModified = false;
		sEditListMode = false;
		PreviewExerciseAdapter.sExpandedViews.clear();
		PreviewExerciseAdapter.sUndoSet = null;

		super.finish();
	}

	@Override
	public void onSaveCompleted(String workoutName) {
		setTitle(workoutName);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// Check for activity
		switch (resultActivities.values()[requestCode]) {
		case ELEMENTS_LIST:
			// If data is valid
			if ((data != null) && (resultCode == RESULT_OK)) {
				Bundle b = data.getExtras();
				Exercise modifiedSet = (Exercise) b.getSerializable(Consts.CURRENT_EXERCISE);

				// Check for difference
				if (!modifiedSet.equals(mAdapter.getSets().get(modifiedSet.getId()))) {
					// Value modified, set flag to true
					sIsModified = true;
				}

				// Set modified Exercise in its place
				mAdapter.getSets().set(modifiedSet.getId(), modifiedSet);

				mAdapter.notifyDataSetChanged();
			}

			break;
		default:
			break;
		}
	}
}
