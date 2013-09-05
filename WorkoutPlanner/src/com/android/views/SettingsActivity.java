package com.android.views;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.android.global.Consts;

public class SettingsActivity extends Activity {

	private final int MAX_OFFSET_VALUE = 5;
	SharedPreferences mShared;

	// TODO Change style of this activity. Should look a lot leaner and hotter, like those settings in google apps
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		// Setting up basic activity requirements
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		mShared = getSharedPreferences(Consts.PREFS_FILE_TAG, 0);

		SeekBar restOffset = (SeekBar) findViewById(R.id.restOffset);
		final TextView offsetSeconds = (TextView) findViewById(R.id.offsetSeconds);

		// Set seconds at first time
		offsetSeconds.setText(Integer.toString(mShared.getInt(Consts.REST_OFFSET_TAG, 0)));

		// Set bar max value and value for first time
		restOffset.setMax(MAX_OFFSET_VALUE);
		restOffset.setProgress(mShared.getInt(Consts.REST_OFFSET_TAG, 0));

		restOffset.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				if (fromUser) {
					SharedPreferences.Editor edit = mShared.edit();

					// Save offset value
					edit.putInt(Consts.REST_OFFSET_TAG, progress);
					edit.commit();

					// Show it
					offsetSeconds.setText(Integer.toString(progress));
				}
			}
		});
	}
}
