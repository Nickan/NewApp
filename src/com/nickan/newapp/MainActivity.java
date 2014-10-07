package com.nickan.newapp;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener, 
	FeedFragment.OnFeedFragmentListener, SplashFragment.OnSessionOpenStateListener {
	private static final String TAG = "MainActivity";

	// Swipe implementation
	private ViewPager viewPager;
	private FragPagerAdapter fragPagerAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SplashFragment splashFragment = new SplashFragment();
		
		setContentView(R.layout.main);
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.main, new SplashFragment());
		ft.addToBackStack(null);
		ft.commit();
	}
	
	private void createViewPager() {
		Log.e(TAG, "createViewPager()");
		
		setContentView(R.layout.view_pager);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		fragPagerAdapter = new FragPagerAdapter(getSupportFragmentManager());
		
//		viewPager = new ViewPager(this);
//		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		viewPager.setLayoutParams(params);
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
	public void onPostSelected(ArrayList<String> comments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSessionOpen() {
		createViewPager();
	}

	
	
}

