package com.android.views;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.android.element.Set;

public class PreviewItemHolder {

	// views for optimization
	TextView nameLabel;
	TextView repetitionsLabel;
	EditText commentInput;
	EditText soundInput;
	CheckBox endlessInput;
	View expand;
	View collapse;
	View expandArea;

	// Other states
	Set set;
}
