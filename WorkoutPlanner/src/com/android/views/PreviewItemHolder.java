package com.android.views;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.element.AElement;
import com.android.element.Exercise;
import java.util.ArrayList;

public class PreviewItemHolder {

	public PreviewItemHolder() {
	}

	// Copy constructor
	public PreviewItemHolder(PreviewItemHolder original) {
		// TODO FIND A WAY TO CLONE ALL THOSE SHITTY VIEWS! PARSE THEM OR
		// SOMETHING
	}

	// views for optimization
	TextView nameLabel;
	TextView setsLabel;
	EditText commentInput;
	Spinner soundInput;
	View expand;
	View collapse;
	View expandArea;
	LinearLayout data;
	Button editElementButton;
	ImageView dragHandler;

	// Other states
	Exercise exercise;
}

class PreviewElementHolder {
	TextView nameLabel;
	Spinner soundInput;
	EditText commentInput;
	ImageView dragHandler;
	LinearLayout data;

	// Other states
	AElement element;
}

class PreviewRestHolder extends PreviewElementHolder {
	TextView timeLabel;
}

class PreviewTimeExerciseHolder extends PreviewElementHolder {
	TextView timeLabel;
}

class PreviewRepetitionExerciseHolder extends PreviewElementHolder {
	ArrayList<TextView> repsValueList = new ArrayList<TextView>();
	ArrayList<TextView> weightsValueList = new ArrayList<TextView>();
	ArrayList<CheckBox> endlessSetList = new ArrayList<CheckBox>();
}
