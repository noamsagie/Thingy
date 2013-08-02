package com.android.element;

import java.util.ArrayList;

public class Set extends AElement {
	private int m_repetitions = 0;
	private boolean m_isEndless;
	private ArrayList<AElement> m_elements = new ArrayList<AElement>();

	public int getRepetitions() {
		return m_repetitions;
	}

	public void setRepetitions(int repetitions) {
		this.m_repetitions = repetitions;
	}

	public ArrayList<AElement> getElements() {
		return m_elements;
	}

	public void setElements(ArrayList<AElement> elements) {
		this.m_elements = elements;
	}
	
	public boolean getEndless() {
		return m_isEndless;
	}

	public void setEndless(boolean isEndless) {
		this.m_isEndless = isEndless;
	}
}
