package com.android.views;

import com.android.element.RepetitionExercise;

import com.android.element.Rest;

import com.android.element.TimeExercise;

import android.widget.LinearLayout.LayoutParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.element.AElement;
import com.android.element.Set;
import com.android.global.Consts;
import com.android.global.Consts.resultActivities;
import java.util.ArrayList;
import java.util.List;

public class PreviewSetAdapter extends ArrayAdapter<AElement> {

	// private static final String TAG = "WorkoutPlanner_PreviewSetAdapter";
	private ArrayList<AElement> mSets;
	static Set sUndoSet;
	static ArrayList<Boolean> sExpandedViews = new ArrayList<Boolean>();

	public PreviewSetAdapter(Context context, int textViewResourceId, List<AElement> sets) {
		super(context, textViewResourceId, sets);
		mSets = (ArrayList<AElement>) sets;
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
		View inflatedView = inflater.inflate(R.layout.preview_element_set, parent, false);
		PreviewItemHolder holder = new PreviewItemHolder();

		// Set a Set in the holder. It may be a different Set than the previous,
		// according to position
		holder.set = (Set) mSets.get(position);

		// Set position of the set into id for later use. Very important
		holder.set.setId(position);
		holder.expandArea = (View) inflatedView.findViewById(R.id.expandArea);
		holder.data = (LinearLayout) inflatedView.findViewById(R.id.previewData);
		holder.repetitionsLabel = (TextView) inflatedView.findViewById(R.id.previewRepetitionsInput);
		holder.endlessInput = (CheckBox) inflatedView.findViewById(R.id.previewSetEndlessInput);
		holder.nameLabel = (TextView) inflatedView.findViewById(R.id.previewSetNameLabel);
		holder.commentInput = (EditText) inflatedView.findViewById(R.id.previewSetCommentInput);
		holder.soundInput = (EditText) inflatedView.findViewById(R.id.previewSetSoundInput);
		holder.editElementButton = (Button) inflatedView.findViewById(R.id.previewSetAddElements);
		holder.expand = (View) inflatedView.findViewById(R.id.infoArea);
		holder.collapse = (View) inflatedView.findViewById(R.id.collapse);
		holder.dragHandler = (ImageView) inflatedView.findViewById(R.id.drag_handle);

		inflatedView.setTag(holder);

		return inflatedView;
	}

	private void bindView(int position, View inflatedView) {
		final PreviewItemHolder holder = (PreviewItemHolder) inflatedView.getTag();

		holder.set = (Set) mSets.get(position);

		// Set position of the set into id for later use. Very important
		holder.set.setId(position);
		holder.endlessInput.setChecked(holder.set.getEndless());
		holder.soundInput.setText(holder.set.getSound());
		holder.nameLabel.setText(holder.set.getName());
		holder.commentInput.setText(holder.set.getComment());

		// Setting visibility of the drag handler
		holder.dragHandler.setVisibility((PreviewActivity.sEditListMode) ? View.VISIBLE : View.GONE);

		// Set lock/unlock for expanding views
		holder.expand.setEnabled((PreviewActivity.sEditListMode) ? false : true);

		// Make sure there is a name. If none, put default: Exercise - index
		// value (+1 for readability)
		if (holder.nameLabel.getText().equals("")) {
			holder.nameLabel.setText(getContext().getString(R.string.default_set_name) + " " + Integer.toString(holder.set.getId() + 1));
		}

		// Set repetitions value according to the endless flag
		if (holder.set.getEndless()) {
			holder.repetitionsLabel.setText(R.string.infinity);
		}
		else {
			holder.repetitionsLabel.setText(String.valueOf(holder.set.getRepetitions()));
		}

		// Set click listeners
		holder.endlessInput.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				// Save endless flag
				holder.set.setEndless(isChecked);

				// If an endless set - Dropset
				if (isChecked) {
					holder.repetitionsLabel.setText(R.string.infinity);
				}
				else {
					// Regular set
					holder.repetitionsLabel.setText(String.valueOf(holder.set.getRepetitions()));
				}

				// Value modified, set flag to true
				PreviewActivity.sIsModified = true;
			}

		});

		holder.repetitionsLabel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Setting flag to true to allow populating this view
				holder.updateFlag = true;
				NumericDialog instance = NumericDialog.newInstance(holder.set, NumericDialog.INTEGER_MODE, Consts.SET_REPETITIONS_METHOD_NAME);
				instance.show(((Activity) getContext()).getFragmentManager(), null);
			}
		});

		holder.nameLabel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Setting flag to true to allow updating this view
				holder.rePopulateFlag = true;
				SetNameDialog instance = SetNameDialog.newInstance(holder.set);
				instance.show(((Activity) getContext()).getFragmentManager(), null);
			}
		});

		holder.commentInput.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// After focus is lost, save the text into the set
					holder.set.setComment(holder.commentInput.getText().toString());
				}

				// Value modified, set flag to true
				PreviewActivity.sIsModified = true;
			}
		});

		// TODO Change that into a dialog that allows selection of sounds
		holder.soundInput.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// After focus is lost, save the text into the set
					holder.set.setSound(holder.soundInput.getText().toString());
				}

				// Value modified, set flag to true
				PreviewActivity.sIsModified = true;
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
				sExpandedViews.set(holder.set.getId(), true);

				// Move screen to current item
				((PreviewActivity) getContext()).mItemsList.smoothScrollToPositionFromTop(holder.set.getId(), 0);
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
				sExpandedViews.set(holder.set.getId(), false);
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
				intent.putExtra(Consts.CURRENT_SET, holder.set);
				((Activity) getContext()).startActivityForResult(intent, resultActivities.ELEMENTS_LIST.ordinal());
			}
		});

		// Display elements
		displayElements(holder);
	}

	private void displayElements(PreviewItemHolder holder) {

		// Clear first
		holder.data.removeAllViews();

		// Go over elements and add the name of each to the display
		for (int i = 0; i < holder.set.getElements().size(); i++) {
			LinearLayout elementDisplay = new LinearLayout(getContext());
			TextView elementName = new TextView(getContext());

			// Set position and text and stuff
			elementName.setGravity(Gravity.CENTER);
			elementName.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			elementName.setTextAppearance(getContext(), R.style.smaller_bold);

			// Set text. If empty use default
			if (holder.set.getElements().get(i).getName().equals("")) {
				if (holder.set.getElements().get(i) instanceof TimeExercise) {
					elementName.setText(R.string.new_element_time);
				} else if (holder.set.getElements().get(i) instanceof Rest) {
					elementName.setText(R.string.new_element_rest);
				} else if (holder.set.getElements().get(i) instanceof RepetitionExercise) {
					elementName.setText(R.string.new_element_repetitions);
				}
			}
			else {
				elementName.setText(holder.set.getElements().get(i).getName());
			}

			elementDisplay.addView(elementName);

			holder.data.addView(elementDisplay);
		}
	}

	public void resetIds() {
		// Reset Id to match position if position has changed
		for (int i = 0; i < mSets.size(); i++) {
			mSets.get(i).setId(i);
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
		return mSets;
	}
}
