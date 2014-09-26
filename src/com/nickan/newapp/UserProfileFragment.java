package com.nickan.newapp;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class UserProfileFragment extends ListFragment {
	
	private OnUserProfileSelectedListener selectedCallback;
	
	public interface OnUserProfileSelectedListener {
		public void onPostSelected(String comment);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			selectedCallback = (OnUserProfileSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
                    + " must implement OnUserProfileSelectedListener");
		}
	}
	
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Send the event to the host activity
		selectedCallback.onPostSelected("Test");
    }
	
	
	private static final String TAG = "UserProfileFragment";

	
	TextView id;
	TextView name;
	TextView link;
	TextView gender;
	TextView locale;
	
	private ArrayList<TextView> posts = new ArrayList<TextView>();
	
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onStateStatusChange(session, state, exception);
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.user_profile, container, false);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
		return view;
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		
		Session session = Session.getActiveSession();
		onStateStatusChange(session, null, null);
	}
	
	private void createUserProfile(Session session) {
		//...
		showSessionStatus("createUserProfile()");
		View view = getView();
		id = (TextView) view.findViewById(R.id.id);
		name = (TextView) view.findViewById(R.id.name);
		link = (TextView) view.findViewById(R.id.link);
		gender = (TextView) view.findViewById(R.id.gender);
		locale = (TextView) view.findViewById(R.id.locale);


		Log.e(TAG, "Session is open");
		Request.newMeRequest(session, new Request.GraphUserCallback() {
				
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (response.getError() != null) {
					Log.e(TAG, "Error " + response.getError().getErrorMessage());
					return;
				}
					
				if (user == null) {
					Log.e(TAG, "GraphUser is null");
					return;
				}
					
				id.setText("ID: " + user.getId());
				name.setText("Name: " + user.getName());
				link.setText("Link: " + user.getLink());
				gender.setText("Gender: " + user.asMap().get("gender").toString());
				locale.setText("Locale: " + user.getLocation());		

				id.setText("ID: " + user.getId());
				name.setText("Name: " + user.getName());
				link.setText("Link: " + user.getLink());
				gender.setText("Gender: "
						+ user.asMap().get("gender").toString());
				locale.setText("Locale: " + user.getLocation());
			}
		}).executeAsync();
	}
	
	
	private void createUserPosts(Session session) {
		new Request(session, "/me/feed", null, HttpMethod.GET,
				new Request.Callback() {
			
					public void onCompleted(Response response) {
						if (response == null) {
							Log.e(TAG, "onCompleted() response is null");
							return;
						}
						
						JSONObject jObject = response.getGraphObject().getInnerJSONObject();
						
						int likeCount = 0;
						try {
							JSONArray jArrayData = (JSONArray) jObject.get("data");
							Log.e(TAG, "Array Length: " + jArrayData.length());
							for (int index = 0; index < jArrayData.length(); ++index) {
								JSONObject tmpJObj = jArrayData.getJSONObject(index);
								JSONObject jObjLikes = getJSONEdge(tmpJObj, "likes");
								
								if (jObjLikes != null) {
									++likeCount;
								}
								Log.e(TAG, "LIke count: " + likeCount);	
								
								
								String story = getStory(tmpJObj);
								createPost(story, jObjLikes);
							}
												
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
				}).executeAsync();
		
		
	}
	
	private JSONObject getJSONEdge(JSONObject jsonObject, String edgeType) {
		try {
			return jsonObject.getJSONObject(edgeType);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String getStory(JSONObject jsonObject) {
		try {
			return jsonObject.getString("story");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private int postCount = 0;
	
	private void createPost(String story, JSONObject jObjLikes) {
		JSONArray jArrayLikesData = null;
		
		// There is likes count
		if (jObjLikes != null) {
			try {
				jArrayLikesData = jObjLikes.getJSONArray("data");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (jArrayLikesData != null) {
			TextView newPost = new TextView(getActivity());
			newPost.setText("Post Count: " + postCount++ + "\n" + story + " No: of likes: " + jArrayLikesData.length());
			newPost.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView tv = (TextView) v;
					selectedCallback.onPostSelected(tv.getText().toString());
				}
			});
			
			Resources r = getResources();
			
			int leftMarginDp = 25;
			int leftMarginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftMarginDp, r.getDisplayMetrics());
			
			int topMarginDp = 25;
			int topMarginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topMarginDp, r.getDisplayMetrics());
			
			LayoutParams lParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lParams.leftMargin = leftMarginPx;
			lParams.topMargin = topMarginPx;
			newPost.setLayoutParams(lParams);
			
			View view = getView();
			LinearLayout layout = (LinearLayout) view.findViewById(R.id.user_profile_layout);
			layout.addView(newPost);
			posts.add(newPost);
		}

	}
	
	
	private void onStateStatusChange(Session session, SessionState state,
			Exception exception) {
		if (session != null && session.isOpened()) {
			clearUserPosts();
			
			createUserProfile(session);
			createUserPosts(session);
			
		}
	}
	
	private void clearUserPosts() {
		View view = getView();
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.user_profile_layout);
		for (TextView tmpView : posts) {
			layout.removeView(tmpView);
		}
		postCount = 0;
	}

	
	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		uiHelper.onSaveInstanceState(state);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
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
	
}
