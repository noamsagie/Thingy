package com.android.views;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
			holder.soundInput = (EditText) convertView.findViewById(R.id.previewElementSound);
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
		holder.soundInput.setText(mElement.getSound());
		holder.timeLabel.setText(Double.toString(((TimeExercise) mElement).getTime()));

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
