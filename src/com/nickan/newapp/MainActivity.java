package com.nickan.newapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import java.util.ArrayList;
import java.util.Arrays;
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
import com.facebook.widget.LoginButton;

public class MainActivity extends FragmentActivity implements UserProfileFragment.OnUserProfileSelectedListener {
	private static final String TAG = "MainActivity";
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
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
			
			SplashFragment splashFragment = new SplashFragment();
		//	replaceCurrentFragment(splashFragment, false);
			ft.add(R.id.container, splashFragment);
			ft.commit();
		}
	
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
			Fragment currentFragment = getCurrentFragment();
			
			if (currentFragment instanceof SplashFragment) {
				replaceCurrentFragment(new UserProfileFragment(), true);
				
				Log.e(TAG, "To SplashFragment");
			}
			invalidateOptionsMenu();
			
			Log.e(TAG, "onSessionStateChange()" + getCurrentFragment().toString());
		} else {
			Log.e(TAG, "onSessionStateChange() + null");
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
		
		onSessionStateChange(session, session.getState(), null);
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
		Log.e(TAG, "onPrepareOptionsMenu()");
		
	    if (getCurrentFragment() instanceof SplashFragment) {
	    //	menu.removeItem(R.string.settings);
	    //    settings = null;
	    	MenuItem searchItem = menu.findItem(R.id.action_search);
	    	MenuItem setttingsItem = menu.findItem(R.id.action_settings);
	    } else {
	    //	settings = menu.add(R.string.settings);
	    	initializeActionButtons(menu);
	    }
	    
	    return super.onPrepareOptionsMenu(menu);
	}
	
	private void initializeActionButtons(Menu menu) {
		// Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_action, menu);
	    MenuItem item = menu.findItem(R.id.action_search);
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
	public void onPostSelected(ArrayList<String> comments) {
		Bundle args = new Bundle();
		args.putStringArrayList(UserProfileFragment.COMMENT, comments);
		CommentFragment newComFrag = new CommentFragment();
		newComFrag.setArguments(args);
		
		replaceCurrentFragment(newComFrag, true);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		invalidateOptionsMenu();
	}
	
	
	private void replaceCurrentFragment(Fragment newFragment, boolean addToBackStack) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		ft.replace(R.id.container, newFragment);
		if (addToBackStack) {
			ft.addToBackStack(null);
		}
		ft.commit();
	}
	
	private final Fragment getCurrentFragment() {
		FragmentManager fm = getSupportFragmentManager();
		List<Fragment> fragments = fm.getFragments();
		for (Fragment tmpFrag : fragments) {
			if (tmpFrag != null && tmpFrag.isVisible()) {
				return tmpFrag;
			}
		}
		return null;
	}
	
}

