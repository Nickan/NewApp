package com.nickan.newapp;

import java.util.List;

import com.nickan.newapp.util.IdCreator;
import com.nickan.newapp.util.ViewUtil;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MainActivity extends FragmentActivity implements FeedFragment.OnFeedFragmentListener, 
		SplashFragment.OnSessionOpenStateListener {
	private static final String TAG = "MainActivity";

	// Swipe implementation
	private FragPagerAdapter fragPagerAdapter = null;
	
	SplashFragment splashFragment;
	private MainFragment mainFragment;
	private MessengerFragment messengerFragment;
	private boolean messengerIsShown = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		splashFragment = new SplashFragment();
		
		setContentView(R.layout.main_activity_layout);
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.main, new SplashFragment());
		ft.commit();
	}
	
	private void createViewPager() {
		Log.e(TAG, "createViewPager()");
		mainFragment = new MainFragment();
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		// Adding the messenger fragment;
		messengerFragment = new MessengerFragment();
		ft.add(R.id.main, messengerFragment);
		ft.hide(messengerFragment);
		
		ft.replace(R.id.main, mainFragment);
		ft.commit();
	}
	
	@Override
	public void onBackPressed() {
	//	super.onBackPressed();
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		ft.show(mainFragment);
		ft.hide(commentFragment);
		ft.commit();
		mainFragment.onBackPressed();
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
		// {{ Fields for animating the Layout of the ViewPage scrolling left
		LinearLayout viewPager = (LinearLayout) findViewById(R.id.view_pager_parent_layout);
		
		float moveX = -350;
		if (messengerIsShown) {
			TranslateAnimation animation = new TranslateAnimation(moveX, 0, 0, 0);
			animation.setDuration(500);
			animation.setFillAfter(true);
			viewPager.startAnimation(animation);
		} else {
			TranslateAnimation animation = new TranslateAnimation(0, moveX, 0, 0);
			animation.setDuration(500);
			animation.setFillAfter(true);
			viewPager.startAnimation(animation);
		}
		
		messengerIsShown = !messengerIsShown;
		// }}
	}

	@Override
	public void onSessionOpen() {
		createViewPager();
	}
	
	CommentFragment commentFragment;
	@Override
	public void onPostSelected(String comments) {
		Log.e(TAG, "Post Selected");
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		Bundle bundle = new Bundle();
		bundle.putString(FeedFragment.COMMENTS, comments);
		commentFragment = new CommentFragment();
		commentFragment.setArguments(bundle);

		ft.replace(R.id.main, commentFragment);
		ft.hide(mainFragment);
		ft.hide(messengerFragment);
		ft.addToBackStack(null);
		ft.commit();
		
		mainFragment.onPostSelected();
	}
	
}

