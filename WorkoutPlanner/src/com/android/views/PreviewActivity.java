package com.android.views;

import com.android.views.SaveDialog.onSaveCompletedListener;

import android.app.ListActivity;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.android.element.AElement;
import com.android.element.RepetitionExercise;
import com.android.element.Set;
import com.android.global.Consts;
import com.android.global.Globals;
import com.android.presenters.PreviewPresenter;
import com.android.views.AddElementDialog.onElementSelectedListener;
import com.android.views.NumericDialog.onNumberEnteredListener;
import com.android.views.SelectPositionDialog.onPositionSelectedListener;
import com.android.views.SetNameDialog.onNameEnteredListener;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

/*public class PreviewActivity extends Activity */
public class PreviewActivity extends ListActivity implements onNumberEnteredListener, onNameEnteredListener, onElementSelectedListener, onPositionSelectedListener, onSaveCompletedListener {

	PreviewPresenter mPresenter;
	PreviewSetAdapter mAdapter;
	MenuItem mUndoIcon;
	DragSortListView mItemsList;
	DragSortController mController;
	
	public static boolean sEditListMode = false;

	// Keys
	private static final String KEY_SERIALIZABLE_SET = "SerializableSet";
	private static final String KEY_SETS_LIST_SIZE = "SetsListSize";

	private static final String TAG = "WorkoutPlanner_PreviewActivity";

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

		// Setting drag stuff data
		DragSortListView lv = (DragSortListView) getListView();
		lv.setDropListener(onDrop);
		lv.setRemoveListener(onRemove);
		mController = buildController(lv);
		
		
		lv.setFloatViewManager(mController);
		lv.setOnTouchListener(mController);
		lv.setDragEnabled(true);

		// Setting up basic activity requirements
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// Creating the presenter for this view
		mPresenter = new PreviewPresenter(this);

		// If creating for the first time, load or create workout
		if (savedInstanceState == null) {
			// Get selected workout name from intent
			String workoutName = getIntent().getExtras().getString(Consts.SELECT_WORKOUT_TAG);

			// Load workout if exists, else making a new workout
			if (workoutName == null) {
				mPresenter.createNewWorkout();
			}
			else {
				mPresenter.loadWorkoutData(workoutName);
			}
			
			// Set activity title and remove icon
			setTitle(workoutName);
			getActionBar().setDisplayShowHomeEnabled(false);

			/*
			 * conversion is always legal because the father set may only
			 * include sets
			 */
			for (AElement currSet : mPresenter.getFatherSet().getElements()) {
				// Validate data. If one element is not set, exit
				if (!(currSet instanceof Set)) {
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
				mPresenter.getFatherSet().getElements().add((Set) savedInstanceState.getSerializable(KEY_SERIALIZABLE_SET + i));
			}
		}

		// Set the adapter in the list view. The adapter should update the
		// listview automatically with it's getView method
		mAdapter = new PreviewSetAdapter(this, R.layout.gays_element_set, Globals.sFatherSet.getElements());
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
				Set item = (Set) mAdapter.getItem(from);
				mAdapter.remove(item);
				mAdapter.insert(item, to);

				// Reset Ids and re-lock/unlock clickability
				mAdapter.resetIds();
				mAdapter.lockExdends(!sEditListMode);
			}
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			// Save to undo before removing
			PreviewSetAdapter.sUndoSet = new Set((Set) mAdapter.getItem(which));
			
			// FIXME NOT WORKING...
			//mAdapter.setUpdateViewData(PreviewSetAdapter.sHolders.get(PreviewSetAdapter.sHolders.indexOf(mAdapter.getSets().get(which))));

			mAdapter.remove(mAdapter.getItem(which));
			
			// Reset Ids and re-lock/unlock clickability
			mAdapter.resetIds();
			mAdapter.lockExdends(!sEditListMode);
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
	public void onNumericInputEntered(PreviewItemHolder holder, AElement element) {
		// Check if a set was edited. If so, assuming it was the repetitions
		// value!
		if (element instanceof Set) {
			// Turn endless to false if not already false
			((Set) element).setEndless(false);

			// Setting the data for updating the view
			//mAdapter.setUpdateViewData(holder); // XXX Remove if not used. Probably won't be

			// Check for repetition exercises
			for (AElement curr : ((Set) element).getElements()) {
				if (curr instanceof RepetitionExercise) {
					// Add/Remove rep/weight values so the lists' size would
					// match set repetition value
					int length = ((RepetitionExercise) curr).getReps().size() - ((Set) element).getRepetitions();
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
						}
						else {
							// Adding values to the list because the list size
							// is smaller than the repetition value of the set
							((RepetitionExercise) curr).getReps().add(0);
							((RepetitionExercise) curr).getWeights().add(0.0);
						}
					}
				}
			}
		}

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

		// Update sets
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onElementSelectEntered(PreviewItemHolder holder) {
		// Setting data needed for updating view
		//mAdapter.setUpdateViewData(holder); // XXX Remove if not used. Probably won't be

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
			mAdapter.add(new Set(mAdapter.getCount()));
			mAdapter.notifyDataSetChanged();

			break;
		case R.id.menu_item_save:
			mPresenter.save();

			break;
		case R.id.menu_item_save_as:
			// Set new name, then save
			mPresenter.saveAs();

			break;
		case R.id.menu_item_undo:
			if (PreviewSetAdapter.sUndoSet != null) {				
				// Change undo icon to enable
				mUndoIcon.setIcon(R.drawable.ic_content_undo);

				// Inserting and refreshing
				mAdapter.insert(PreviewSetAdapter.sUndoSet, PreviewSetAdapter.sUndoSet.getId());

				mAdapter.notifyDataSetChanged();

				// Reset undo set
				PreviewSetAdapter.sUndoSet = null;

				// Set undo to disable
				setUndoMode(false);
			}
			break;
		case R.id.menu_item_sort:
			// Show handlers for edit mode. Then dragging and removing items is allowed, but expanding them is not
			sEditListMode = !sEditListMode;
			mAdapter.showHideSortHandler((sEditListMode) ? View.VISIBLE : View.GONE);
			mController.setRemoveEnabled(sEditListMode);
			mAdapter.lockExdends(!sEditListMode);
			
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

	private void showMenu(View v) {
		PopupMenu popupMenu = new PopupMenu(this, v);
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case (R.id.popup_menu_delete):
					break;
				case (R.id.popup_menu_move):
				default:
					break;
				}
				return true;
			}
		});
		popupMenu.inflate(R.menu.preview_popup_menu);

		popupMenu.show();
	}

	@Override
	public void onPositionSelected(Set parent, int from, int to) {
		// Get selected child Element from parent, move it to its new position
		AElement item = (AElement) parent.getElements().get(from);
		parent.getElements().remove(item);
		parent.getElements().add(to, item);
	}

	@Override
	public void onBackPressed() {
		// Warning for the user, could use a save flag in the future
		// TODO Call a dialog that would ask the user if he would like to save
		// first. If not, exit.
		// If yes, check for file name. If exists, overwrite, if not ask for
		// name input. (call save dialog)
		super.onBackPressed();
	}

	@Override
	public void onSaveCompleted(String workoutName) {
		setTitle(workoutName);
	}
}
