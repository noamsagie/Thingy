package com.android.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import com.android.element.AElement;
import com.android.element.RepetitionExercise;
import com.android.element.Rest;
import com.android.element.Set;
import com.android.element.TimeExercise;
import com.android.global.Consts.elementTypes;

public class AddElementDialog extends DialogFragment {

	private static Set sSet;
	private onElementSelectedListener mCallback;

	public static AddElementDialog newInstance(Set set) {
		final AddElementDialog frag = new AddElementDialog();
		sSet = set;

		return frag;
	}

	// Container Activity must implement this interface
	// an element than one!
	public interface onElementSelectedListener {
		public void onElementSelectEntered(AElement newElement);
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
		elementNames[elementTypes.REPETITIONS_INDEX.ordinal()] = getString(R.string.element_repetitions_label);
		elementNames[elementTypes.REST_INDEX.ordinal()] = getString(R.string.element_rest_label);
		elementNames[elementTypes.TIME_INDEX.ordinal()] = getString(R.string.element_time_label);

		builder.setTitle(R.string.dialog_new_element_name).setItems(elementNames, new OnClickListener() {
			AElement newElement = null;

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				// Add according to the index of the selected item
				switch (elementTypes.values()[which]) {
				case REPETITIONS_INDEX:
					// Initialize with repetitions value
					newElement = new RepetitionExercise(sSet.getRepetitions());
					break;
				case REST_INDEX:
					newElement = new Rest();
					break;
				case TIME_INDEX:
					newElement = new TimeExercise();
					break;
				default:
					break;
				}
				
				// Set element id
				newElement.setId(sSet.getElements().size() - 1);
				
				// Add new element to the set
				sSet.getElements().add(newElement);

				// Update the altered view and send back the new element
				mCallback.onElementSelectEntered(newElement);
				
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
		sSet = null;
		super.dismiss();
	}
}
