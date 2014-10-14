package com.nickan.newapp;


import com.facebook.Session;
import com.nickan.newapp.util.ViewUtil;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

public class MainActivity extends FragmentActivity implements FeedFragment.OnFeedFragmentListener, 
		SplashFragment.OnSessionOpenStateListener {
	private static final String TAG = "MainActivity";
	
	private Fragment splashFragment;
	private Fragment mainFragment;
	private Fragment messengerFragment;
	private Fragment commentFragment;
	private boolean messengerIsShown = false;
	private LinearLayout viewPagerParentLayout;
	
	private boolean portraitMode = true;
	float messengerSlideX;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtil.activity = this;
		ViewUtil.dm = getResources().getDisplayMetrics();
		
		splashFragment = new SplashFragment();
		
		setContentView(R.layout.main_activity_layout);
		
		splashFragment = new SplashFragment();
		mainFragment = new MainFragment();
		messengerFragment = new MessengerFragment();
		commentFragment = new CommentFragment();
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		ft.add(R.id.main, splashFragment);
		ft.add(R.id.main, messengerFragment);
		ft.add(R.id.main, mainFragment);
		ft.add(R.id.main, commentFragment);
		ft.commit();
		
		fm = getSupportFragmentManager();
		ft = fm.beginTransaction();
		ft.hide(splashFragment);
		ft.hide(mainFragment);
		ft.hide(messengerFragment);
		ft.hide(commentFragment);
		ft.commit();
		
		
		fm = getSupportFragmentManager();
		ft = fm.beginTransaction();
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			ft.show(mainFragment);
		} else {
			ft.show(splashFragment);
		}
		ft.commit();
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		messengerSlideX = -width * 0.75f;
	}

	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		Log.e(TAG, "Width: " + width);
		
		messengerSlideX = -width * 0.75f;
		super.onConfigurationChanged(newConfig);
	}
	
	private void createViewPager() {
		Log.e(TAG, "createViewPager()");
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		// Adding the messenger fragment;
		ft.hide(splashFragment);
		ft.hide(commentFragment);
		ft.hide(messengerFragment);
		ft.show(mainFragment);
		ft.addToBackStack(null);
		ft.commit();
	}
	
	@Override
	public void onBackPressed() {
		((MainFragment) mainFragment).onBackPressed();
	//	super.onBackPressed();
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
	//	fm.popBackStack();
	//	ft.commit();
		
		if (!messengerIsShown) {
			ft.hide(commentFragment);
			ft.hide(messengerFragment);
			ft.hide(splashFragment);
			ft.show(mainFragment);
			ft.commit();
		}
		
		/*
		if (mainFragment.isVisible()) {
			Log.e(TAG, "Main Fragment is visible");
		} else {
			Log.e(TAG, "Main Fragment is not visible");
		}
		*/
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_action, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_search:
				return true;
			case R.id.action_messenger:
				showMessengerFragment();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void showMessengerFragment() {
		Log.e(TAG, "Show messenger is clicked");
		
		if (!messengerFragment.isVisible()) {
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			
			ft.show(messengerFragment);
			ft.show(mainFragment);
			ft.commit();
			Log.e(TAG, "Messenger show");
		}
		
		viewPagerParentLayout = (LinearLayout) mainFragment.getView().findViewById(R.id.view_pager_parent_layout);
		
		
		
		// {{ Fields for animating the Layout of the ViewPage scrolling left
		if (messengerIsShown) {
			TranslateAnimation animation = new TranslateAnimation(messengerSlideX, 0, 0, 0);
			animation.setDuration(500);
			animation.setFillAfter(true);
			viewPagerParentLayout.startAnimation(animation);
		} else {
			
			TranslateAnimation animation = new TranslateAnimation(0, messengerSlideX, 0, 0);
			animation.setDuration(500);
			animation.setFillAfter(true);
			viewPagerParentLayout.startAnimation(animation);
		}
		
		messengerIsShown = !messengerIsShown;
		// }}
	}

	@Override
	public void onSessionOpen() {
		createViewPager();
	}
	
	@Override
	public void onPostSelected(String comments) {
		
		Log.e(TAG, "Post Selected");
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		((CommentFragment) commentFragment).showComment(comments);
		ft.hide(mainFragment);
		ft.show(messengerFragment);
		ft.hide(splashFragment);
		ft.show(commentFragment);
	//	ft.addToBackStack("Test");
		ft.commit();
		
		((MainFragment) mainFragment).onPostSelected();
		
	}
	
}

