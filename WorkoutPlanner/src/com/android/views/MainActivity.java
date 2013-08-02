package com.android.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		// TODO Complete
	}
	
	
	public void aboutPressed(View view) {
    	Toast.makeText(this, "shit", Toast.LENGTH_SHORT).show();
    	Intent i = new Intent(getApplicationContext(), AboutActivity.class);
    	startActivity(i);
	}
	
	
	public void gaysPressed(View view) {
    	Toast.makeText(this, "crap", Toast.LENGTH_SHORT).show();
    	Intent i = new Intent(getApplicationContext(), GaysActivity.class);
    	startActivity(i);
	}
}
