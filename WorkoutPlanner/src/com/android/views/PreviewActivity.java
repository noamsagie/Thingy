package com.android.views;

import android.app.Activity;

import android.widget.ListView;

import android.app.ListActivity;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.Toast;
import com.android.element.AElement;
import com.android.element.Rest;
import com.android.element.Set;
import com.android.element.TimeExercise;
import com.android.global.Consts;
import com.android.presenters.PreviewPresenter;
import com.android.views.NumericDialog.onNumberEnteredListener;
import com.android.views.SetNameDialog.onNameEnteredListener;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import java.util.ArrayList;

/*public class PreviewActivity extends ListActivity implements IFocusable, onNumberEnteredListener, onNameEnteredListener { */
public class PreviewActivity extends Activity implements IFocusable, onNumberEnteredListener, onNameEnteredListener {

	PreviewPresenter presenter;
	PreviewSetAdapter adapter;
	/*DragSortListView itemsList;*/

	// TEMP
	ListView itemsList;
	// TEMP
	
	ArrayList<Set> mSets;

	// Keys
	private static final String KEY_SERIALIZABLE_SET = "SerializableSet";
	private static final String KEY_SETS_LIST_SIZE = "SetsListSize";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*setContentView(R.layout.activity_preview);*/
		
		// Setting drag stuff data
		/*DragSortListView lv = (DragSortListView) getListView();
		lv.setDropListener(onDrop);
		lv.setRemoveListener(onRemove);
        DragSortController controller = buildController(lv);
        lv.setFloatViewManager(controller);
        lv.setOnTouchListener(controller);
        lv.setDragEnabled(true);*/

		// Setting up basic activity requirements
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// If saved state is null, create all that needs to be initialized
		if (savedInstanceState == null) {
			// Creating the presenter for this view
			presenter = new PreviewPresenter(this);

			// Get selected workout name from intent
			String workoutName = getIntent().getExtras().getString(Consts.SELECT_WORKOUT_TAG);

			// Load workout if exists, else making a new workout
			if (workoutName == null) {
				presenter.createNewWorkout();
			}
			else {
				presenter.loadWorkoutData(workoutName);
			}

			// Set sets into list and send 'em
			mSets = new ArrayList<Set>();

			/*
			 * conversion is always legal because the father set may only
			 * include sets
			 */
			for (AElement currSet : presenter.getSet().getElements()) {
				if (currSet instanceof Set) {
					mSets.add((Set) currSet);
				}
				else {
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
			mSets = new ArrayList<Set>();
			int length = savedInstanceState.getInt(KEY_SETS_LIST_SIZE);

			for (int i = 0; i < length; i++) {
				mSets.add((Set) savedInstanceState.getSerializable(KEY_SERIALIZABLE_SET + i));
			}
		}

		// Set the adapter in the list view. The adapter should update the
		// listview automatically with it's getView method
		adapter = new PreviewSetAdapter(this, R.layout.gays_element_set, mSets);
		/*itemsList = (DragSortListView) findViewById(android.R.id.list); */
		
		// TEMP
		itemsList = new ListView(this);
		itemsList.setBackgroundResource(R.color.grey);
		itemsList.setDividerHeight(8);
		this.setContentView(itemsList);
		// TEMP
		
		itemsList.setAdapter(adapter);
	}
	
	
	// Drag list view data
/*    @Override
    public DragSortListView getListView() {
        return (DragSortListView) super.getListView();
    } */
	
	// Drag list view data
    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    if (from != to) {
                        Set item = adapter.getItem(from);
                        adapter.remove(item);
                        adapter.insert(item, to);
                        
                        // TODO Swap sets' locations as well in mSets
                    }
                }
            };

    private DragSortListView.RemoveListener onRemove = 
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                    adapter.remove(adapter.getItem(which));
                    
                    // TODO Remove set from mSets
                }
            };
            
            /**
             * Called in onCreateView. Override this to provide a custom
             * DragSortController.
             */
            public DragSortController buildController(DragSortListView dslv) {
                DragSortController controller = new DragSortController(dslv);
                controller.setRemoveEnabled(true);
                controller.setSortEnabled(true);
                controller.setDragInitMode(DragSortController.ON_LONG_PRESS);
                controller.setRemoveMode(DragSortController.FLING_REMOVE);
                
                return controller;
            }
            
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save sets onto the bundle via serializable
		for (int i = 0; i < mSets.size(); i++) {
			outState.putSerializable(KEY_SERIALIZABLE_SET + i, mSets.get(i));
		}

		outState.putInt(KEY_SETS_LIST_SIZE, mSets.size());
	}

	@Override
	public void getFocus() {
		// TODO Complete

	}

	private void setFocus() {
		// TODO Complete

	}

	private void createNewWorkout() {

		presenter.createNewWorkout();
		// TODO Complete - load data to the screen
	}

	private void loadWorkout(String workoutName) {

		presenter.loadWorkoutData(workoutName);
		// TODO Complete - load data to the screen
	}

	@Override
	public void onNumericInputEntered(AElement element, int numericValue) {
		// XXX FIND A MORE GENERIC WAY TO USE THIS DIALOG
		// Find out what was edited. Update the numeric value of the element
		if (element instanceof Set) {
			((Set)element).setRepetitions(numericValue);

			// Turn endless to false if not already false
			((Set)element).setEndless(false);
		} else if (element instanceof Rest) {
			((Rest)element).setTime(numericValue);
		} else if (element instanceof TimeExercise) {
			((TimeExercise)element).setTime(numericValue);
		}

		// Update sets
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onNameInputEntered(AElement element, String nameValue) {
		element.setName(nameValue);

		// Update sets
		adapter.notifyDataSetChanged();
	}
}
