package com.nickan.newapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import java.util.List;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class MainActivity extends FragmentActivity implements UserProfileFragment.OnUserProfileSelectedListener {
	private static final String TAG = "MainActivity";
	
	private static final int SPLASH_FRAGMENT = 0;
	private static final int USER_PROFILE_FRAGMENT = 1;
	private static final int COMMENT_FRAGMENT = 2;
	private static final int SETTINGS_FRAGMENT = 3;
	
	private int currentFragment = -1;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (state != null && state.isOpened()) {
				onSessionStateChange(session, state, exception);
				Log.e(TAG, "call");
			}
		}
	};
	private UiLifecycleHelper uiHelper;
	
	private MenuItem settings;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		if (savedInstanceState == null) {
			uiHelper = new UiLifecycleHelper(this, callback);
			uiHelper.onCreate(savedInstanceState);
			
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			
			ft.add(R.id.container, new SplashFragment());
			currentFragment = SPLASH_FRAGMENT;
			ft.commit();
		}
	
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_action, menu);
	    MenuItem item = menu.findItem(R.id.action_search);
	    return super.onCreateOptionsMenu(menu);
	}

	
	private void getHashKey() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"com.nickan.newapp",
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}
	}

	
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
			if (currentFragment == SPLASH_FRAGMENT) {
				currentFragment = USER_PROFILE_FRAGMENT;
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				
				ft.replace(R.id.container, new UserProfileFragment());
				ft.addToBackStack(null);
				ft.commit();
			}
		}
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		
		onSessionStateChange(session, null, null);
		uiHelper.onResume();
	}
	
	@Override 
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		
		/*
	    if (fragments[USER_FRAGMENT].isVisible()) {
	        settings = menu.add(R.string.settings);
	    } else {
	    	menu.removeItem(R.string.settings);
	        settings = null;
	    }
	    */

	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.equals(settings)) {
	    	
	        return true;
	    }
	    return false;
	}
	
	
	// For debugging
	public static void showSessionStatus(String identifier) {
		Session session = Session.getActiveSession();

		if (session == null) {
			Log.e(TAG + " " + identifier, "session is null");
		} else {
			Log.e(TAG + " " + identifier, "session is not null");
		}

		if (session.isOpened()) {
			Log.e(TAG + " " + identifier, "session open");
		} else if (session.isClosed()) {
			Log.e(TAG + " " + identifier, "session close");
		} else {
			Log.e(TAG + " " + identifier, "session neither open nor close");
		}
	}
	

	@Override
	public void onPostSelected(String msg) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		Bundle args = new Bundle();
		args.putString(UserProfileFragment.COMMENT, msg);
		CommentFragment newComFrag = new CommentFragment();
		newComFrag.setArguments(args);
		
		ft.replace(R.id.container, newComFrag);
		ft.addToBackStack(null);
		ft.commit();
		Log.e(TAG, "Post: " + msg);
		Log.e(TAG, "Post Clicked");
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setCurrentFragment();
	}
	
	private void setCurrentFragment() {
		FragmentManager fm = getSupportFragmentManager();
		List<Fragment> fragments = fm.getFragments();
		for (Fragment tmpFrag : fragments) {
			if (tmpFrag != null && tmpFrag.isVisible()) {
				
				Log.e(TAG, "Current Fragment: " + tmpFrag.toString());
				if (tmpFrag instanceof SplashFragment) {
					currentFragment = SPLASH_FRAGMENT;
					break;
				}
			}
		}
	}
	
}

