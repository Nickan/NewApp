package com.nickan.newapp;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener, 
	FeedFragment.OnFeedFragmentListener, SplashFragment.OnSessionOpenStateListener {
	private static final String TAG = "MainActivity";

	// Swipe implementation
	private ViewPager viewPager;
	private FragPagerAdapter fragPagerAdapter = null;
	
	SplashFragment splashFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		splashFragment = new SplashFragment();
		
		setContentView(R.layout.main);
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.main, new SplashFragment());
		ft.commit();
	}
	
	private void createViewPager() {
		Log.e(TAG, "createViewPager()");
		
		final ActionBar actionBar = getActionBar();
		actionBar.show();
		actionBar.removeAllTabs();
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
	public void onPostSelected(String comments) {
		Log.e(TAG, "Post Selected");
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		Bundle bundle = new Bundle();
		bundle.putString(FeedFragment.COMMENTS, comments);
		CommentFragment commentFragment = new CommentFragment();
		commentFragment.setArguments(bundle);
		ft.replace(R.id.main, commentFragment);
		ft.commit();
		
		final ActionBar actionBar = getActionBar();
		actionBar.hide();
		viewPager.setEnabled(false);
		viewPager.setVisibility(ViewPager.GONE);
	}
	
	@Override
	public void onBackPressed() {
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		List<Fragment> fragments = fm.getFragments();
		for (Fragment tmpFrag : fragments) {
			if (tmpFrag instanceof CommentFragment) {
				ft.remove(tmpFrag);
				ft.commit();
				
				viewPager.setEnabled(true);
				viewPager.setVisibility(ViewPager.VISIBLE);
				final ActionBar actionBar = getActionBar();
				actionBar.show();
				return;
			}
		}
		
	//	super.onBackPressed();
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
		Log.e(TAG, "Tab Selected");
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

