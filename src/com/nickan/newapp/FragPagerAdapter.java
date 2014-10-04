package com.nickan.newapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragPagerAdapter extends FragmentPagerAdapter {

	public FragPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int index) {
		switch (index) {
		case 0:
			return new FeedFragment();
		case 1:
			return new CommentFragment();
		default:
			return new CommentFragment();	// <-- To be changed later
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return "Fragment " + position;
	}

}
