package com.android.element;

import com.android.global.Consts;

import java.util.ArrayList;

public class Exercise extends AElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3008889828323557540L;

	private int mSets = Consts.DEFAULT_SET_VALUE;
	private ArrayList<AElement> mElements = new ArrayList<AElement>();

	public int getSets() {
		return mSets;
	}

	public void setSets(int sets) {
		this.mSets = sets;
	}

	public ArrayList<AElement> getElements() {
		return mElements;
	}

	public void setElements(ArrayList<AElement> elements) {
		this.mElements = elements;
	}

	public Exercise(int id) {
		setId(id);
		
		// Default sound
		setSound(Consts.DEFAULT_SOUND_EXERCISE);
	}

	// Copy constructor
	public Exercise(Exercise original) {
		setComment(original.getComment());
		setId(original.getId());
		setName(original.getName());
		setSound(original.getSound());
		setSets(original.getSets());

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
