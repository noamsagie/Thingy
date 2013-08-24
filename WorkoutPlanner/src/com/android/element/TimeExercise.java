package com.android.element;

public class TimeExercise extends AExercise {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4472892371114303578L;
	
	private double mTime = 0;

	public double getTime() {
		return mTime;
	}

	public void setTime(double time) {
		this.mTime = time;
	}
	
	public TimeExercise() {}
	
	public TimeExercise(TimeExercise original) {
		setComment(original.getComment());
		setId(original.getId());
		setName(original.getName());
		setSound(original.getSound());
		setTime(original.getTime());
	}
	
	// TODO Might wanna change that data member to an array and allow the user to set different time values for each set in an exercise
}
