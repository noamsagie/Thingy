package com.android.element;

import java.util.ArrayList;

public class Set extends AElement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3008889828323557540L;
	
	private int mRepetitions = 0;
	private boolean mIsEndless;
	private ArrayList<AElement> mElements = new ArrayList<AElement>();

	public int getRepetitions() {
		return mRepetitions;
	}

	public void setRepetitions(int repetitions) {
		this.mRepetitions = repetitions;
	}

	public ArrayList<AElement> getElements() {
		return mElements;
	}

	public void setElements(ArrayList<AElement> elements) {
		this.mElements = elements;
	}
	
	public boolean getEndless() {
		return mIsEndless;
	}

	public void setEndless(boolean isEndless) {
		this.mIsEndless = isEndless;
	}
}
