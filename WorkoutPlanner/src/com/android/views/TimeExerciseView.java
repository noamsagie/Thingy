package com.android.views;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.element.TimeExercise;
import com.android.global.Consts;
import com.android.global.Consts.elementTypes;

public class TimeExerciseView extends AElementView {
	@Override
	public int getViewType() {
		return elementTypes.TIME_INDEX.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup parent, View convertView, int position) {
		final PreviewTimeExerciseHolder holder;

		if (convertView == null) {
			// Inflate and setup holder
			convertView = inflater.inflate(R.layout.element_time_layout, parent, false);
			holder = new PreviewTimeExerciseHolder();
			holder.nameLabel = (TextView) convertView.findViewById(R.id.previewElementName);
			holder.commentInput = (EditText) convertView.findViewById(R.id.previewElementComment);
			holder.soundInput = (Spinner) convertView.findViewById(R.id.previewSetSoundInput);
			holder.timeLabel = (TextView) convertView.findViewById(R.id.previewElementTime);
			holder.dragHandler = (ImageView) convertView.findViewById(R.id.drag_handle);

			// Set holder
			convertView.setTag(holder);
		}
		else {
			// Get holder from view
			holder = (PreviewTimeExerciseHolder) convertView.getTag();
		}

		// Populate
		mElement.setId(position);
		holder.element = mElement;
		holder.nameLabel.setText(mElement.getName());
		holder.commentInput.setText(mElement.getComment());
		holder.timeLabel.setText(Double.toString(((TimeExercise) mElement).getTime()));

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
			holder.nameLabel.setText(R.string.new_element_time);
		}

		holder.timeLabel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NumericDialog instance = NumericDialog.newInstance(mElement, NumericDialog.DOUBLE_MODE, Consts.SET_TIME_METHOD_NAME);
				instance.show(((Activity) mContext).getFragmentManager(), null);
			}
		});

		// Set holder visibility
		holder.dragHandler.setVisibility((ElementsListActivity.sEditListMode) ? View.VISIBLE : View.GONE);

		// Display selected sound
		holder.soundInput.setSelection(((ElementsListActivity) mContext).mSoundsAdapter.getPosition(mElement.getSound().substring(0, mElement.getSound().length() - Consts.SOUND_FILE_EXTENSION.length())));

		return convertView;
	}

	public TimeExerciseView(TimeExercise element, Context context) {
		super(context);
		mElement = element;
	}

	public TimeExerciseView(TimeExerciseView toClone) {
		super(toClone.mContext);
		mElement = new TimeExercise((TimeExercise) toClone.mElement);
	}
}
