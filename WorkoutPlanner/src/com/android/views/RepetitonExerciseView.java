package com.android.views;

import android.view.ViewGroup;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
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

		// Create if convertview is null of if number of reps & weights was
		// updated
		if ((convertView == null) || (((PreviewRepetitionExerciseHolder) convertView.getTag()).repsValueList.size() != ((RepetitionExercise) mElement).getReps().size())) {
			// Inflate
			convertView = inflater.inflate(R.layout.element_repetition_layout, parent, false);
			holder = new PreviewRepetitionExerciseHolder();
			holder.nameLabel = (TextView) convertView.findViewById(R.id.previewElementName);
			holder.commentInput = (EditText) convertView.findViewById(R.id.previewElementComment);
			holder.soundInput = (EditText) convertView.findViewById(R.id.previewElementSound);
			holder.dragHandler = (ImageView) convertView.findViewById(R.id.drag_handle);
			holder.data = (LinearLayout) convertView.findViewById(R.id.data);
			
			// Clearing out reps and weights if this is an update
			holder.repsValueList.clear();
			holder.weightsValueList.clear();
			holder.data.removeAllViews();

			// Create & read reps and weights
			for (int i = 0; i < ((RepetitionExercise) mElement).getReps().size(); i++) {
				// Add children to match the number of items in the list
				LinearLayout currLayout = new LinearLayout(mContext);
				TextView repsLabel = new TextView(mContext);
				TextView weightsLabel = new TextView(mContext);
				holder.repsValueList.add(new TextView(mContext));
				holder.weightsValueList.add(new TextView(mContext));

				// Set text. Since these views' text is constant and there's no
				// reason to keep them in the holder,
				// they are populated here. They simply cannot be inflated
				// because
				// they are dynamic
				repsLabel.setText(R.string.reps_label);
				weightsLabel.setText(R.string.weight_label);

				// Set style
				holder.repsValueList.get(i).setTextAppearance(mContext, R.style.smaller_bold);
				holder.weightsValueList.get(i).setTextAppearance(mContext, R.style.smaller_bold);

				// Adding the children to the view
				currLayout.addView(repsLabel);
				currLayout.addView(holder.repsValueList.get(i));

				// Add a space between the reps and sets
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
		holder.soundInput.setText(mElement.getSound());

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

		// TODO Change that into a dialog that allows selection of sounds
		holder.soundInput.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// After focus is lost, save the text into the set
					mElement.setSound(holder.soundInput.getText().toString());
				}
				
				// Value modified, set flag to true
				ElementsListActivity.sIsModified = true;
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

			// Setting values from element. Check for value in current index
			// first
			if (((RepetitionExercise) mElement).getReps().get(i) != null) {
				holder.repsValueList.get(i).setText(Integer.toString(((RepetitionExercise) mElement).getReps().get(i)));
			}
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
		}

		// Set holder visibility
		holder.dragHandler.setVisibility((ElementsListActivity.sEditListMode) ? View.VISIBLE : View.GONE);

		return convertView;
	}

	public RepetitonExerciseView(RepetitionExercise element, Context context) {
		super(context);
		mElement = element;
	}
}
