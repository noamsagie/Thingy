package com.android.views;

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
				// Valid data
				Set fatherSet = new Set();
				fatherSet.setName("BestFatherEver");
				
				Set moshe = new Set();
				moshe.setName("Moshe");
				moshe.setComment("hi");
				moshe.setSound("sound1");
				moshe.setRepetitions(4);
				moshe.setEndless(false);
				fatherSet.getElements().add(moshe);
				
				Rest rest1 = new Rest();
				rest1.setName("resty rest");
				rest1.setComment("fuck you");
				rest1.setSound("sound2");
				rest1.setTime(20.00);
				moshe.getElements().add(rest1);
				
				Set set2 = new Set();
				set2.setName("lol");
				set2.setSound("sound1");
				moshe.setEndless(true);
				fatherSet.getElements().add(set2);
				
				RepetitionExercise repEx = new RepetitionExercise();
				repEx.setName("name");
				repEx.setComment("none");
				repEx.setSound("sound3");
				set2.getElements().add(repEx);
				
				TimeExercise timeEx = new TimeExercise();
				timeEx.setName("hurry!");
				timeEx.setComment("time is money");
				timeEx.setSound("sound5");
				timeEx.setTime(35.00);
				set2.getElements().add(timeEx);
				
				// WRITING FILE
					try {
						XMLWorkoutWriter.WriteFile(fatherSet, openFileOutput(fatherSet.getName() + ".xml", Context.MODE_APPEND));
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
			}
		});
	}
}
