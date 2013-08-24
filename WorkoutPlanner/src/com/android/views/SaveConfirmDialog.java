package com.android.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import com.android.presenters.SaveConfirmPresenter;

public class SaveConfirmDialog extends DialogFragment {

	private static String sWorkoutName;
	
	public static SaveConfirmDialog newInstance(String workoutName) {
		final SaveConfirmDialog frag = new SaveConfirmDialog();
		sWorkoutName = workoutName;
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Creating the presenter for this view. Must be final to be called in
		// OnClick event...
		final SaveConfirmPresenter presenter = new SaveConfirmPresenter(this);

		// Create confirmation dialog instead of the regular dialog
		builder.setTitle(R.string.dialog_confirm_overwrite).setPositiveButton(R.string.dialog_save_confirm, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Check if there's any input at all
				presenter.save();
			}
		}).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
				dismiss();
			}
		});

		// Create the AlertDialog object and return it
		return builder.create();
	}

	public String getWorkoutName() {
		return sWorkoutName;
	}
	
	@Override
	public void dismiss() {
		// Reset var
		sWorkoutName = null;
		super.dismiss();
	}
}
