package com.nickan.newapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class FragPagerAdapter extends FragmentPagerAdapter {
	private FeedFragment feedFragment;
	private FriendRequestsFragment friendRequestsFragment;

	public FragPagerAdapter(FragmentManager fm) {
		super(fm);
		feedFragment = new FeedFragment();
		friendRequestsFragment = new FriendRequestsFragment();
	}

	@Override
	public Fragment getItem(int index) {
		switch (index) {
		case 0:
			return feedFragment;
		case 1:
			return friendRequestsFragment;
		default:
			return friendRequestsFragment;	// <-- To be changed later
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
