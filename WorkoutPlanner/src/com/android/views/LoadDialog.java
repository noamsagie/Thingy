package com.android.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.android.global.Consts;
import com.android.presenters.LoadPresenter;

public class LoadDialog extends DialogFragment {

	public LoadDialog() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Set dialog message
		builder.setTitle(R.string.dialog_select_workout);

		// Creating the presenter for this view. Must be final to be called in
		// OnClick event...
		final LoadPresenter presenter = new LoadPresenter(this);

		// Setting all workouts in a list
		builder.setItems(presenter.getWorkouts(),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// The 'which' argument contains the index position
						// of the selected item
						Intent intent = new Intent(getActivity(),
								PreviewActivity.class);

						// Setting the name of the selected workout in the
						// intent's bundle
						intent.putExtra(Consts.SELECT_WORKOUT_TAG,
								presenter.getWorkout(which));

						startActivity(intent);
					}
				});

		// Create the AlertDialog object and return it
		return builder.create();
	}
}
