package com.android.views;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import com.android.element.AElement;
import com.android.element.RepetitionExercise;
import com.android.element.Rest;
import com.android.element.Set;
import com.android.element.TimeExercise;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PreviewSetAdapter extends ArrayAdapter<Set> {

	private static final String TAG = "WorkoutPlanner_PreviewSetAdapter";
	private final int KEY_ELEMENT_LAYOUT_ID = 123456789;
	private final int CHILDREN_AFTER_PHASES_LABEL = 2;
	private ArrayList<Set> m_sets;
	private HashMap<Integer, View> cache;

	public PreviewSetAdapter(Context context, int textViewResourceId, List<Set> sets) {
		super(context, textViewResourceId, sets);
		cache = new HashMap<Integer, View>();
		m_sets = (ArrayList<Set>) sets;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i(TAG, "getView " + position);
		if (cache.containsKey(position)) {
			// TODO Ask Ben-Or what to do here. Update I think
		}

		View inflatedView = convertView;
		final PreviewItemHolder holder;

		// Initialize view
		if (inflatedView == null) {
			// Getting view somehow...
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflatedView = inflater.inflate(R.layout.preview_element_set, null, false);

			holder = new PreviewItemHolder();

			// inflatedView = newView();
			holder.set = m_sets.get(position);
			holder.set.setId(position);
			holder.repetitionsLabel = (TextView) inflatedView.findViewById(R.id.previewRepetitionsInput);
			holder.repetitionsLabel.setText(String.valueOf(holder.set.getRepetitions()));
			holder.endlessInput = (CheckBox) inflatedView.findViewById(R.id.previewSetEndlessInput);
			holder.endlessInput.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

					// Save endless flag
					holder.set.setEndless(isChecked);

					// If an endless set - Dropset
					if (isChecked) {
						holder.repetitionsLabel.setText("∞");
					}
					else {
						// Regular set
						holder.repetitionsLabel.setText(String.valueOf(holder.set.getRepetitions()));
					}
				}
			});

			// Now that the listener has been created updating the checked value
			holder.endlessInput.setChecked(holder.set.getEndless());

			// If first time populating
			// Populating data
			holder.nameLabel = (TextView) inflatedView.findViewById(R.id.previewSetNameLabel);
			holder.nameLabel.setText(holder.set.getName());
			holder.commentInput = (EditText) inflatedView.findViewById(R.id.previewSetCommentInput);
			holder.commentInput.setText(holder.set.getComment());
			holder.soundInput = (EditText) inflatedView.findViewById(R.id.previewSetSoundInput);
			holder.soundInput.setText(holder.set.getSound());
			holder.expandArea = (View) inflatedView.findViewById(R.id.expandArea);
			holder.expand = (View) inflatedView.findViewById(R.id.infoArea);
			holder.collapse = (View) inflatedView.findViewById(R.id.collapse);

			// Set click listeners
			holder.repetitionsLabel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					NumericDialog num = NumericDialog.newInstance(holder.set);
					num.show(((Activity) getContext()).getFragmentManager(), null);
				}
			});

			holder.nameLabel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SetNameDialog setName = SetNameDialog.newInstance(holder.set);
					setName.show(((Activity) getContext()).getFragmentManager(), null);
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
					// Change visibility
					holder.expandArea.setVisibility(View.VISIBLE);
					holder.expand.setVisibility(View.GONE);
					holder.collapse.setVisibility(View.VISIBLE);
				}
			});

			holder.collapse.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Change visibility
					holder.expandArea.setVisibility(View.GONE);
					holder.collapse.setVisibility(View.GONE);
					holder.expand.setVisibility(View.VISIBLE);
				}
			});

			final int setsLength = holder.set.getElements().size();

			for (int i = 0; i < setsLength; i++) {
				AElement currElement = holder.set.getElements().get(i);
				View newLayout = null;

				if (currElement instanceof RepetitionExercise) {
					newLayout = inflateRepetitionElement(currElement, inflater);
				}
				else if (currElement instanceof Rest) {
					newLayout = inflateTimeElement(currElement, inflater);
				}
				else if (currElement instanceof TimeExercise) {
					newLayout = inflateTimeElement(currElement, inflater);
				}
				else if (currElement instanceof Set) {
					// TODO Check if to create recursive set
				}

				if (newLayout != null) {
					// Set layout color in a cycle of 2
					newLayout.setBackgroundResource((i % 2 == 0) ? R.color.greyish1 : R.color.greyish2);

					// Give the new layout an id
					newLayout.setId(KEY_ELEMENT_LAYOUT_ID + i);

					// Add the child before the hairline and the collapse image (2 last children of the expandArea view
					// IF THIS VIEW IS CHANGED SO MUST BE THE FINAL VALUE OF CHILDREN_AFTER_PHASES_LABEL
					// TODO Should be a part of a DSLV of the current set...
					((ViewGroup) holder.expandArea).addView(newLayout, ((ViewGroup) holder.expandArea).getChildCount() - CHILDREN_AFTER_PHASES_LABEL);

				}
			}

			inflatedView.setTag(holder);
		}
		// Populate from previously saved holder
		else {
			holder = (PreviewItemHolder) inflatedView.getTag();
			holder.endlessInput.setChecked(holder.set.getEndless());
			holder.nameLabel.setText(holder.set.getName());
			holder.commentInput.setText(holder.set.getComment());
			holder.soundInput.setText(holder.set.getSound());

			// Update repetitions value only if endless is false, otherwise show
			// "∞"
			if (holder.set.getEndless()) {
				holder.repetitionsLabel.setText("∞");
			}
			else {
				holder.repetitionsLabel.setText(String.valueOf(holder.set.getRepetitions()));
			}

			final int setsLength = holder.set.getElements().size();

			for (int i = 0; i < setsLength; i++) {
				AElement currElement = holder.set.getElements().get(i);

				// Get view by id
				View view = ((ViewGroup) holder.expandArea).findViewById(KEY_ELEMENT_LAYOUT_ID + i);

				// Check for element values and if they exist, update them
				TextView elementName = (TextView) view.findViewById(R.id.previewElementName);
				TextView elementTime = (TextView) view.findViewById(R.id.previewElementTime);

				// Update values
				if (elementName != null) {
					elementName.setText(currElement.getName());
				}

				if (elementTime != null) {
					if (currElement instanceof Rest) {
						elementTime.setText(Double.toString(((Rest) currElement).getTime()));
					}
					else if (currElement instanceof TimeExercise) {
						elementTime.setText(Double.toString(((TimeExercise) currElement).getTime()));
					}
				}
			}
		}

		return inflatedView;
	}

	private View inflateRepetitionElement(final AElement element, LayoutInflater inflater) {
		View result = new View(getContext());

		result = inflater.inflate(R.layout.element_repetition_layout, null, false);
		TextView elementName = (TextView) result.findViewById(R.id.previewElementName);
		final EditText elementComment = (EditText) result.findViewById(R.id.previewElementComment);
		final EditText elementSound = (EditText) result.findViewById(R.id.previewElementSound);

		elementName.setText(element.getName());
		elementComment.setText(element.getComment());
		elementSound.setText(element.getSound());

		elementName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SetNameDialog setName = SetNameDialog.newInstance(element);
				setName.show(((Activity) getContext()).getFragmentManager(), null);
			}
		});
		
		elementComment.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// After focus is lost, save the text into the set
					element.setComment(elementComment.getText().toString());
				}
			}
		});

		// TODO Change that into a dialog that allows selection of sounds
		elementSound.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// After focus is lost, save the text into the set
					element.setSound(elementSound.getText().toString());
				}
			}
		});

		return result;
	}

	private View inflateTimeElement(final AElement element, LayoutInflater inflater) {
		View result = new View(getContext());

		result = inflater.inflate(R.layout.element_time_layout, null, false);
		TextView elementName = (TextView) result.findViewById(R.id.previewElementName);
		TextView elementTime = (TextView) result.findViewById(R.id.previewElementTime);
		final EditText elementComment = (EditText) result.findViewById(R.id.previewElementComment);
		final EditText elementSound = (EditText) result.findViewById(R.id.previewElementSound);

		elementName.setText(element.getName());
		elementComment.setText(element.getComment());
		elementSound.setText(element.getSound());
		if (element instanceof Rest) {
			elementTime.setText(Double.toString(((Rest) element).getTime()));
		}
		else if (element instanceof TimeExercise) {
			elementTime.setText(Double.toString(((TimeExercise) element).getTime()));
		}

		elementName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SetNameDialog setName = SetNameDialog.newInstance(element);
				setName.show(((Activity) getContext()).getFragmentManager(), null);
			}
		});

		elementTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NumericDialog num = NumericDialog.newInstance(element);
				num.show(((Activity) getContext()).getFragmentManager(), null);
			}
		});
		
		elementComment.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// After focus is lost, save the text into the set
					element.setComment(elementComment.getText().toString());
				}
			}
		});

		// TODO Change that into a dialog that allows selection of sounds
		elementSound.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// After focus is lost, save the text into the set
					element.setSound(elementSound.getText().toString());
				}
			}
		});

		return result;
	}
}
