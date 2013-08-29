package com.android.views;

import com.android.element.AElement;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class SetNameDialog extends DialogFragment {

	private EditText mSetName;
	private static AElement sElement;
	onNameEnteredListener mCallback;
	private static final int MAX_TEXT_LENGTH = 24;

	public static SetNameDialog newInstance(AElement element) {
		final SetNameDialog frag = new SetNameDialog();
		sElement = element;
		return frag;
	}

	// Container Activity must implement this interface
	public interface onNameEnteredListener {
		public void onNameInputEntered(AElement element, String nameValue);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		// Close keyboard
		((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
			hideSoftInputFromWindow(mSetName.getWindowToken(), 0);
		
		// Close dialog if paused (screen locked and sich)
		dismiss();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (onNameEnteredListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement onNameEnteredListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initializing variables
		mSetName = new EditText(getActivity());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Creating set name dialog. Making a text field with confirmation and
		// cancel buttons
		builder.setTitle(R.string.set_name_label).setView(mSetName).setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Check if there's any input at all or if the text size is to
				// large
				if ((mSetName.getText().toString().trim().length() == 0) || (mSetName.getText().toString().trim().length() > MAX_TEXT_LENGTH)) {
					// Don't allow user to input label with only whitespace or
					// oversized text
					Toast.makeText(getActivity(), "Please enter a valid name. Must have text and now exceed " + 
													MAX_TEXT_LENGTH + 
													" characters", 
								   Toast.LENGTH_SHORT).show();
					
					// Close keyboard
					((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
						hideSoftInputFromWindow(mSetName.getWindowToken(), 0);
					
					dismiss();
				}
				else {
					// Put input in bundle
					mCallback.onNameInputEntered(sElement, mSetName.getText().toString().trim());
				}
				
				// Close keyboard
				((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
					hideSoftInputFromWindow(mSetName.getWindowToken(), 0);
			}
		}).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				// Close keyboard
				((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
					hideSoftInputFromWindow(mSetName.getWindowToken(), 0);
				
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
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		// Reset variables
		sElement = null;
		super.dismiss();
	}
}
