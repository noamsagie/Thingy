package com.android.views;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import com.android.element.Exercise;
import com.android.global.Consts;
import com.android.presenters.PlayPresenter;

public class PlayActivity extends Activity {
	
	public Exercise mWorkout;
	PlayPresenter mPresenter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setting up basic activity requirements
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_play);
		
		
		// Load the workout data into the father set
		if (getIntent().getExtras() == null) {
			mPresenter.loadWorkoutData(getIntent().getExtras().getString(Consts.SELECT_WORKOUT_TAG));
		}
		
		// Load rest offset
		SharedPreferences shared = getSharedPreferences(Consts.PREFS_FILE_TAG, 0);
		int restOffset = shared.getInt(Consts.REST_OFFSET_TAG, 0);
	}
}
