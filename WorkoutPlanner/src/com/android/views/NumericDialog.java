package com.android.views;

import com.android.element.Set;

import com.android.element.AElement;

import android.content.Context;

import android.view.inputmethod.InputMethodManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

public class NumericDialog extends DialogFragment {

	private EditText mNumValue;
	private static AElement sElement;
	onNumberEnteredListener mCallback;
	
    public static NumericDialog newInstance(AElement element) {
        final NumericDialog frag = new NumericDialog();
        sElement = element;
        return frag;
    }

	// Container Activity must implement this interface
	public interface onNumberEnteredListener {
	    public void onNumericInputEntered(AElement element, int repetitionValue);
	}

	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);

	    // This makes sure that the container activity has implemented
	    // the callback interface. If not, it throws an exception
	    try {
	        mCallback = (onNumberEnteredListener) activity;
	    } catch (ClassCastException e) {
	        throw new ClassCastException(activity.toString()
	                + " must implement onNumberEnteredListener");
	    }
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initializing variables
		mNumValue = new EditText(getActivity());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		// Set input to be numeric only
		mNumValue.setInputType(InputType.TYPE_CLASS_NUMBER);

		// Creating set name dialog. Making a text field with confirmation and
		// cancel buttons
		builder.setTitle(R.string.sets_label).setView(mNumValue).setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				int input = -1;
				
				// Check if input is valid
				try {
					input = Integer.parseInt(mNumValue.getText().toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();
					
					// Close the dialog
					Toast.makeText(getActivity(), "Please enter a valid number of repetitions", Toast.LENGTH_SHORT).show();
					
					// Close keyboard
					((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
						hideSoftInputFromWindow(mNumValue.getWindowToken(), 0);
					
					dismiss();
				}

				// Check if input is between 0 & 999 for set input
				if ((sElement instanceof Set) && ((input < 0) || (input > 999))) {
					// Close the dialog
					Toast.makeText(getActivity(), "Please enter a valid number of repetitions", Toast.LENGTH_SHORT).show();
					
					// Close keyboard
					((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
						hideSoftInputFromWindow(mNumValue.getWindowToken(), 0);
					
					dismiss();
				} else if ((input < 0) || (input > 99999)) {
					// Check for time input
					// Close the dialog
					Toast.makeText(getActivity(), "Please enter a valid number of seconds", Toast.LENGTH_SHORT).show();
					
					// Close keyboard
					((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
						hideSoftInputFromWindow(mNumValue.getWindowToken(), 0);
					
					dismiss();
				}
				else {
					// Send the event to the host activity
				    mCallback.onNumericInputEntered(sElement, input);
				}
				
				// Close keyboard
				((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
					hideSoftInputFromWindow(mNumValue.getWindowToken(), 0);
			}
		}).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				// Close keyboard
				((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
					hideSoftInputFromWindow(mNumValue.getWindowToken(), 0);
				
				// User cancelled the dialog
				dismiss();
			}
		});
		
		// Open keyboard
		((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
				toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		
		// Create the AlertDialog object and return it
		return builder.create();
	}
}
