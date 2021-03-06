package com.android.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.android.element.AElement;
import com.android.element.Exercise;
import com.android.global.Consts;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class NumericDialog extends DialogFragment {

	public static final int INTEGER_MODE = 0;
	public static final int DOUBLE_MODE = 1;
	private static final int EMPTY = -1;
	private static final double MAX_SET_SIZE = 30;

	private static EditText mNumValue;
	private static AElement sElement;
	private static String sNumericMethod;
	private static int sMode;
	private static int sIndex = EMPTY;
	onNumberEnteredListener mCallback;

	public static NumericDialog newInstance(AElement element, int numericMode, String numericField) {
		final NumericDialog frag = new NumericDialog();
		sElement = element;
		sNumericMethod = numericField;

		// Set input to be numeric only
		switch (numericMode) {
		case INTEGER_MODE:
			sMode = INTEGER_MODE;
			break;
		case DOUBLE_MODE:
			sMode = DOUBLE_MODE;
			break;
		default:
			break;
		}

		return frag;
	}

	// An override for entering an array of numbers
	public static NumericDialog newInstance(AElement element, int numericMode, String numericField, int index) {
		// Set index and call override
		sIndex = index;
		
		return newInstance(element, numericMode, numericField);
	}

	// Container Activity must implement this interface
	// an element than one!
	public interface onNumberEnteredListener {
		public void onNumericInputEntered(AElement element);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (onNumberEnteredListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement onNumberEnteredListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initializing variables
		mNumValue = new EditText(getActivity());
		
		// Set input to be numeric only
		switch (sMode) {
		case INTEGER_MODE:
			mNumValue.setInputType(InputType.TYPE_CLASS_NUMBER);
			break;
		case DOUBLE_MODE:
			mNumValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			break;
		default:
			break;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Creating set name dialog. Making a text field with confirmation and
		// cancel buttons
		builder.setTitle(R.string.exercises_label).setView(mNumValue).setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				double input = -1;

				// Check if input is valid
				try {
					input = Double.parseDouble(mNumValue.getText().toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();

					// Close the dialog
					Toast.makeText(getActivity(), "Please enter a valid number", Toast.LENGTH_SHORT).show();

					// Close keyboard
					((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mNumValue.getWindowToken(), 0);

					dismiss();
				}

				// Check element is exercise and if input is between set ranges
				if ((sElement instanceof Exercise) && ((input < Consts.DEFAULT_SET_VALUE) || (input > MAX_SET_SIZE))) {
					// Close the dialog
					Toast.makeText(getActivity(), "Please enter a valid number of sets", Toast.LENGTH_SHORT).show();

					// Close keyboard
					((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mNumValue.getWindowToken(), 0);

					dismiss();
				}
				// Check element is NOT exercise and if input is between ranges 
				else if (!(sElement instanceof Exercise) && ((input < 0) || (input > 9999))) {
					// Check for time input
					// Close the dialog
					Toast.makeText(getActivity(), "Please enter a valid number", Toast.LENGTH_SHORT).show();

					// Close keyboard
					((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mNumValue.getWindowToken(), 0);

					dismiss();
				}
				else {					
					try {
						if (sMode == INTEGER_MODE) {
							if (sIndex == EMPTY) {
								// Set the input into the element by calling the setter method
								Method numericMethod = sElement.getClass().getMethod(sNumericMethod, new Class[] {Integer.TYPE});
								numericMethod.invoke(sElement, (int) input);
							}
							else {
								// Set the input into the element by calling the getter method of the array list
								Method numericMethod = sElement.getClass().getMethod(sNumericMethod, new Class[] {});
								ArrayList<Integer> numericList = (ArrayList<Integer>) numericMethod.invoke(sElement, null);
								
								numericList.set(sIndex, (int) input);
							}
						}
						else if (sMode == DOUBLE_MODE) {
							if (sIndex == EMPTY) {
								// Set the input into the element by calling the setter method
								Method numericMethod = sElement.getClass().getMethod(sNumericMethod, new Class[] {Double.TYPE});
								numericMethod.invoke(sElement, input);
							}
							else {
								Method numericMethod = sElement.getClass().getMethod(sNumericMethod, new Class[] {});
								ArrayList<Double> numericList = (ArrayList<Double>) numericMethod.invoke(sElement, null);
								
								numericList.set(sIndex, input);
							}
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
					// Send the event to the host activity
					mCallback.onNumericInputEntered(sElement);
				}

				// Close keyboard
				((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mNumValue.getWindowToken(), 0);
			}
		}).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				// Close keyboard
				((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mNumValue.getWindowToken(), 0);

				// User cancelled the dialog
				dismiss();
			}
		});

		// Open keyboard
		((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

		// Create the AlertDialog object and return it
		return builder.create();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		// Close keyboard
		((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
			hideSoftInputFromWindow(mNumValue.getWindowToken(), 0);
		
		// Close dialog if paused (screen locked and sich)
		dismiss();
	}
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		// Reset variables
		sIndex = EMPTY;
		sElement = null;
		sMode = EMPTY;
		sNumericMethod = "";
		super.onDismiss(dialog);
	}
}
