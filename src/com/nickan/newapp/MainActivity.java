package com.nickan.newapp;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener, FeedFragment.OnFeedFragmentListener {
	private static final String TAG = "MainActivity";

	// Swipe implementation
	private ViewPager viewPager;
	private FragPagerAdapter fragPagerAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		fragPagerAdapter = new FragPagerAdapter(getSupportFragmentManager());
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(fragPagerAdapter);
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
				Log.e(TAG, "onPageSelected() " + position);
			}
		});
		
		for (int index = 0; index < fragPagerAdapter.getCount(); ++index) {
			actionBar.addTab(actionBar.newTab()
					.setText(fragPagerAdapter.getPageTitle(index))
					.setTabListener(this)
					);
		}
	
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void onPostSelected(ArrayList<String> comments) {
		// TODO Auto-generated method stub
		
	}
	
	
}

