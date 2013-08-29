package com.android.views;

import android.widget.ImageView;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import com.android.element.AElement;
import com.android.element.RepetitionExercise;
import com.android.element.Rest;
import com.android.element.Set;
import com.android.element.TimeExercise;
import com.android.global.Consts;
import java.util.ArrayList;
import java.util.List;

public class PreviewSetAdapter extends ArrayAdapter<AElement> {

	private static final String TAG = "WorkoutPlanner_PreviewSetAdapter";
	private final int KEY_ELEMENT_LAYOUT_ID = 123456789;
	private ArrayList<AElement> mSets;
	static Set sUndoSet;
	static ArrayList<PreviewItemHolder> sHolders = new ArrayList<PreviewItemHolder>();
	static ArrayList<Boolean> sExpandedViews = new ArrayList<Boolean>();

	// Update these if indexes change!
	private static final int CHILDREN_INDEX_AFTER_PHASES_LABEL = 3;
	private static final int CHILDREN_INDEX_BEFORE_LISTS = 3;
	private static final int CHILDREN_INDEX_BEFORE_ELEMENTS = 4;

	public PreviewSetAdapter(Context context, int textViewResourceId, List<AElement> sets) {
		super(context, textViewResourceId, sets);
		mSets = (ArrayList<AElement>) sets;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		PreviewItemHolder holder = null;

		// Create expand flags to match the number of sets in existence
		while (sExpandedViews.size() < position + 1) {
			// Add a new boolean for expand mode with default false value
			sExpandedViews.add(Boolean.valueOf(false));
		}

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

			// If set is not the same object as the set in the array (removed
			// for example, or not in the listview children), re-populate with
			// appropriate Set
			if (!holder.set.equals(mSets.get(position))) {
				
				// If holder elements are not empty, clear them and collapse
				if (!holder.previewElementHolders.isEmpty()) {
					removeElement(holder);
					collapeExpandExercise(holder, false);
				}
				
				// Re-populating holder with correct set
				bindView(position, v);
				
				// If current holder should have elements, create them and expand
				if (sExpandedViews.get(position)) {
					createElements(holder);
					populateElements(holder);
					collapeExpandExercise(holder, true);
				}
			}
			else {
				// Update the current view's Rep exercises if boolean is true
				if (holder.readUpdateFlag()) {
					updateView(holder);
				}

				// Minimize if requested
				if (holder.readMinimizeFlag()) {
					collapeExpandExercise(holder, false);
				}
			}
		}

		// Populate if the holder is null (newly inflated view) OR
		// if current view's holder's flag is true and requires populating
		if ((holder == null) || (holder.readPopulateFlag())) {
			bindView(position, v);
		}

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
		holder.previewElementHolders = new ArrayList<PreviewElementHolder>();
		holder.expandArea = (View) inflatedView.findViewById(R.id.expandArea);
		holder.repetitionsLabel = (TextView) inflatedView.findViewById(R.id.previewRepetitionsInput);
		holder.endlessInput = (CheckBox) inflatedView.findViewById(R.id.previewSetEndlessInput);
		holder.nameLabel = (TextView) inflatedView.findViewById(R.id.previewSetNameLabel);
		holder.commentInput = (EditText) inflatedView.findViewById(R.id.previewSetCommentInput);
		holder.soundInput = (EditText) inflatedView.findViewById(R.id.previewSetSoundInput);
		holder.addElementButton = (Button) inflatedView.findViewById(R.id.previewSetAddElements);
		holder.expand = (View) inflatedView.findViewById(R.id.infoArea);
		holder.collapse = (View) inflatedView.findViewById(R.id.collapse);
		holder.dragHandler = (ImageView) inflatedView.findViewById(R.id.drag_handle);

		inflatedView.setTag(holder);
		sHolders.add(holder);

		return inflatedView;
	}

	// Remove children from the holder
	private void removeElement(PreviewItemHolder holder) {
		//final int setsLength = holder.set.getElements().size();
		int elementsLength = holder.previewElementHolders.size();

		// Removing views and then clearing holders
		for (int i = 0; i < elementsLength; i++) {
			((ViewGroup) holder.expandArea).removeViewAt(i + CHILDREN_INDEX_BEFORE_ELEMENTS);
		}
		
		holder.previewElementHolders.clear();
	}

	// Create elements for holder
	private void createElements(PreviewItemHolder holder) {
		final int setsLength = holder.set.getElements().size();

		for (int i = 0; i < setsLength; i++) {
			AElement currElement = holder.set.getElements().get(i);

			// Creating new element holder according to the type
			if (currElement instanceof Rest) {
				holder.previewElementHolders.add(new PreviewRestHolder());
			}
			else if (currElement instanceof TimeExercise) {
				holder.previewElementHolders.add(new PreviewTimeExerciseHolder());
			}
			else if (currElement instanceof RepetitionExercise) {
				holder.previewElementHolders.add(new PreviewRepetitionExerciseHolder());
			}

			View currLayout = inflateElement(currElement, (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE), i, holder.previewElementHolders.get(i));

			// Add the child before the hairline, collapse image and the add
			// button
			// (3 last children of the expandArea view
			// IF THIS VIEW IS CHANGED SO MUST BE THE FINAL VALUE OF
			// CHILDREN_INDEX_AFTER_PHASES_LABEL
			// TODO Should be a part of a DSLV of the current set...
			((ViewGroup) holder.expandArea).addView(currLayout, ((ViewGroup) holder.expandArea).getChildCount() - CHILDREN_INDEX_AFTER_PHASES_LABEL);
		}
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
		holder.dragHandler.setVisibility((PreviewActivity.sEditListMode) ? View.VISIBLE : View.GONE);

		// Make sure there is a name. If none, put default
		if (holder.nameLabel.getText().equals("")) {
			holder.nameLabel.setText(R.string.default_set_name);
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

				hideShowRepsWeights(holder);
			}

		});

		holder.repetitionsLabel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Setting flag to true to allow populating this view
				holder.updateFlag = true;
				NumericDialog instance = NumericDialog.newInstance(holder, holder.set, NumericDialog.INTEGER_MODE, Consts.SET_REPETITIONS_METHOD_NAME);
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
			}
		});

		holder.expand.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Change visibility - Show expandArea and its data
				holder.expandArea.setVisibility(View.VISIBLE);
				holder.expand.setVisibility(View.GONE);
				holder.collapse.setVisibility(View.VISIBLE);

				// Create elements of set when needed
				createElements(holder);

				// Populate elements
				for (PreviewElementHolder elementHolder : holder.previewElementHolders) {
					populateElement(elementHolder, holder);
				}

				// Save expand mode
				sExpandedViews.set(holder.set.getId(), true);

				// Move screen to current item
				// ((PreviewActivity)getContext()).mItemsList.smoothScrollToPositionFromTop(holder.set.getId(),
				// ???); FIXME Finish up here. Decide how
			}
		});

		holder.collapse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Change visibility - Hide expandArea and its data
				holder.expandArea.setVisibility(View.GONE);
				holder.collapse.setVisibility(View.GONE);
				holder.expand.setVisibility(View.VISIBLE);

				// Remove elements from holder
				removeElement(holder);

				// Save expand mode
				sExpandedViews.set(holder.set.getId(), false);
			}
		});

		holder.addElementButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Setting flag to true to allow updating this view
				holder.updateFlag = true;
				AddElementDialog instance = AddElementDialog.newInstance(holder);
				instance.show(((Activity) getContext()).getFragmentManager(), null);
			}
		});
		
		populateElements(holder);

		// Finally hide/show if needed - Should this be put somewhere else?
		hideShowRepsWeights(holder);
	}
	
	private void populateElements(PreviewItemHolder holder) {
		// Populate elements
		for (PreviewElementHolder elementHolder : holder.previewElementHolders) {
			populateElement(elementHolder, holder);
		}
	}

	private void populateElement(final PreviewElementHolder holder, final PreviewItemHolder father) {
		// Populate general views
		holder.nameLabel.setText(holder.element.getName());
		holder.commentInput.setText(holder.element.getComment());
		holder.soundInput.setText(holder.element.getSound());

		// Set listeners
		holder.nameLabel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Setting flag to true to allow populating this view
				father.rePopulateFlag = true;
				SetNameDialog instance = SetNameDialog.newInstance(holder.element);
				instance.show(((Activity) getContext()).getFragmentManager(), null);
			}
		});

		holder.commentInput.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// After focus is lost, save the text into the set
					holder.element.setComment(holder.commentInput.getText().toString());
				}
			}
		});

		// TODO Change that into a dialog that allows selection of sounds
		holder.soundInput.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// After focus is lost, save the text into the set
					holder.element.setSound(holder.soundInput.getText().toString());
				}
			}
		});

		// Populate specifics accordingly
		if (holder instanceof PreviewRestHolder) {
			populateRestElement((PreviewRestHolder) holder, father);
		}
		else if (holder instanceof PreviewTimeExerciseHolder) {
			populateTimeExerciseElement((PreviewTimeExerciseHolder) holder, father);
		}
		else if (holder instanceof PreviewRepetitionExerciseHolder) {
			populateRepetitionExerciseElement((PreviewRepetitionExerciseHolder) holder, father);
		}
	}

	private void populateRestElement(final PreviewRestHolder holder, final PreviewItemHolder father) {
		holder.timeLabel.setText(Double.toString(((Rest) holder.element).getTime()));

		// Make sure there is a name. If none, put default
		if (holder.nameLabel.getText().equals("")) {
			holder.nameLabel.setText(R.string.new_element_rest);
		}

		holder.timeLabel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Setting populate flag to true because views are about to
				// change!
				father.rePopulateFlag = true;
				NumericDialog instance = NumericDialog.newInstance(father, holder.element, NumericDialog.DOUBLE_MODE, Consts.SET_TIME_METHOD_NAME);
				instance.show(((Activity) getContext()).getFragmentManager(), null);
			}
		});
	}

	private void populateTimeExerciseElement(final PreviewTimeExerciseHolder holder, final PreviewItemHolder father) {
		holder.timeLabel.setText(Double.toString(((TimeExercise) holder.element).getTime()));

		// Make sure there is a name. If none, put default
		if (holder.nameLabel.getText().equals("")) {
			holder.nameLabel.setText(R.string.new_element_time);
		}

		holder.timeLabel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Setting populate flag to true because views are about to
				// change!
				father.rePopulateFlag = true;
				NumericDialog instance = NumericDialog.newInstance(father, holder.element, NumericDialog.DOUBLE_MODE, Consts.SET_TIME_METHOD_NAME);
				instance.show(((Activity) getContext()).getFragmentManager(), null);
			}
		});
	}

	private void populateRepetitionExerciseElement(final PreviewRepetitionExerciseHolder holder, final PreviewItemHolder father) {
		// Make sure there is a name. If none, put default
		if (holder.nameLabel.getText().equals("")) {
			holder.nameLabel.setText(R.string.new_element_repetitions);
		}

		// For the sake of shorter code
		final RepetitionExercise repEx = ((RepetitionExercise) holder.element);

		// Going over reps and weights text views lists
		for (int i = 0; i < holder.repsValueList.size(); i++) {

			// Current child index
			final int listIndex = i;

			// Setting values from element. Check for value in current index
			// first
			if (repEx.getReps().get(i) != null) {
				holder.repsValueList.get(i).setText(Integer.toString(repEx.getReps().get(i)));
			}
			if (repEx.getWeights().get(i) != null) {
				holder.weightsValueList.get(i).setText(Double.toString(repEx.getWeights().get(i)));
			}

			holder.repsValueList.get(i).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Setting populate flag to true because views are about to
					// change!
					father.rePopulateFlag = true;
					NumericDialog instance = NumericDialog.newInstance(father, repEx, NumericDialog.INTEGER_MODE, Consts.GET_REPS_MEHOD_NAME, listIndex);
					instance.show(((Activity) getContext()).getFragmentManager(), null);
				}
			});

			holder.weightsValueList.get(i).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Setting populate flag to true because views are about to
					// change!
					father.rePopulateFlag = true;
					NumericDialog instance = NumericDialog.newInstance(father, repEx, NumericDialog.DOUBLE_MODE, Consts.GET_WEIGHTS_METHOD_NAME, listIndex);
					instance.show(((Activity) getContext()).getFragmentManager(), null);
				}
			});
		}
	}

	// Hide or show reps and weights on repetition exercises
	private void hideShowRepsWeights(PreviewItemHolder holder) {
		for (int i = 0; i < holder.set.getElements().size(); i++) {
			if (holder.set.getElements().get(i) instanceof RepetitionExercise) {
				// Get view by id
				View currLayout = ((ViewGroup) holder.expandArea).findViewById(KEY_ELEMENT_LAYOUT_ID + i);

				if (currLayout != null) {
					for (int j = 0; j < ((ViewGroup) currLayout).getChildCount() - CHILDREN_INDEX_BEFORE_LISTS; j++) {
						// Hide if in endless mode
						if (holder.set.getEndless()) {
							((ViewGroup) currLayout).getChildAt(((ViewGroup) currLayout).getChildCount() - j - 1).setVisibility(View.GONE);
						}
						else {
							((ViewGroup) currLayout).getChildAt(((ViewGroup) currLayout).getChildCount() - j - 1).setVisibility(View.VISIBLE);
						}
					}
				}
			}
		}
	}

	// Warning.. ugly code ahead
	private void updateView(final PreviewItemHolder holder) {

		// If number of elements do not match the number of element holders, add
		// missing
		// element holders
		if (holder.set.getElements().size() > holder.previewElementHolders.size()) {
			int newElementIndex = holder.set.getElements().size() - 1;

			// Get the last added element.
			AElement currElement = holder.set.getElements().get(newElementIndex);

			// Creating new element holder according to the type
			if (currElement instanceof Rest) {
				holder.previewElementHolders.add(new PreviewRestHolder());
			}
			else if (currElement instanceof TimeExercise) {
				holder.previewElementHolders.add(new PreviewTimeExerciseHolder());
			}
			else if (currElement instanceof RepetitionExercise) {
				holder.previewElementHolders.add(new PreviewRepetitionExerciseHolder());
			}

			View currLayout = inflateElement(currElement, (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE), newElementIndex, holder.previewElementHolders.get(newElementIndex));

			// Add the child before the hairline, collapse image and the add
			// button
			// (3 last children of the expandArea view
			// IF THIS VIEW IS CHANGED SO MUST BE THE FINAL VALUE OF
			// CHILDREN_INDEX_AFTER_PHASES_LABEL
			((ViewGroup) holder.expandArea).addView(currLayout, ((ViewGroup) holder.expandArea).getChildCount() - CHILDREN_INDEX_AFTER_PHASES_LABEL);
		}
		else if (holder.set.getElements().size() < holder.previewElementHolders.size()) {
			boolean emptyElementFound = false;

			// If there are more element holders than elements, remove
			// unnecessary elements
			for (int i = 0; i < holder.previewElementHolders.size() && !emptyElementFound; i++) {
				// Set found flag to true, if it remains so after checking all
				// elements, it is true
				emptyElementFound = true;

				for (int j = 0; j < holder.set.getElements().size() && emptyElementFound; j++) {
					// Check for each holder element if any of the set elements
					// match it
					if (holder.previewElementHolders.get(i).element.equals(holder.set.getElements().get(j))) {
						emptyElementFound = false;
					}
				}

				// If no elements matched, remove the view and the holder
				if (emptyElementFound) {
					// Remove element view and element holder
					((ViewGroup) holder.expandArea).removeViewAt(CHILDREN_INDEX_BEFORE_ELEMENTS + i);
					holder.previewElementHolders.remove(i);
				}
			}

		}

		// Now looking for a repetition exercise in the holder. If there is
		// attempt to update it
		final int setsLength = holder.set.getElements().size();

		for (int i = 0; i < setsLength; i++) {
			AElement currElement = holder.set.getElements().get(i);

			if (currElement instanceof RepetitionExercise) {
				PreviewRepetitionExerciseHolder repHolder = (PreviewRepetitionExerciseHolder) holder.previewElementHolders.get(i);

				// Get the parent view of all the current repetition element
				View parent = ((ViewGroup) holder.expandArea).getChildAt(i + CHILDREN_INDEX_BEFORE_ELEMENTS);

				// Comparing lengths of the actual list size with the currently
				// displayed "list" of children
				int length = ((RepetitionExercise) currElement).getReps().size() - (((ViewGroup) parent).getChildCount() - CHILDREN_INDEX_BEFORE_LISTS);
				int childrenCount = ((ViewGroup) parent).getChildCount();
				boolean isListSizeSmaller = length <= 0;
				length = Math.abs(length);

				// Create/Remove & read reps and weights
				for (int j = 0; j < length; j++) {
					// Add or remove values from the list according to the flag
					// set
					// earlier
					if (isListSizeSmaller) {
						// Remove children because there are more of them than
						// the list. Removing from the last always
						((ViewGroup) parent).removeViewAt(childrenCount - j - 1);

						// Remove the last views
						repHolder.repsValueList.remove(repHolder.repsValueList.size() - 1);
						repHolder.weightsValueList.remove(repHolder.weightsValueList.size() - 1);
					}
					else {
						// Add children to match the number of items in the list
						LinearLayout currLayout = new LinearLayout(getContext());
						TextView repsLabel = new TextView(getContext());
						TextView weightsLabel = new TextView(getContext());
						repHolder.repsValueList.add(new TextView(getContext()));
						repHolder.weightsValueList.add(new TextView(getContext()));

						// Get the index of the newly added text of reps and
						// weights
						int repsWeightsIndex = repHolder.repsValueList.size() - 1;

						// Set text. Since these views' text is constant and
						// there's no
						// reason to keep them in the holder,
						// they are populated here. They simply cannot be
						// inflated because
						// they are dynamic
						repsLabel.setText(R.string.reps_label);
						weightsLabel.setText(R.string.weight_label);

						// Set style
						repHolder.repsValueList.get(repsWeightsIndex).setTextAppearance(getContext(), R.style.smaller_bold);
						repHolder.weightsValueList.get(repsWeightsIndex).setTextAppearance(getContext(), R.style.smaller_bold);

						// Adding the children to the view
						currLayout.addView(repsLabel);
						currLayout.addView(repHolder.repsValueList.get(repsWeightsIndex));

						// Add a space between the reps and sets
						Space space = new Space(getContext());
						space.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f));
						currLayout.addView(space);

						currLayout.addView(weightsLabel);
						currLayout.addView(repHolder.weightsValueList.get(repsWeightsIndex));

						// Finally add the layout to the parent
						((ViewGroup) parent).addView(currLayout);
					}
				}
			}
		}
	}

	private View inflateElement(AElement currElement, LayoutInflater inflater, int position, PreviewElementHolder holder) {
		View newLayout = null;

		// Setting element
		holder.element = currElement;

		if (currElement instanceof RepetitionExercise) {
			newLayout = inflateRepetitionElement(inflater, position, (PreviewRepetitionExerciseHolder) holder);
		}
		else if (currElement instanceof Rest) {
			newLayout = inflateTimeElement(inflater, holder);
		}
		else if (currElement instanceof TimeExercise) {
			newLayout = inflateTimeElement(inflater, holder);
		}
		else if (currElement instanceof Set) {
			// XXX Should one day make recursive sets available?
		}

		if (newLayout != null) {
			// Set layout color in a cycle of 2
			newLayout.setBackgroundResource((position % 2 == 0) ? R.color.greyish1 : R.color.greyish2);

			// Give the new layout an id
			newLayout.setId(KEY_ELEMENT_LAYOUT_ID + position);
		}

		return newLayout;
	}

	private View inflateRepetitionElement(LayoutInflater inflater, int setRepetitions, PreviewRepetitionExerciseHolder holder) {
		View result = new View(getContext());

		// Inflate
		result = inflater.inflate(R.layout.element_repetition_layout, null, false);
		holder.nameLabel = (TextView) result.findViewById(R.id.previewElementName);
		holder.commentInput = (EditText) result.findViewById(R.id.previewElementComment);
		holder.soundInput = (EditText) result.findViewById(R.id.previewElementSound);

		// Clearing reps and weights list first
		holder.repsValueList.clear();
		holder.weightsValueList.clear();

		// Create & read reps and weights
		for (int i = 0; i < ((RepetitionExercise) holder.element).getReps().size(); i++) {
			// Add children to match the number of items in the list
			LinearLayout currLayout = new LinearLayout(getContext());
			TextView repsLabel = new TextView(getContext());
			TextView weightsLabel = new TextView(getContext());
			holder.repsValueList.add(new TextView(getContext()));
			holder.weightsValueList.add(new TextView(getContext()));

			// Set text. Since these views' text is constant and there's no
			// reason to keep them in the holder,
			// they are populated here. They simply cannot be inflated because
			// they are dynamic
			repsLabel.setText(R.string.reps_label);
			weightsLabel.setText(R.string.weight_label);

			// Set style
			holder.repsValueList.get(i).setTextAppearance(getContext(), R.style.smaller_bold);
			holder.weightsValueList.get(i).setTextAppearance(getContext(), R.style.smaller_bold);

			// Adding the children to the view
			currLayout.addView(repsLabel);
			currLayout.addView(holder.repsValueList.get(i));

			// Add a space between the reps and sets
			Space space = new Space(getContext());
			space.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f));
			currLayout.addView(space);

			currLayout.addView(weightsLabel);
			currLayout.addView(holder.weightsValueList.get(i));

			// Finally add the layout to the parent
			((ViewGroup) result).addView(currLayout);
		}

		return result;
	}

	private View inflateTimeElement(LayoutInflater inflater, PreviewElementHolder holder) {
		View result = new View(getContext());

		// Inflate
		result = inflater.inflate(R.layout.element_time_layout, null, false);
		holder.nameLabel = (TextView) result.findViewById(R.id.previewElementName);
		holder.commentInput = (EditText) result.findViewById(R.id.previewElementComment);
		holder.soundInput = (EditText) result.findViewById(R.id.previewElementSound);

		// Inflate specifics
		if (holder.element instanceof Rest) {
			((PreviewRestHolder) holder).timeLabel = (TextView) result.findViewById(R.id.previewElementTime);
		}
		else if (holder.element instanceof TimeExercise) {
			((PreviewTimeExerciseHolder) holder).timeLabel = (TextView) result.findViewById(R.id.previewElementTime);
		}

		return result;
	}

	public void resetIds() {
		// Reset Id to match position if position has changed
		for (int i = 0; i < mSets.size(); i++) {
			mSets.get(i).setId(i);
		}
	}

	public void minimizeAll() {
		// Turn a minimize flags on, then call data set changed
		for (PreviewItemHolder curr : sHolders) {
			collapeExpandExercise(curr, false);
		}

		notifyDataSetChanged();
	}

	private void collapeExpandExercise(PreviewItemHolder holder, boolean expand) {
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

	/*
	 * public void setUpdateViewData(???) { // Setting populate flag to true
	 * because some views might need // to be re-constructed holder.updateFlag =
	 * true;
	 * 
	 * // TODO Finish up here
	 * 
	 * // Set undo to enable ((PreviewActivity) getContext()).setUndoMode(true);
	 * }
	 */

	public void showHideSortHandler(int visibility) {
		// Set visibility
		for (PreviewItemHolder curr : sHolders) {
			curr.dragHandler.setVisibility(visibility);
		}
	}

	public void lockExdends(boolean enabled) {
		// Set lock/unlock for expanding views
		for (PreviewItemHolder curr : sHolders) {
			curr.expand.setClickable(enabled);
		}
	}

	public ArrayList<AElement> getSets() {
		return mSets;
	}
}
