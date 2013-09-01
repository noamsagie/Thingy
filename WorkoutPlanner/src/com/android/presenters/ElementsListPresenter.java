package com.android.presenters;

import com.android.views.ElementsListActivity;

public class ElementsListPresenter extends APresenter {

	private ElementsListActivity mCurrentView;
	
	public ElementsListPresenter(ElementsListActivity context) {
		// A presenter must hold a reference to its view
		mCurrentView = context;
	}
}
