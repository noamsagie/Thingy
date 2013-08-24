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

	public Set(int id) {
		setId(id);
	}

	// Copy constructor
	public Set(Set original) {
		setComment(original.getComment());
		setId(original.getId());
		setName(original.getName());
		setSound(original.getSound());
		setRepetitions(original.getRepetitions());
		setEndless(original.getEndless());

		ArrayList<AElement> clones = new ArrayList<AElement>();

		// Cloning elements
		for (AElement element : original.getElements()) {
			if (element instanceof RepetitionExercise) {
				clones.add(new RepetitionExercise((RepetitionExercise) element));
			}
			else if (element instanceof Rest) {
				clones.add(new Rest((Rest) element));
			}
			else if (element instanceof TimeExercise) {
				clones.add(new TimeExercise((TimeExercise) element));
			}
		}
		
		setElements(clones);
	}
}
