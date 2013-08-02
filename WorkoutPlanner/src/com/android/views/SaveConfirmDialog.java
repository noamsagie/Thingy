package com.android.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import com.android.global.Consts;
import com.android.presenters.SaveConfirmPresenter;

public class SaveConfirmDialog extends DialogFragment {

	private String m_workoutName;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Retrieve workout name from bundle. Retrieving here because bundle
		// description mentioned something about having to get arguments right
		// after dialog creation
		m_workoutName = new String(getArguments().getCharSequence(
				Consts.WORKOUT_NAME_KEY).toString());

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Creating the presenter for this view. Must be final to be called in
		// OnClick event...
		final SaveConfirmPresenter presenter = new SaveConfirmPresenter(this);

		// Create confirmation dialog instead of the regular dialog
		builder.setTitle(R.string.dialog_confirm_overwrite)
				.setPositiveButton(R.string.dialog_save_confirm,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Check if there's any input at all
								presenter.Save();
							}
						})
				.setNegativeButton(R.string.dialog_save_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						});

		// Create the AlertDialog object and return it
		return builder.create();
	}

	public String getWorkoutName() {
		return m_workoutName;
	}
}
