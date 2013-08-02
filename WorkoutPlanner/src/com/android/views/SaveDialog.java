package com.android.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import com.android.presenters.SavePresenter;

public class SaveDialog extends DialogFragment {

	// Create text field
	private EditText m_etWorkoutName;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Initializing variables
		m_etWorkoutName = new EditText(getActivity());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Creating the presenter for this view. Must be final to be called in
		// OnClick event...
		final SavePresenter presenter = new SavePresenter(this);

		// Creating save dialog. Making a text field with confirmation and
		// cancel buttons
		builder.setTitle(R.string.dialog_save_workout).setView(m_etWorkoutName).setPositiveButton(R.string.dialog_save_confirm, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Check if there's any input at all
				presenter.ProcessRequest();
			}
		}).setNegativeButton(R.string.dialog_save_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
			}
		});

		// Create the AlertDialog object and return it
		return builder.create();
	}

	// Return text input without spaces at the beginning or end
	public String getWorkoutName() {
		return m_etWorkoutName.getText().toString().trim();
	}
}
