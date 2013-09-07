package com.android.views;

import android.widget.ArrayAdapter;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import com.android.element.AElement;
import com.android.element.RepetitionExercise;
import com.android.element.Rest;
import com.android.element.Exercise;
import com.android.element.TimeExercise;
import com.android.global.Consts;
import com.android.presenters.ElementsListPresenter;
import com.android.views.AddElementDialog.onElementSelectedListener;
import com.android.views.NumericDialog.onNumberEnteredListener;
import com.android.views.SetNameDialog.onNameEnteredListener;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import java.util.ArrayList;

public class ElementsListActivity extends ListActivity implements onElementSelectedListener, onNameEnteredListener, onNumberEnteredListener {

	ElementsListPresenter mPresenter;
	ElementsListAdapter mAdapter;
	MenuItem mUndoIcon;
	DragSortListView mItemsList;
	DragSortController mController;
	Exercise mFather;
	LinearLayout mHeader;
	
	public ArrayAdapter<String> mSoundsAdapter;
	public static boolean sIsModified = false;
	public static boolean sEditListMode;

	// Keys
	private static final String KEY_SERIALIZABLE_ELEMENT = "SerializableElement";
	private static final String KEY_ELEMENTS_LIST_SIZE = "ElementsListSize";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_elements_list);

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
		
		// Initializing presenter
		mPresenter = new ElementsListPresenter(this);

		if (savedInstanceState == null) {
			// Get father set from intent
			mFather = (Exercise) getIntent().getSerializableExtra(Consts.CURRENT_EXERCISE);
		}
		else {
			// Read sets from the saved state via serializable
			int length = savedInstanceState.getInt(KEY_ELEMENTS_LIST_SIZE);

			for (int i = 0; i < length; i++) {
				mFather.getElements().add((Exercise) savedInstanceState.getSerializable(KEY_SERIALIZABLE_ELEMENT + i));
			}
		}

		// Set activity title and remove icon
		setTitle(R.string.elements_list_activity_label);
		getActionBar().setDisplayShowHomeEnabled(false);

		// Set sort false
		sEditListMode = false;
		
		// Set sounds into adapter
		mSoundsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		mSoundsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSoundsAdapter.addAll(mPresenter.loadSounds());

		// Set the adapter in the list view. The adapter should update the
		// listview automatically with it's getView method
		mAdapter = new ElementsListAdapter(this, mFather, elementsToElementViews());
		mItemsList = (DragSortListView) findViewById(android.R.id.list);

		// Create header and place it
		createHeader();
		mItemsList.addHeaderView(mHeader);
		mItemsList.setAdapter(mAdapter);
	}

	private void createHeader() {
		mHeader = new LinearLayout(this);

		// Create views for header
		TextView setName = new TextView(this);
		setName.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setName.setTextAppearance(this, R.style.small_bold);
		setName.setId(R.id.header_name);

		Space space = new Space(this);
		space.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f));

		TextView setReps = new TextView(this);
		setReps.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setReps.setTextAppearance(this, R.style.small_bold);
		setReps.setId(R.id.header_reps);

		// Set listeners
		setReps.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NumericDialog instance = NumericDialog.newInstance(mFather, NumericDialog.INTEGER_MODE, Consts.SET_SETS_METHOD_NAME);
				instance.show(getFragmentManager(), null);
			}
		});

		setName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SetNameDialog instance = SetNameDialog.newInstance(mFather);
				instance.show(getFragmentManager(), null);
			}
		});

		mHeader.addView(setName);
		mHeader.addView(space);
		mHeader.addView(setReps);

		// Update header values
		updateHeader();
	}

	private void updateHeader() {
		// Update header values
		TextView name = ((TextView) mHeader.findViewById(R.id.header_name));
		
		if (mFather.getName().equals("")) {
			// Make sure there is a name. If none, put default: Exercise - index
			// value (+1 for readability)
			name.setText(getString(R.string.default_exercise_name) + " " + Integer.toString(mFather.getId() + 1));
		}
		else {
			name.setText(mFather.getName());
		}
		
		((TextView) mHeader.findViewById(R.id.header_reps)).setText(Integer.toString(mFather.getSets()));
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

	// Drag list view data
	@Override
	public DragSortListView getListView() {
		return (DragSortListView) super.getListView();
	}

	private ArrayList<AElementView> elementsToElementViews() {
		ArrayList<AElementView> result = new ArrayList<AElementView>();

		// Creating list of element views
		for (AElement element : mFather.getElements()) {
			result.add(createViewForElement(element));
		}

		return result;
	}

	private AElementView createViewForElement(AElement element) {
		AElementView result = null;

		if (element instanceof RepetitionExercise) {
			result = new RepetitonExerciseView((RepetitionExercise) element, this);
		}
		else if (element instanceof TimeExercise) {
			result = new TimeExerciseView((TimeExercise) element, this);
		}
		else if (element instanceof Rest) {
			result = new RestView((Rest) element, this);
		}

		return result;
	}

	// Drag list view data
	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				AElementView item = (AElementView) mAdapter.getItem(from);
				mAdapter.remove(item);
				mAdapter.insert(item, to);

				// Moving data in father as well
				AElement element = mFather.getElements().get(from);
				mFather.getElements().remove(from);
				mFather.getElements().add(to, element);

				// Value modified, set flag to true
				sIsModified = true;

				// Reset Ids
				mAdapter.resetIds();
			}
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			// Save to undo before removing
			if (mAdapter.getItem(which) instanceof TimeExerciseView) {
				ElementsListAdapter.sUndoElementView = new TimeExerciseView((TimeExerciseView)mAdapter.getItem(which));
			} else if (mAdapter.getItem(which) instanceof RepetitonExerciseView) {
				ElementsListAdapter.sUndoElementView = new RepetitonExerciseView((RepetitonExerciseView)mAdapter.getItem(which));
			} else if (mAdapter.getItem(which) instanceof RestView) {
				ElementsListAdapter.sUndoElementView = new RestView((RestView)mAdapter.getItem(which));
			}

			// Value modified, set flag to true
			sIsModified = true;
			
			setUndoMode(true);

			mAdapter.remove(mAdapter.getItem(which));

			// Removing data in father as well
			mFather.getElements().remove(mFather.getElements().get(which));

			// Reset Ids
			mAdapter.resetIds();
		}
	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save sets onto the bundle via serializable
		for (int i = 0; i < mFather.getElements().size(); i++) {
			outState.putSerializable(KEY_SERIALIZABLE_ELEMENT + i, mFather.getElements().get(i));
		}

		outState.putInt(KEY_ELEMENTS_LIST_SIZE, mFather.getElements().size());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.elements_list_menu, menu);

		// Save menu item
		mUndoIcon = menu.findItem(R.id.menu_item_undo);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_item_add_element:
			AddElementDialog instance = AddElementDialog.newInstance(mFather);
			instance.show(getFragmentManager(), null);

			break;
		case R.id.menu_item_undo:
			if (ElementsListAdapter.sUndoElementView != null) {
				// Change undo icon to enable
				mUndoIcon.setIcon(R.drawable.ic_content_undo);

				// Inserting and refreshing
				mAdapter.insert(ElementsListAdapter.sUndoElementView, ElementsListAdapter.sUndoElementView.mElement.getId());
				
				// Re adding to the father
				mFather.getElements().add(ElementsListAdapter.sUndoElementView.mElement.getId(), ElementsListAdapter.sUndoElementView.mElement);

				mAdapter.notifyDataSetChanged();

				// Value modified, set flag to true
				sIsModified = true;

				// Reset undo set
				ElementsListAdapter.sUndoElementView = null;

				// Set undo to disable
				setUndoMode(false);
			}
			break;
		case R.id.menu_item_sort:
			// Show handlers for edit mode. Then dragging and removing items is
			// allowed, but expanding them is not
			sEditListMode = !sEditListMode;
			mController.setRemoveEnabled(sEditListMode);

			// Finally update views
			mAdapter.notifyDataSetChanged();

			break;
		case R.id.menu_item_save:
			// Send result in intent
			setResult(RESULT_OK, new Intent().putExtra(Consts.CURRENT_EXERCISE, mFather));

			// finishes activity
			finish();
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
	public void onElementSelectEntered(AElement newElement) {
		// Create & add a new view according to the element added
		mAdapter.add(createViewForElement(newElement));

		// Scroll to new set
		mItemsList.smoothScrollToPositionFromTop(newElement.getId(), 0);

		// Value modified, set flag to true
		sIsModified = true;

		// Update sets
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onNumericInputEntered(AElement element) {
		// If updated element is the father
		if (element.equals(mFather)) {
			// Update the header
			updateHeader();

			// Since the father was updated, the modified field was repetitions.
			// Check for repetition exercises to update
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

		// If updated element is the father, update the header
		if (element.equals(mFather)) {
			updateHeader();
		}

		// Value modified, set flag to true
		sIsModified = true;

		// Update values
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed() {

		if (sIsModified) {
			// Ask for confirmation.. Will not save
			// Set result and exit if confirmed
			ConfirmDialog.newInstance(getString(R.string.exit_title), getString(R.string.exit_context) + " " + mFather.getName() + "?", "exitActivity", ElementsListActivity.class).show(getFragmentManager(), null);
		}
		else {
			// Simply exit, no questions asked
			exitActivity();
		}
	}

	@Override
	public void finish() {
		// Reset static variables
		sIsModified = false;
		sEditListMode = false;
		ElementsListAdapter.sUndoElementView = null; 

		super.finish();
	}

	public void exitActivity() {
		// Set result with no intent, this activity was canceled
		setResult(Activity.RESULT_CANCELED, null);

		// Leaving activity
		finish();
	}
}
