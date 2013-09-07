package com.android.views;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.element.RepetitionExercise;
import com.android.global.Consts;
import com.android.global.Consts.elementTypes;

public class RepetitonExerciseView extends AElementView {

	@Override
	public int getViewType() {
		return elementTypes.REPETITIONS_INDEX.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup parent, View convertView, int position) {
		final PreviewRepetitionExerciseHolder holder;

		// Create if convertview is null of if number of reps & weights &
		// endless was
		// updated
		if ((convertView == null) || (((PreviewRepetitionExerciseHolder) convertView.getTag()).repsValueList.size() != ((RepetitionExercise) mElement).getReps().size())) {
			// Inflate
			convertView = inflater.inflate(R.layout.element_repetition_layout, parent, false);
			holder = new PreviewRepetitionExerciseHolder();
			holder.nameLabel = (TextView) convertView.findViewById(R.id.previewElementName);
			holder.commentInput = (EditText) convertView.findViewById(R.id.previewElementComment);
			holder.soundInput = (Spinner) convertView.findViewById(R.id.previewSetSoundInput);
			holder.dragHandler = (ImageView) convertView.findViewById(R.id.drag_handle);
			holder.data = (LinearLayout) convertView.findViewById(R.id.data);

			// Clearing out reps and weights and endless if this is an update
			holder.repsValueList.clear();
			holder.weightsValueList.clear();
			holder.endlessSetList.clear();
			holder.data.removeAllViews();

			// Create & read reps and weights
			for (int i = 0; i < ((RepetitionExercise) mElement).getReps().size(); i++) {
				// Add children to match the number of items in the list
				LinearLayout currLayout = new LinearLayout(mContext);
				TextView repsLabel = new TextView(mContext);
				TextView weightsLabel = new TextView(mContext);
				holder.repsValueList.add(new TextView(mContext));
				holder.weightsValueList.add(new TextView(mContext));
				holder.endlessSetList.add(new CheckBox(mContext));

				// Set text. Since these views' text is constant and there's no
				// reason to keep them in the holder,
				// they are populated here. They simply cannot be inflated
				// because
				// they are dynamic
				repsLabel.setText(R.string.reps_label);
				weightsLabel.setText(R.string.weight_label);
				holder.endlessSetList.get(i).setText(R.string.endless_string);

				// Set style
				holder.repsValueList.get(i).setTextAppearance(mContext, R.style.smaller_bold);
				holder.weightsValueList.get(i).setTextAppearance(mContext, R.style.smaller_bold);

				// Adding the children to the view
				currLayout.addView(repsLabel);
				currLayout.addView(holder.repsValueList.get(i));
				currLayout.addView(holder.endlessSetList.get(i));

				// Add a space between the reps & endless option to the weights
				Space space = new Space(mContext);
				space.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f));
				currLayout.addView(space);

				currLayout.addView(weightsLabel);
				currLayout.addView(holder.weightsValueList.get(i));

				// Add layout to view
				holder.data.addView(currLayout);
			}

			// Set holder
			convertView.setTag(holder);
		}
		else {
			// Get holder from view
			holder = (PreviewRepetitionExerciseHolder) convertView.getTag();
		}

		// Populate
		mElement.setId(position);
		holder.element = mElement;
		holder.nameLabel.setText(mElement.getName());
		holder.commentInput.setText(mElement.getComment());

		// Set sound adapter if it isn't already set
		if (holder.soundInput.getAdapter() == null) {
			holder.soundInput.setAdapter(((ElementsListActivity) mContext).mSoundsAdapter);
		}

		// Set listeners
		holder.nameLabel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SetNameDialog instance = SetNameDialog.newInstance(mElement);
				instance.show(((Activity) mContext).getFragmentManager(), null);
			}
		});

		holder.commentInput.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// After focus is lost, save the text into the set
					mElement.setComment(holder.commentInput.getText().toString());
				}

				// Value modified, set flag to true
				ElementsListActivity.sIsModified = true;
			}
		});

		holder.soundInput.setOnItemSelectedListener(new OnItemSelectedListener() {
			boolean isInitialized = false;

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// Do the following only when event is called from user. This
				// SHOULD be called once programmatically when initialized
				if (isInitialized) {
					// Set sound name
					mElement.setSound(parent.getItemAtPosition(pos).toString() + Consts.SOUND_FILE_EXTENSION);

					// Value modified, set flag to true
					PreviewActivity.sIsModified = true;
				}

				isInitialized = true;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Do nothing
			}
		});

		// Make sure there is a name. If none, put default
		if (holder.nameLabel.getText().equals("")) {
			holder.nameLabel.setText(R.string.new_element_repetitions);
		}

		// Going over reps and weights text views lists
		for (int i = 0; i < holder.repsValueList.size(); i++) {

			// Current child index
			final int listIndex = i;

			if (((RepetitionExercise) mElement).getWeights().get(i) != null) {
				holder.weightsValueList.get(i).setText(Double.toString(((RepetitionExercise) mElement).getWeights().get(i)));
			}

			holder.repsValueList.get(i).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					NumericDialog instance = NumericDialog.newInstance(mElement, NumericDialog.INTEGER_MODE, Consts.GET_REPS_MEHOD_NAME, listIndex);
					instance.show(((Activity) mContext).getFragmentManager(), null);
				}
			});

			holder.weightsValueList.get(i).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					NumericDialog instance = NumericDialog.newInstance(mElement, NumericDialog.DOUBLE_MODE, Consts.GET_WEIGHTS_METHOD_NAME, listIndex);
					instance.show(((Activity) mContext).getFragmentManager(), null);
				}
			});

			// Setting values from element. Check for value in current index
			// first
			if (((RepetitionExercise) mElement).getEndlessSets().get(i) != null) {
				// Set checked
				holder.endlessSetList.get(i).setChecked(((RepetitionExercise) mElement).getEndlessSets().get(i));

				// Set reps value according to the endless flag
				if (((RepetitionExercise) mElement).getEndlessSets().get(i)) {
					holder.repsValueList.get(listIndex).setText(R.string.infinity);
				}
				else {
					// Setting values from element. Check for value in current
					// index first
					if (((RepetitionExercise) mElement).getReps().get(i) != null) {
						holder.repsValueList.get(i).setText(Integer.toString(((RepetitionExercise) mElement).getReps().get(i)));
					}
				}
			}

			// Set click listeners
			holder.endlessSetList.get(listIndex).setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// Do the following only when event is called from user
					if (buttonView.isPressed()) {
						// Save endless flag
						((RepetitionExercise) mElement).getEndlessSets().set(listIndex, isChecked);

						// If an endless set - Dropset
						if (isChecked) {
							holder.repsValueList.get(listIndex).setText(R.string.infinity);
						}
						else {
							// Setting values from element. Check for value in
							// current index
							// first
							if (((RepetitionExercise) mElement).getReps().get(listIndex) != null) {
								holder.repsValueList.get(listIndex).setText(Integer.toString(((RepetitionExercise) mElement).getReps().get(listIndex)));
							}
						}

						// Value modified, set flag to true
						ElementsListActivity.sIsModified = true;
					}
				}
			});
		}

		// Set holder visibility
		holder.dragHandler.setVisibility((ElementsListActivity.sEditListMode) ? View.VISIBLE : View.GONE);

		// Display selected sound
		holder.soundInput.setSelection(((ElementsListActivity) mContext).mSoundsAdapter.getPosition(mElement.getSound().substring(0, mElement.getSound().length() - Consts.SOUND_FILE_EXTENSION.length())));

		return convertView;
	}

	public RepetitonExerciseView(RepetitionExercise element, Context context) {
		super(context);
		mElement = element;
	}

	public RepetitonExerciseView(RepetitonExerciseView toClone) {
		super(toClone.mContext);
		mElement = new RepetitionExercise((RepetitionExercise) toClone.mElement);
	}
}
