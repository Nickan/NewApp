package com.nickan.newapp;


import java.util.ArrayList;
import java.util.List;

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
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class UserProfileFragment extends ListFragment {
	public static final String COMMENT = "com.nickan.newapp.UserProfileFragment.COMMENT";
	
	private OnUserProfileSelectedListener selectedCallback;
	
	public interface OnUserProfileSelectedListener {
		public void onPostSelected(ArrayList<String> comment);
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
	//	selectedCallback.onPostSelected("Test");
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
	
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.user_profile, container, false);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
		
		initializeDebugTools(view);
		return view;
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		
		Session session = Session.getActiveSession();
		onStateStatusChange(session, null, null);
	}
	
	private void createUserProfile(Session session) {
		id = (TextView) view.findViewById(R.id.id);
		name = (TextView) view.findViewById(R.id.name);
		link = (TextView) view.findViewById(R.id.link);
		gender = (TextView) view.findViewById(R.id.gender);
		locale = (TextView) view.findViewById(R.id.locale);
		
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
						
						GraphObject graphObject = response.getGraphObject();
						
						if (graphObject == null) {
							Log.e(TAG, "onCompleted() GraphObject is null");
							return;
						}
						
						JSONObject jObject = graphObject.getInnerJSONObject();
						
						int likeCount = 0;
						try {
							JSONArray jArrayData = (JSONArray) jObject.get("data");
						//	Log.e(TAG, "Array Length: " + jArrayData.length());
							for (int index = 0; index < jArrayData.length(); ++index) {
								JSONObject tmpJObj = jArrayData.getJSONObject(index);
								
								
									String test = null;
									test = tmpJObj.getString("id");
									
									if (test != null) {
										Log.e(TAG, "Test: " + test);
									}
									
									
								JSONObject jObjLikes = getJSONEdge(tmpJObj, "likes");
								JSONObject jObjComments = getJSONEdge(tmpJObj, "comments");
								
								if (jObjLikes != null) {
									++likeCount;
								}
								Log.e(TAG, "LIke count: " + likeCount);
								
								if (jObjComments == null) {
									Log.e(TAG, "jObjComments is null");
								}
								
								
								String story = getStory(tmpJObj);
								createPost(story, jObjLikes, jObjComments);
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
	
	private void createPost(String story, JSONObject jObjLikes, final JSONObject jObjComments) {
		JSONArray jArrayLikesData = null;
		
		if (getActivity() == null) return;
		
		// There is likes count
		if (jObjLikes != null) {
			try {
				jArrayLikesData = jObjLikes.getJSONArray("data");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			
		}
		
		int likeCount = 0;
		if (jArrayLikesData != null) {
			likeCount = jArrayLikesData.length();
		} else {
			likeCount = 0;
		}
		
		TextView newPost = new TextView(getActivity());
		newPost.setText("Post Count: " + postCount++ + "\n" + story + " No: of likes: " + likeCount);
		newPost.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TextView tv = (TextView) v;
				if (jObjComments != null) {
					setComments(jObjComments);
				} else {
					selectedCallback.onPostSelected(null);
				}
				
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

		LinearLayout layout = (LinearLayout) view.findViewById(R.id.user_profile_layout);
		layout.addView(newPost);
		posts.add(newPost);

	}
	
	private void setComments(JSONObject jObjComments) {
		ArrayList<String> comments = new ArrayList<String>();
		JSONArray comData = null;
		try {
			comData = jObjComments.getJSONArray("data");
			
			if (comData == null) return;
			
			for (int index = 0; index < comData.length(); ++index) {
				JSONObject tmpData = comData.getJSONObject(index);
				
				JSONObject from = tmpData.getJSONObject("from");
				String fromUserName = from.getString("name");
				
				String msg = tmpData.getString("message");
				comments.add("From: " + fromUserName + ": " + msg);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (comments.size() > 0) {
			selectedCallback.onPostSelected(comments);
		}
		
	}
	
	
	private void onStateStatusChange(Session session, SessionState state,
			Exception exception) {
		if (session != null && session.isOpened()) {
			if (getView() == null) return;
			
			clearUserPosts();
			
			createUserProfile(session);
			createUserPosts(session);
			
		}
	}
	
	private void clearUserPosts() {
		View view = getView();
		if (view == null) return;
		
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.user_profile_layout);
		
		if (layout != null) {
			for (TextView tmpView : posts) {
				layout.removeView(tmpView);
			}
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
	
	
	private void clearTokenAccess(final Session session) {
		new Request(session, "/me/permissions", null, HttpMethod.DELETE,
				new Request.Callback() {
					public void onCompleted(Response response) {
						Log.e(TAG, "onCompleted() clearing token access");
						showPermissions(session);
					}

				}).executeAsync();
	}
	
	private void showPermissions(Session session) {
		List<String> perms = session.getPermissions();
	    if (perms != null) {
	    	for (String tmp : perms) {
	    		Log.e(TAG, "Permission: " + tmp);
	    	}
	    }
	    
	}
	
	private void initializeDebugTools(View view) {
		Button clearAccessTokenButton = (Button) view.findViewById(R.id.clear_access_token_button);
		clearAccessTokenButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Session session = Session.getActiveSession();
				if (session != null && session.isOpened()) {
					Log.e(TAG, "Clear access token clicked!");
					clearTokenAccess(session);
					showPermissions(session);
				} else {
					Log.e(TAG, "session is null or closed");
				}
				
			}
		});
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
