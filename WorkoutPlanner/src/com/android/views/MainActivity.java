package com.android.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// TODO Complete
	}

	public void selectWorkoutPressed(View view) {
		startActivity(new Intent(view.getContext(), SelectWorkoutActivity.class));
	}

	public void aboutPressed(View view) {
		Intent i = new Intent(getApplicationContext(), AboutActivity.class);
		startActivity(i);
	}
}
