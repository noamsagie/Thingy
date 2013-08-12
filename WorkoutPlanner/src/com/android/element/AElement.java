package com.android.element;

import java.io.Serializable;

public abstract class AElement implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6355546541938822573L;
	private String mName = "";
	private String mComment = "";
	private String mSound = "";
	private int mId = 0;

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getComment() {
		return mComment;
	}

	public void setComment(String comment) {
		this.mComment = comment;
	}

	public String getSound() {
		return mSound;
	}

	public void setSound(String sound) {
		this.mSound = sound;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}
}
