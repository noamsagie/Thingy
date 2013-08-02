package com.android.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.android.element.Set;
import java.util.ArrayList;

public class GaysActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gays);
		ListView mainListView = (ListView) findViewById(R.id.items_list);
		mainListView.setSmoothScrollbarEnabled(true);
		mainListView.setScrollingCacheEnabled(true);
		
		ArrayList<Set> setList = new ArrayList<Set>();
		setList.add(new Set());
		setList.add(new Set());
		setList.add(new Set());
		setList.add(new Set());
		setList.add(new Set());
		setList.add(new Set());
		setList.add(new Set());
		setList.add(new Set());
		setList.add(new Set());
		
		// Create and populate a List of planet names.
		GaysSetAdapter gaysSetAdapter = new GaysSetAdapter(this, R.layout.gays_element_set, setList);
		mainListView.setAdapter(gaysSetAdapter);
		
	}
	
	public void backToCurrentPressed(View view) {
    	Toast.makeText(this, "backToCurrentPressed", Toast.LENGTH_SHORT).show();
    	//TODO get back to focused item.
	}
}
