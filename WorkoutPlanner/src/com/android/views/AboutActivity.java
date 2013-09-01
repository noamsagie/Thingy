package com.android.views;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AboutActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		// Setting up basic activity requirements
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// Get help button from the view
		Button btHelp = (Button) findViewById(R.id.btHelp);

		// Set the event that would occur on click
		btHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Open a site that would have tutorials for this app
			}
		});
	}
}
