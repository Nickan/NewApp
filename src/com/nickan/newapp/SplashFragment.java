package com.nickan.newapp;

import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;


public class SplashFragment extends Fragment {
	private static final String TAG = "SplashFragment";
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	private UiLifecycleHelper uiHelper;
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.splash, container, false);
	    
	    LoginButton authButton = (LoginButton) view.findViewById(R.id.loginButton);
	    authButton.setFragment(this);
	//    authButton.setReadPermissions(Arrays.asList("user_likes", "user_status", "read_stream"));
	    authButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickLogin(v);
			}
		});
	    
	    Log.e(TAG, "onCreateView()");
	    return view;
	}
	
	private void onClickLogin(View view) {
		LoginButton loginButton = (LoginButton) view;
		loginButton.setReadPermissions(Arrays.asList("user_likes", "user_status", "read_stream"));
		Log.e(TAG, "Log in clicked");
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.e(TAG, "Logged in...");
	    //    clearTokenAccess(session);
	        showPermissions();
	    } else if (state.isClosed()) {
	        Log.e(TAG, "Logged out...");
	        session.closeAndClearTokenInformation();
	        showPermissions();
	        
	        if (exception != null) {
	        	Log.e(TAG, "Error: " + exception.getMessage());
	        }
	    }
	    
	}
	
	private void clearTokenAccess(Session session) {
		new Request(session, "/me/permissions", null, HttpMethod.DELETE,
				new Request.Callback() {
					public void onCompleted(Response response) {
					}

				}).executeAsync();
	}
	
	
	@Override
	public void onResume() {
	    super.onResume();
	 // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }

	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
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
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	
	private void showPermissions() {
		Session session = Session.getActiveSession();
		List<String> perms = session.getPermissions();
	    if (perms != null) {
	    	for (String tmp : perms) {
	    		Log.e(TAG, "Permission: " + tmp);
	    	}
	    }
	    
	}

}
