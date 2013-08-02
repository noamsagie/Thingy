package com.android.element;

public abstract class AElement {
	private String m_name = "";
	private String m_comment = "";
	private String m_sound = "";
	private int m_id = 0;

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		this.m_name = name;
	}

	public String getComment() {
		return m_comment;
	}

	public void setComment(String comment) {
		this.m_comment = comment;
	}

	public String getSound() {
		return m_sound;
	}

	public void setSound(String sound) {
		this.m_sound = sound;
	}

	public int getId() {
		return m_id;
	}

	public void setId(int id) {
		this.m_id = id;
	}
}
