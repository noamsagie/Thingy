package com.android.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.element.AElement;
import com.android.element.Exercise;
import com.android.element.RepetitionExercise;
import com.android.element.Rest;
import com.android.element.TimeExercise;
import com.android.global.Consts;
import com.android.global.Consts.resultActivities;
import java.util.ArrayList;
import java.util.List;

public class PreviewExerciseAdapter extends ArrayAdapter<AElement> {

	// private static final String TAG = "WorkoutPlanner_PreviewSetAdapter";
	private ArrayList<AElement> mExercises;
	private ArrayAdapter<String> mSoundsAdapter;
	static Exercise sUndoSet;
	static ArrayList<Boolean> sExpandedViews = new ArrayList<Boolean>();

	public PreviewExerciseAdapter(Context context, int textViewResourceId, List<AElement> sets) {
		super(context, textViewResourceId, sets);
		mExercises = (ArrayList<AElement>) sets;

		// Set sounds into adapter
		mSoundsAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item);
		mSoundsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSoundsAdapter.addAll(((PreviewActivity) getContext()).mPresenter.loadSounds());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		PreviewItemHolder holder = null;

		// Fill flags
		fillExpandFlags(position);

		// Initialize view if convertview is null
		if (convertView == null) {
			v = newView(parent, position);
		}
		// Populate from previously saved holder
		else {
			// Use previous item if not null
			v = convertView;

			// Get holder
			holder = (PreviewItemHolder) v.getTag();

			// Set collapse status
			collapeExpandExercise(holder, sExpandedViews.get(position));
		}

		// Populate
		bindView(position, v);

		return v;
	}

	private View newView(ViewGroup parent, int position) {
		// Getting view somehow...
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View inflatedView = inflater.inflate(R.layout.preview_element_exercise, parent, false);
		PreviewItemHolder holder = new PreviewItemHolder();

		// Set a Set in the holder. It may be a different Set than the previous,
		// according to position
		holder.exercise = (Exercise) mExercises.get(position);

		// Set position of the set into id for later use. Very important
		holder.exercise.setId(position);
		holder.expandArea = (View) inflatedView.findViewById(R.id.expandArea);
		holder.data = (LinearLayout) inflatedView.findViewById(R.id.previewData);
		holder.setsLabel = (TextView) inflatedView.findViewById(R.id.previewRepetitionsInput);
		holder.nameLabel = (TextView) inflatedView.findViewById(R.id.previewSetNameLabel);
		holder.commentInput = (EditText) inflatedView.findViewById(R.id.previewSetCommentInput);
		holder.soundInput = (Spinner) inflatedView.findViewById(R.id.previewSetSoundInput);
		holder.editElementButton = (Button) inflatedView.findViewById(R.id.previewSetAddElements);
		holder.expand = (View) inflatedView.findViewById(R.id.infoArea);
		holder.collapse = (View) inflatedView.findViewById(R.id.collapse);
		holder.dragHandler = (ImageView) inflatedView.findViewById(R.id.drag_handle);

		inflatedView.setTag(holder);

		return inflatedView;
	}

	private void bindView(int position, View inflatedView) {
		final PreviewItemHolder holder = (PreviewItemHolder) inflatedView.getTag();

		holder.exercise = (Exercise) mExercises.get(position);

		// Set position of the set into id for later use. Very important
		holder.exercise.setId(position);
		holder.nameLabel.setText(holder.exercise.getName());
		holder.commentInput.setText(holder.exercise.getComment());
		holder.setsLabel.setText(String.valueOf(holder.exercise.getSets()));

		// Set sound adapter if it isn't already set
		if (holder.soundInput.getAdapter() == null) {
			holder.soundInput.setAdapter(mSoundsAdapter);
		}

		// Setting visibility of the drag handler
		holder.dragHandler.setVisibility((PreviewActivity.sEditListMode) ? View.VISIBLE : View.GONE);

		// Set lock/unlock for expanding views
		holder.expand.setEnabled((PreviewActivity.sEditListMode) ? false : true);

		// Make sure there is a name. If none, put default: Exercise - index
		// value (+1 for readability)
		if (holder.nameLabel.getText().equals("")) {
			holder.nameLabel.setText(getContext().getString(R.string.default_exercise_name) + " " + Integer.toString(holder.exercise.getId() + 1));
		}

		holder.setsLabel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NumericDialog instance = NumericDialog.newInstance(holder.exercise, NumericDialog.INTEGER_MODE, Consts.SET_SETS_METHOD_NAME);
				instance.show(((Activity) getContext()).getFragmentManager(), null);
			}
		});

		holder.nameLabel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SetNameDialog instance = SetNameDialog.newInstance(holder.exercise);
				instance.show(((Activity) getContext()).getFragmentManager(), null);
			}
		});

		holder.commentInput.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// After focus is lost, save the text into the set
					holder.exercise.setComment(holder.commentInput.getText().toString());
				}

				// Value modified, set flag to true
				PreviewActivity.sIsModified = true;
			}
		});

		holder.soundInput.setOnItemSelectedListener(new OnItemSelectedListener() {
			boolean isInitialized = false;

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// Do the following only when event is called from user. This SHOULD be called once programmatically when initialized
				if (isInitialized) {
					// Set sound name
					holder.exercise.setSound(parent.getItemAtPosition(pos).toString() + Consts.SOUND_FILE_EXTENSION);

					// Value modified, set flag to true
					PreviewActivity.sIsModified = true;
				}

				isInitialized = true;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		holder.expand.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Change visibility - Show expandArea and its data
				holder.expandArea.setVisibility(View.VISIBLE);
				holder.expand.setVisibility(View.GONE);
				holder.collapse.setVisibility(View.VISIBLE);

				// Save expand mode
				sExpandedViews.set(holder.exercise.getId(), true);

				// Display elements
				displayElements(holder);

				// Display selected sound
				holder.soundInput.setSelection(mSoundsAdapter.getPosition(holder.exercise.getSound().substring(0, holder.exercise.getSound().length() - Consts.SOUND_FILE_EXTENSION.length())));

				// Move screen to current item
				((PreviewActivity) getContext()).mItemsList.smoothScrollToPositionFromTop(holder.exercise.getId(), 0);
			}
		});

		holder.collapse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Change visibility - Hide expandArea and its data
				holder.expandArea.setVisibility(View.GONE);
				holder.collapse.setVisibility(View.GONE);
				holder.expand.setVisibility(View.VISIBLE);

				// Save expand mode
				sExpandedViews.set(holder.exercise.getId(), false);
			}
		});

		holder.editElementButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Create a new intent, put the selected set as a parameter and
				// pass to Elements list activity
				// to edit elements of the selected set
				Intent intent = new Intent();
				intent.setClass(getContext(), ElementsListActivity.class);
				intent.putExtra(Consts.CURRENT_EXERCISE, holder.exercise);
				((Activity) getContext()).startActivityForResult(intent, resultActivities.ELEMENTS_LIST.ordinal());
			}
		});
		
		// Update elements list if display is out of sync with data
		if (holder.data.getChildCount() != holder.exercise.getElements().size()) {
			// Display elements
			displayElements(holder);
		}
	}

	public void displayElements(PreviewItemHolder holder) {

		// Clear first
		holder.data.removeAllViews();

		// Go over elements and add the name of each to the display
		for (int i = 0; i < holder.exercise.getElements().size(); i++) {
			LinearLayout elementDisplay = new LinearLayout(getContext());
			TextView elementName = new TextView(getContext());

			// Set position and text and stuff
			elementName.setGravity(Gravity.CENTER);
			elementName.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			elementName.setTextAppearance(getContext(), R.style.smaller_bold);

			// Set text. If empty use default
			if (holder.exercise.getElements().get(i).getName().equals("")) {
				if (holder.exercise.getElements().get(i) instanceof TimeExercise) {
					elementName.setText(R.string.new_element_time);
				}
				else if (holder.exercise.getElements().get(i) instanceof Rest) {
					elementName.setText(R.string.new_element_rest);
				}
				else if (holder.exercise.getElements().get(i) instanceof RepetitionExercise) {
					elementName.setText(R.string.new_element_repetitions);
				}
			}
			else {
				elementName.setText(holder.exercise.getElements().get(i).getName());
			}

			elementDisplay.addView(elementName);

			holder.data.addView(elementDisplay);
		}
	}

	public void resetIds() {
		// Reset Id to match position if position has changed
		for (int i = 0; i < mExercises.size(); i++) {
			mExercises.get(i).setId(i);
		}
	}

	public void minimizeAll() {
		// Resetting all expand flags
		for (int i = 0; i < sExpandedViews.size(); i++) {
			sExpandedViews.set(i, false);
		}

		notifyDataSetChanged();
	}

	public void fillExpandFlags(int position) {
		// Create expand flags to match the number of sets in existence
		while (sExpandedViews.size() < position + 1) {
			// Add a new boolean for expand mode with default false value
			sExpandedViews.add(Boolean.valueOf(false));
		}
	}

	public void collapeExpandExercise(PreviewItemHolder holder, boolean expand) {
		if (expand) {
			// Change visibility - Show expandArea and its data
			holder.expandArea.setVisibility(View.VISIBLE);
			holder.collapse.setVisibility(View.VISIBLE);
			holder.expand.setVisibility(View.GONE);
		}
		else {
			// Change visibility - Hide expandArea and its data
			holder.expandArea.setVisibility(View.GONE);
			holder.collapse.setVisibility(View.GONE);
			holder.expand.setVisibility(View.VISIBLE);
		}
	}

	public ArrayList<AElement> getSets() {
		return mExercises;
	}
}
