package com.android.presenters;

import android.widget.Toast;
import com.android.global.Consts;
import com.android.views.ElementsListActivity;
import com.android.views.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ElementsListPresenter extends APresenter {

	private ElementsListActivity mCurrentView;

	public ElementsListPresenter(ElementsListActivity context) {
		// A presenter must hold a reference to its view
		mCurrentView = context;
	}

	public Collection<? extends String> loadSounds() {
		List<String> result = new ArrayList<String>();

		try {
			// Retrieve sounds file names from folder
			result = Arrays.asList(mCurrentView.getAssets().list(Consts.ASSETS_SOUNDS));

			// Remove extension
			for (int i = 0; i < result.size(); i++) {
				result.set(i, result.get(i).substring(0, result.get(i).length() - Consts.SOUND_FILE_EXTENSION.length()));
			}
		} catch (IOException e) {
			// TODO Write to log
			Toast.makeText(mCurrentView, R.string.error_sounds, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

		return result;
	}
}
