package com.android.element;

import java.util.ArrayList;

public class RepetitionExercise extends AExercise {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9217680970832532040L;

	// TODO Add a comment line or otherwise a modified section of data inputs
	// that would allow the user to enter the weight and/or reps of each set.
	// Since this sort of data is crucial to know, the GUI for this should be
	// intuitive and easy to use
	private ArrayList<Integer> mReps = new ArrayList<Integer>();
	private ArrayList<Double> mWeights = new ArrayList<Double>();

	public ArrayList<Integer> getReps() {
		return mReps;
	}

	public void setReps(ArrayList<Integer> reps) {
		this.mReps = reps;
	}

	public ArrayList<Double> getWeights() {
		return mWeights;
	}

	public void setWeight(ArrayList<Double> weights) {
		this.mWeights = weights;
	}

	public RepetitionExercise() {
	}

	// Initialize reps and weight list sizes by the size of the set repetition
	// value
	public RepetitionExercise(int setRepetitions) {
		int length = setRepetitions - mReps.size();

		for (int i = 0; i < length; i++) {
			// Adding values to the list because the list size is smaller than
			// the repetition value of the set
			mReps.add(0);
			mWeights.add(0.0);
		}
	}
	
	// Copy constructor
	public RepetitionExercise(RepetitionExercise original) {
		setComment(original.getComment());
		setId(original.getId());
		setName(original.getName());
		setSound(original.getSound());
		
		for (int i = 0; i < original.getReps().size(); i++) {
			// Cloning arrays
			mReps.add(original.getReps().get(i));
			mWeights.add(original.getWeights().get(i));
		}
	}
}
