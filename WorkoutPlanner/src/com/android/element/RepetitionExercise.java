package com.android.element;

import com.android.global.Consts;

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
	private ArrayList<Boolean> mEndlessSets = new ArrayList<Boolean>();

	public ArrayList<Boolean> getEndlessSets() {
		return mEndlessSets;
	}

	public void setEndlessSets(ArrayList<Boolean> endlessSets) {
		this.mEndlessSets = endlessSets;
	}

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
		// Default sound
		setSound(Consts.DEFAULT_SOUND_REPETITION_EXERCISE);
	}

	// Initialize reps and weight list sizes by the size of the Exercise sets
	// value
	public RepetitionExercise(int setSets) {
		this();
		int length = setSets - mReps.size();

		for (int i = 0; i < length; i++) {
			// Adding values to the list because the list size is smaller than
			// the sets value of the set
			mReps.add(0);
			mWeights.add(0.0);
			mEndlessSets.add(false);
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
			mEndlessSets.add(original.getEndlessSets().get(i));
		}
	}
}
