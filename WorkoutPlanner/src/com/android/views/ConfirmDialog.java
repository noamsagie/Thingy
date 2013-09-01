package com.android.views;

import java.lang.reflect.InvocationTargetException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ConfirmDialog extends DialogFragment {

	private static String sTitle;
	private static String sMessage;
	private static String sPositiveMethodName;
	private static Class<?> sClass;
	
	public static ConfirmDialog newInstance(String title, String message, String positiveMethodName, Class<?> callingClass) {
		ConfirmDialog frag = new ConfirmDialog();
		sTitle = title;
		sMessage = message;
		sPositiveMethodName = positiveMethodName;
		sClass = callingClass;
		
		return frag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle(sTitle).setMessage(sMessage).setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Check for given method
				if (sPositiveMethodName != null) {
					try {
						// Invoking given method with NO variables on the activity which called this dialog
						sClass.getMethod(sPositiveMethodName, null).invoke(getActivity(), new Class[] {});
						
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}

		}).setNegativeButton(R.string.dialog_cancel, null);

		return builder.show();
	}
	
	@Override
	public void dismiss() {
		// Reset variables
		sTitle = null;
		sMessage = null;
		sPositiveMethodName = null;
		super.dismiss();
	}
}
