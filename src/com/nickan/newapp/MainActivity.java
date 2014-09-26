package com.nickan.newapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
import android.support.v7.app.ActionBarActivity;
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
	private static final int USER_FRAGMENT = 1;
	private static final int SETTINGS_FRAGMENT = 2;
	private static final int COMMENT_FRAGMENT = 3;
	
	private Fragment[] fragments = new Fragment[COMMENT_FRAGMENT + 1];
	
//	private SplashFragment splashFragment;
//	private UserProfileFragment userFragment;
	
//	@Override
//	public View onCreateView(String name, Context context, AttributeSet attrs) {
//		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		
//		return inflater.inflate(resource, root)
//	}
	
	// Trying out again the Callback system
	private Session.StatusCallback callback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (state != null && state.isOpened()) {
				onSessionStateChange(session, state, exception);
			}
			showSessionStatus("call()");
		}
	};
	private UiLifecycleHelper uiHelper;
	
	private MenuItem settings;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		FragmentManager fm = getSupportFragmentManager();
		
		fragments[SPLASH_FRAGMENT] = fm.findFragmentById(R.id.splash_fragment);
		fragments[USER_FRAGMENT] = fm.findFragmentById(R.id.user_fragment);
		fragments[SETTINGS_FRAGMENT] = fm.findFragmentById(R.id.userSettingsFragment);
		fragments[COMMENT_FRAGMENT] = fm.findFragmentById(R.id.comment_fragment);
		
		FragmentTransaction fragTrans = fm.beginTransaction();
		for (int index = 0; index < fragments.length; ++index) {
			fragTrans.hide(fragments[index]);
		}
		fragTrans.commit();
		
		/*
			Creating a fragment at runtime, but still I am unsuccessful :(
		if (savedInstanceState == null) {
	        // Add the fragment on initial activity setup
			splashFragment = new SplashFragment();
			userFragment = new UserProfileFragment();
			
			FragmentTransaction fragTransaction = fm.beginTransaction();
	        fragTransaction.add(android.R.id.content, splashFragment);
	     
	        fragTransaction.commit();
	    } else {
	        // Or set the fragment from restored state info
	    	splashFragment = (SplashFragment) fm.findFragmentById(android.R.id.content);
	    }
	    */
	//	getHashKey();
		
		showFragment(SPLASH_FRAGMENT, true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int index = 0; index < fragments.length; ++index) {
			if (index == fragmentIndex) {
				transaction.show(fragments[index]);
			} else {
				transaction.hide(fragments[index]);
			}
		}
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
		
		showSessionStatus("showFragment: " + fragmentIndex);
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_action, menu);
	    MenuItem item = menu.findItem(R.id.action_search);
	    
	    if (item.isVisible()) {
	    	Log.e(TAG, "Visible");
	    } else {
	    	Log.e(TAG, "not");
	    }
	    
	    if (menu.hasVisibleItems()) {
	    	Log.e(TAG, "Has visible items");
	    } else {
	    	Log.e(TAG, "No visible items");
	    }
	    
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
			showFragment(USER_FRAGMENT, true);
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
		showSessionStatus("onResume");
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
		
	    if (fragments[USER_FRAGMENT].isVisible()) {
	        settings = menu.add(R.string.settings);
	    } else {
	    	menu.removeItem(R.string.settings);
	        settings = null;
	    }

	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.equals(settings)) {
	        showFragment(SETTINGS_FRAGMENT, true);
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
		showFragment(COMMENT_FRAGMENT, true);
		CommentFragment comFrag = (CommentFragment) fragments[COMMENT_FRAGMENT];
		comFrag.showComment(msg);
		Log.e(TAG, "Post Clicked");
	}
	
	@Override
	public void onBackPressed() {
		Log.e(TAG, "Back pressed");
		FragmentManager fm = getSupportFragmentManager();
		if (fm.getBackStackEntryCount() > 0) {
			Log.e(TAG, "Entry count: " + fm.getBackStackEntryCount());
		} else {
			Log.e(TAG, "No entry count");
			super.onBackPressed();
		}
	//	super.onBackPressed();
		
		
	}
	
}

