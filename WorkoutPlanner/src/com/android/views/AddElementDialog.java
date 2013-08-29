package com.android.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import com.android.element.RepetitionExercise;
import com.android.element.Rest;
import com.android.element.TimeExercise;

public class AddElementDialog extends DialogFragment {

	private static PreviewItemHolder sHolder;
	private onElementSelectedListener mCallback;
	private final int REPETITIONS_INDEX = 0;
	private final int REST_INDEX = 1;
	private final int TIME_INDEX = 2;

	public static AddElementDialog newInstance(PreviewItemHolder holder) {
		final AddElementDialog frag = new AddElementDialog();
		sHolder = holder;

		return frag;
	}

	// Container Activity must implement this interface
	// an element than one!
	public interface onElementSelectedListener {
		public void onElementSelectEntered(PreviewItemHolder holder);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (onElementSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement onElementSelectedListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Names of elements
		String[] elementNames = new String[3];
		elementNames[REPETITIONS_INDEX] = getString(R.string.element_repetitions_label);
		elementNames[REST_INDEX] = getString(R.string.element_rest_label);
		elementNames[TIME_INDEX] = getString(R.string.element_time_label);

		builder.setTitle(R.string.dialog_new_element_name).setItems(elementNames, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// First of all, save the untouched set into the undo set
				// Copying original version of the clone to be able to undo
				//PreviewSetAdapter.sUndoSet = new Set(sHolder.set); // XXX Remove if not used. Probably won't be
				
				// Add according to the index of the selected item
				switch (which) {
				case REPETITIONS_INDEX:
					// Initialize with repetitions value
					sHolder.set.getElements().add(new RepetitionExercise(sHolder.set.getRepetitions()));
					break;
				case REST_INDEX:
					sHolder.set.getElements().add(new Rest());
					break;
				case TIME_INDEX:
					sHolder.set.getElements().add(new TimeExercise());
					break;
				default:
					break;
				}

				// Update the altered view
				mCallback.onElementSelectEntered(sHolder);
				
				dismiss();
			}
		});

		return builder.create();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		// Close dialog if paused (screen locked and sich)
		dismiss();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// Reset variables
		sHolder = null;
		super.dismiss();
	}
}
