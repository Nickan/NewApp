package com.nickan.newapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DrawerTitleFragment extends Fragment {
	public static final String DRAWER_TITLE_NUMBER = "drawer_number";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_drawer_title, container, false);
		int i = getArguments().getInt(DRAWER_TITLE_NUMBER);
		String drawerTitle = getResources().getStringArray(R.array.drawer_titles)[i];
		
		TextView clickedDrawerTitle = (TextView) rootView.findViewById(R.id.textview_drawer_title);
		clickedDrawerTitle.setText(drawerTitle);
		
		return rootView;
	}
	
}
