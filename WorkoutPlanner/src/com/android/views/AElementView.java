package com.android.views;

import android.view.ViewGroup;

import android.content.Context;

import com.android.element.AElement;

import android.view.LayoutInflater;
import android.view.View;

public abstract class AElementView {
	public AElement mElement;
	protected Context mContext;
	
	public abstract int getViewType();

	public abstract View getView(LayoutInflater inflater, ViewGroup parent, View convertView, int position);
	
	public AElementView(Context context) {
		mContext = context;
	}
}
