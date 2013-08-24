package com.android.element;

public class Rest extends AElement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3597047023990402265L;
	
	private double mTime = 0;

	public double getTime() {
		return mTime;
	}

	public void setTime(double time) {
		this.mTime = time;
	}
	
	public Rest() {}
	
	public Rest(Rest original) {
		setComment(original.getComment());
		setId(original.getId());
		setName(original.getName());
		setSound(original.getSound());
		setTime(original.getTime());
	}
}
