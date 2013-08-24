package com.android.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import com.android.element.Set;

public class SelectPositionDialog extends DialogFragment {
	
	onPositionSelectedListener mCallback;
	private static Set sParent;
	
	public static SelectPositionDialog newInstance(Set parent) {
		final SelectPositionDialog frag = new SelectPositionDialog();
		sParent = parent;
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		// TODO Complete
		
		return builder.create();
	}
	
	// Container Activity must implement this interface
	public interface onPositionSelectedListener {
		public void onPositionSelected(Set parent, int from, int to);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (onPositionSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement onPositionSelectedListener");
		}
	}
}
