package com.android.views;

import com.android.global.Consts;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.android.dal.XMLWorkoutWriter;
import com.android.element.RepetitionExercise;
import com.android.element.Rest;
import com.android.element.Set;
import com.android.element.TimeExercise;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

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

				// TEMP - DEBUGGING
				Set fatherSet = new Set(-1);
				fatherSet.setName("Empty");

				// Remove existing file if there is
				v.getContext().deleteFile(fatherSet.getName() + Consts.FILE_EXTENSION);

				// WRITING FILE
				try {
					XMLWorkoutWriter.WriteFile(fatherSet, openFileOutput(fatherSet.getName() + Consts.FILE_EXTENSION, Context.MODE_APPEND));
				} catch (IllegalArgumentException e1) {
					Toast.makeText(v.getContext(), "Error writing file. Writing to log...", Toast.LENGTH_SHORT).show();
					// TODO Write to log
					e1.printStackTrace();
				} catch (IllegalStateException e1) {
					Toast.makeText(v.getContext(), "Error writing file. Writing to log...", Toast.LENGTH_SHORT).show();
					// TODO Write to log
					e1.printStackTrace();
				} catch (IOException e1) {
					Toast.makeText(v.getContext(), "Error writing file. Writing to log...", Toast.LENGTH_SHORT).show();
					// TODO Write to log
					e1.printStackTrace();
				} catch (XmlPullParserException e1) {
					Toast.makeText(v.getContext(), "Error writing file. Writing to log...", Toast.LENGTH_SHORT).show();
					// TODO Write to log
					e1.printStackTrace();
				}

				LoadDialog a = new LoadDialog();
				a.show(getFragmentManager(), null);
				// TEMP - DEBUGGING
			}
		});
	}
}
