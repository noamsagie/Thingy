package com.android.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.element.Set;
import java.util.List;

public class GaysSetAdapter extends ArrayAdapter<Set> {

	public GaysSetAdapter(Context context, int textViewResourceId,
			List<Set> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Getting view somehow...
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View inflatedView = inflater.inflate(R.layout.gays_element_set, parent,
				false);

	//	We are inflating textView. we want to get every one of the elements its xml defines, and populate them with relevant data.
//		TextView textView = (TextView) inflatedView.findViewById(R.id.editText1);
//		textView.setText("bullshit");
//		TextView textView2 = (TextView) inflatedView.findViewById(R.id.editText2);
//		textView2.setText("other stuff");
//		//TextView textView3 = (TextView) rowView.findViewById(R.id.editText3);
		return inflatedView;
	}
}
