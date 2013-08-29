package com.android.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.android.presenters.SavePresenter;

public class SaveDialog extends DialogFragment {

	onSaveCompletedListener mCallback;
	
	// Create text field
	private EditText mWorkoutName;

	// Container Activity must implement this interface
	public interface onSaveCompletedListener {
		public void onSaveCompleted(String workoutName);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (onSaveCompletedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement onSaveCompletedListener");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Initializing variables
		mWorkoutName = new EditText(getActivity());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Creating the presenter for this view. Must be final to be called in
		// OnClick event...
		final SavePresenter presenter = new SavePresenter(this);

		// Creating save dialog. Making a text field with confirmation and
		// cancel buttons
		builder.setTitle(R.string.dialog_save_workout).setView(mWorkoutName).setPositiveButton(R.string.dialog_save_confirm, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Check if there's any input at all
				presenter.processRequest();
				
				mCallback.onSaveCompleted(getWorkoutName());
				
				// Close keyboard
				((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
					hideSoftInputFromWindow(mWorkoutName.getWindowToken(), 0);
			}
		}).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
				// Close keyboard
				((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
					hideSoftInputFromWindow(mWorkoutName.getWindowToken(), 0);
				
				// User cancelled the dialog
				dismiss();
			}
		});
		
		// Open keyboard
		((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
				toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);

		// Create the AlertDialog object and return it
		return builder.create();
	}

	// Return text input without spaces at the beginning or end
	public String getWorkoutName() {
		return mWorkoutName.getText().toString().trim();
	}
}
