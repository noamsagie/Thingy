package com.android.views;

import android.widget.TableLayout;

import android.widget.CheckBox;

import android.widget.EditText;

import java.util.ArrayList;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import java.util.List;

import android.widget.ArrayAdapter;
import com.android.element.Set;

public class PreviewSetAdapter extends ArrayAdapter<Set> {

	private ArrayList<Set> m_sets;

	public PreviewSetAdapter(Context context, int textViewResourceId, List<Set> sets) {
		super(context, textViewResourceId, sets);
		m_sets = (ArrayList<Set>) sets;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View inflatedView = convertView;
		
		if (inflatedView == null) {
		// Getting view somehow...
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflatedView = inflater.inflate(R.layout.preview_element_set, null, false);
		}
		// If first time populating
			// Populating data
			EditText commentInput = (EditText) inflatedView.findViewById(R.id.previewSetCommentInput);
			commentInput.setText(m_sets.get(position).getComment());
			EditText nameInput = (EditText) inflatedView.findViewById(R.id.previewSetNameInput);
			nameInput.setText(m_sets.get(position).getName());
			EditText soundInput = (EditText) inflatedView.findViewById(R.id.previewSetSoundInput);
			soundInput.setText(m_sets.get(position).getSound());
			CheckBox endlessInput = (CheckBox) inflatedView.findViewById(R.id.previewSetEndlessInput);
			endlessInput.setChecked(m_sets.get(position).getEndless());
			EditText repetitionsInput = (EditText) inflatedView.findViewById(R.id.previewSetRepetitionsInput);

			// If an endless set - Dropset
			if (m_sets.get(position).getEndless()) {
				// TODO Check that this does not cause problems. If it does
				// "take care of it". That means kill it
				repetitionsInput.setText("∞");
			}
			else {
				// Regular set
				repetitionsInput.setText(String.valueOf(m_sets.get(position).getRepetitions()));
			}

			TableLayout layout = (TableLayout) inflatedView.findViewById(R.id.previewSetLayout);
			final int setsLength = m_sets.get(position).getElements().size();

			for (int ii = 0; ii < setsLength; ii++) {
				// Set a new table row and set new views in it
				// layout.set
			}
		

		return inflatedView;
	}
}
