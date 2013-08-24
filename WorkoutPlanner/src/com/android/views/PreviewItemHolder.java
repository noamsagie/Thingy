package com.android.views;

import android.widget.ImageView;

import com.android.element.AElement;

import java.util.ArrayList;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.android.element.Set;

public class PreviewItemHolder {
	
	public PreviewItemHolder() {}
	
	// Copy constructor
	public PreviewItemHolder(PreviewItemHolder original) {
		// TODO FIND A WAY TO CLONE ALL THOSE SHITTY VIEWS! PARSE THEM OR SOMETHING
	}

	// views for optimization
	TextView nameLabel;
	TextView repetitionsLabel;
	EditText commentInput;
	EditText soundInput;
	CheckBox endlessInput;
	View expand;
	View collapse;
	View expandArea;
	ArrayList<PreviewElementHolder> previewElementHolders;
	Button addElementButton;
	ImageView dragHandler;

	// Other states
	Set set;
	
	// Flags for views
	boolean rePopulateFlag = false;
	boolean updateFlag = false;
	boolean minimizeFlag = false;
	
	boolean readMinimizeFlag() {
		boolean result = minimizeFlag;
		minimizeFlag = false;
		
		return result;
		}
	
	// Read flag value and reset it
	boolean readPopulateFlag() {
		boolean result = rePopulateFlag;
		rePopulateFlag = false;
		
		return result;
	}
	
	// Read flag value and reset it
	boolean readUpdateFlag() {
		boolean result = updateFlag;
		updateFlag = false;
		
		// Turning pop flag to true if inflate flag is true. This is because a newly inflated view must be popped.
		// If the logic changes in some way that these flags don't come one after the other in this order, remove this line!!!
		if (result) {
			rePopulateFlag = true;
		}
		
		return result;
	}
}

class PreviewElementHolder {
	TextView nameLabel;
	EditText soundInput;
	EditText commentInput;
	
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
}
