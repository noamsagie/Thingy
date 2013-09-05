package com.android.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.android.element.Exercise;
import com.android.global.Consts.elementTypes;
import java.util.ArrayList;

public class ElementsListAdapter extends ArrayAdapter<AElementView> {

	private Exercise mFather;
	private ArrayList<AElementView> mElementViews;
	static AElementView sUndoElementView;

	public ElementsListAdapter(Context context, Exercise father, ArrayList<AElementView> elements) {
		super(context, 0, elements);
		mFather = father;
		mElementViews = elements;
	}

	@Override
	public int getViewTypeCount() {
		return elementTypes.values().length;
	}

	@Override
	public int getItemViewType(int position) {
		// Use getViewType from the Item interface
		return mElementViews.get(position).getViewType();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Use getView from the Item interface
		return mElementViews.get(position).getView((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE), parent, convertView, position);
	}

	public void resetIds() {
		// Reset Id to match position if position has changed
		for (int i = 0; i < mElementViews.size(); i++) {
			mElementViews.get(i).mElement.setId(i);
		}
	}
}
