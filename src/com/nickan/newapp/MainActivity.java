package com.nickan.newapp;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.facebook.Session;
import com.nickan.newapp.util.ViewUtil;

public class MainActivity extends FragmentActivity implements FeedFragment.OnFeedFragmentListener, 
		SplashFragment.OnSessionOpenStateListener {
	private static final String TAG = "MainActivity";
	
	private Fragment splashFragment;
	private Fragment mainFragment;
	private Fragment messengerFragment;
	private Fragment commentFragment;
	private boolean messengerIsShown = false;
	private boolean drawerItemIsShown = false;
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
		
		initializeNavigationDrawer();
	}
	
	// {{ Navigation Drawer fields
	private String[] drawerTitles;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private String drawerTitleClicked;
	private ActionBarDrawerToggle drawerToggle;
	// }}
	
	// {{ Navigation Drawer methods
	private void initializeNavigationDrawer() {
		drawerTitles = getResources().getStringArray(R.array.drawer_titles);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.list_drawer);
		
		drawerList.setAdapter(new ArrayAdapter<String>(this, 
				R.layout.drawer_list_item, drawerTitles));
		drawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 
				R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle(drawerTitleClicked);
				invalidateOptionsMenu();
			}
			
			public void onDrawerOpened(View view) {
				super.onDrawerOpened(view);
				getActionBar().setTitle(drawerTitleClicked);
				invalidateOptionsMenu();
			}
			
		};
		
		drawerLayout.setDrawerListener(drawerToggle);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
		menu.findItem(R.id.action_search).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
		
	}
	
	private void selectItem(int position) {
		Fragment drawerFragment = new DrawerTitleFragment();
		Bundle args = new Bundle();
		args.putInt(DrawerTitleFragment.DRAWER_TITLE_NUMBER, position);
		
		drawerFragment.setArguments(args);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.main, drawerFragment);
		ft.addToBackStack(null);
		ft.commit();
		
		drawerItemIsShown = true;
		
		drawerList.setItemChecked(position, true);
	//	setTitle(drawerTitleClicked);
		drawerLayout.closeDrawer(drawerList);
	}
	
	@Override
	public void setTitle(CharSequence title) {
		drawerTitleClicked = title.toString();
		getActionBar().setTitle(drawerTitleClicked);
	}
	
	// }}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
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
		
		if (!messengerIsShown && !drawerItemIsShown) {
			ft.hide(commentFragment);
			ft.hide(messengerFragment);
			ft.hide(splashFragment);
			ft.show(mainFragment);
			ft.commit();
			
			Log.e(TAG, "Show Main Fragment");
		}
		
		if (drawerItemIsShown) {
			drawerItemIsShown = false;
			super.onBackPressed();
		}

		if (mainFragment.isVisible()) {
			Log.e(TAG, "Main Fragment is visible");
			
		} else {
			Log.e(TAG, "Main Fragment is not visible");
		}
		
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
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		messengerSlideX = -width * 0.75f;
		
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
		AnimatorSet aniSet = new AnimatorSet();
		if (messengerIsShown) {
			Log.e(TAG, "Messenger is Open");
			
			ObjectAnimator aniObjTranslation = ObjectAnimator.ofFloat(viewPagerParentLayout, "translationX", messengerSlideX, 0);
			ObjectAnimator aniObjAlpha = ObjectAnimator.ofFloat(viewPagerParentLayout, "alpha", 0.5f, 1.0f);
			aniObjTranslation.setDuration(500);
			aniObjAlpha.setDuration(500);
			aniSet.playTogether(aniObjTranslation, aniObjAlpha);
			aniSet.start();

		} else {
			Log.e(TAG, "Messenger is closed");

			ObjectAnimator aniObjTranslation = ObjectAnimator.ofFloat(viewPagerParentLayout, "translationX", 0, messengerSlideX);
			ObjectAnimator aniObjAlpha = ObjectAnimator.ofFloat(viewPagerParentLayout, "alpha", 1.0f, 0.5f);
			aniObjTranslation.setDuration(500);
			aniObjAlpha.setDuration(500);
			aniSet.playTogether(aniObjTranslation, aniObjAlpha);
			aniSet.start();
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

