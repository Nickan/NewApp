package com.nickan.newapp;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.nickan.newapp.util.IdCreator;
import com.nickan.newapp.util.JSONUtil;
import com.nickan.newapp.util.RuleAlignment;
import com.nickan.newapp.util.ViewUtil;

public class FeedFragment extends ListFragment {
	// {{ Local variables
	public static final String COMMENTS= "com.nickan.newapp.FeedFragment.COMMENTS";
	
	private OnFeedFragmentListener postSelectedCallback;
	
	public interface OnFeedFragmentListener {
		/**
		 * Called every time a post is clicked
		 * @param comments
		 */
		public void onPostSelected(String comments);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			postSelectedCallback = (OnFeedFragmentListener) activity;
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
	
	
	private static final String TAG = "FeedFragment";

	
	TextView id;
	TextView name;
	TextView link;
	TextView gender;
	TextView locale;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onStateStatusChange(session, state, exception);
		}
	};
	
	private HashMap<String, JSONArray> commentsMap;
	// }}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.feed_fragment, container, false);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
		
		commentsMap = new HashMap<String, JSONArray>();
		
		//...
		Log.e(TAG, "onCreateView()");

		return view;
	}
	
	// {{ UI Creation
	int universalTextSize = 15;
	/*
	 * UI Creation
	 */
	private RelativeLayout newPostLayout(int picId, String name, String dateAndTime, String userComment, JSONArray jArrayComments) {
		// Main post layout
		RelativeLayout postLayout = new RelativeLayout(getActivity());
		postLayout.setBackgroundColor(Color.WHITE);
		
		ImageView userPhoto = ViewUtil.newImageView(1000, 40, 40, picId, 
				RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE, 10, 0, 10, 0);

		TextView user_name = ViewUtil.newTextView(1001, universalTextSize, RelativeLayout.RIGHT_OF, userPhoto.getId(), name, 0, 0, 10, 0);
		
		ArrayList<RuleAlignment> dateRuleAlign = new ArrayList<RuleAlignment>();
		dateRuleAlign.add(new RuleAlignment(RelativeLayout.ALIGN_LEFT, user_name.getId()));
		dateRuleAlign.add(new RuleAlignment(RelativeLayout.BELOW, user_name.getId()));
		TextView date_and_time = ViewUtil.newTextView(1002, universalTextSize, dateRuleAlign, 
				dateAndTime, 0, 0, 0, 0);
		
		TextView user_comment = ViewUtil.newTextView(1003, universalTextSize, RelativeLayout.BELOW, userPhoto.getId(), userComment, 10, 0, 0, 0);
		
		int commentsCount = 0;
		if (jArrayComments != null) {
			commentsCount = jArrayComments.length();
		}
		
		TextView comment_count = ViewUtil.newTextView(1004, universalTextSize, Arrays.asList(
				new RuleAlignment(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
						, new RuleAlignment(RelativeLayout.BELOW, user_comment.getId())), 
						Integer.toString(commentsCount), 10, 0, 0, 0);
		
		postLayout.addView(userPhoto);
		postLayout.addView(user_name);
		postLayout.addView(date_and_time);
		postLayout.addView(user_comment);
		postLayout.addView(comment_count);
		
		postLayout.addView(newPostButtons(comment_count.getId(), jArrayComments));
		
		return postLayout;
	}
	
	private LinearLayout newPostButtons(int parentId, JSONArray jArrayComments) {
		LinearLayout postLayout = new LinearLayout(getActivity());
		
		RelativeLayout.LayoutParams postLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, ViewUtil.toPixelDimension(25));
		
		postLayoutParams.addRule(RelativeLayout.BELOW, parentId);
		postLayout.setLayoutParams(postLayoutParams);
		
		// Layout param for like to add weight
		LinearLayout.LayoutParams likeLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.MATCH_PARENT, 1);
		postLayout.addView(newLikeButton(), likeLayoutParams);
		
		LinearLayout.LayoutParams commentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.MATCH_PARENT, 1);
		postLayout.addView(newCommentButton(jArrayComments), commentLayoutParams);
		
		LinearLayout.LayoutParams shareLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.MATCH_PARENT, 1);
		postLayout.addView(newShareButton(), shareLayoutParams);

		return postLayout;
	}
	
	int buttonTextSize = 12;
	
	private RelativeLayout newLikeButton() {
		RelativeLayout likeLayout = new RelativeLayout(getActivity());
		RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(0, 
				RelativeLayout.LayoutParams.MATCH_PARENT);
		likeLayout.setBackgroundColor(Color.CYAN);
		likeLayout.setLayoutParams(rParams);
		
		// Inner layout
		LinearLayout innerLayout = new LinearLayout(getActivity());
		innerLayout.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		innerLayout.setLayoutParams(innerParams);

		ImageView likeImg = ViewUtil.newImageView(1011, 14, 14,
				R.drawable.share_button, RelativeLayout.CENTER_IN_PARENT,
				RelativeLayout.TRUE, 0, 0, 0, 0);
		TextView likeMsg = ViewUtil.newTextView(1012, buttonTextSize,
				RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE, "Share",
				0, 0, 0, 0);
		innerLayout.addView(likeImg);
		innerLayout.addView(likeMsg);

		RelativeLayout.LayoutParams innerLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		innerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,
				RelativeLayout.TRUE);
		likeLayout.addView(innerLayout, innerLayoutParams);
		
		return likeLayout;
	}
	
	private RelativeLayout newCommentButton(JSONArray jArrayComments) {
		RelativeLayout commentLayout = new RelativeLayout(getActivity());
		commentLayout.setId(IdCreator.getId());
		commentsMap.put(Integer.toString(commentLayout.getId()), jArrayComments);
		
		// Insert the comments
		commentLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				JSONArray comments = commentsMap.get(Integer.toString(v.getId()));
				if (comments != null) {
					postSelectedCallback.onPostSelected(comments.toString());
				} else {
					postSelectedCallback.onPostSelected(null);
				}
			}
		});
		
		RelativeLayout.LayoutParams commentParams = new RelativeLayout.LayoutParams(0, 
				RelativeLayout.LayoutParams.MATCH_PARENT);
		
		commentLayout.setLayoutParams(commentParams);
		commentLayout.setBackgroundColor(Color.CYAN);
		
		// Inner layout
		LinearLayout innerLayout = new LinearLayout(getActivity());
		innerLayout.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		innerLayout.setLayoutParams(innerParams);
		
		ImageView commentImg = ViewUtil.newImageView(1011, 14, 14, R.drawable.comment_button, 
				RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE, 0, 0, 0, 0);
		TextView commentMsg = ViewUtil.newTextView(1012, buttonTextSize, RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE, "Comment", 0, 0, 0, 0);
		innerLayout.addView(commentImg);
		innerLayout.addView(commentMsg);
		
		
		RelativeLayout.LayoutParams innerLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		innerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		commentLayout.addView(innerLayout, innerLayoutParams);
		
		return commentLayout;
	}
	
	private RelativeLayout newShareButton() {
		RelativeLayout shareLayout = new RelativeLayout(getActivity());
		RelativeLayout.LayoutParams shareParams = new RelativeLayout.LayoutParams(0, 
				RelativeLayout.LayoutParams.MATCH_PARENT);
		shareLayout.setBackgroundColor(Color.CYAN);
		
		shareLayout.setLayoutParams(shareParams);
		
		// Inner layout
		LinearLayout innerLayout = new LinearLayout(getActivity());
		innerLayout.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		innerLayout.setLayoutParams(innerParams);

		ImageView shareImg = ViewUtil.newImageView(1011, 14, 14,
				R.drawable.share_button, RelativeLayout.CENTER_IN_PARENT,
				RelativeLayout.TRUE, 0, 0, 0, 0);
		TextView shareMsg = ViewUtil.newTextView(1012, buttonTextSize,
				RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE,
				"Share", 0, 0, 0, 0);
		innerLayout.addView(shareImg);
		innerLayout.addView(shareMsg);

		RelativeLayout.LayoutParams innerLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		innerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,
				RelativeLayout.TRUE);
		shareLayout.addView(innerLayout, innerLayoutParams);
		
		return shareLayout;
	}
	
	private LinearLayout getBaseLayout(View view) {
		return (LinearLayout) view.findViewById(R.id.feed_linear_layout);
	}
	// }}
	
	// {{ JSONObject and JSONArray information extraction
	private void createFeed() {
		// I need to extract name, date and time, user comment, comment count. Later photo
		Session session = Session.getActiveSession();
		
		new Request(session, "/me/feed", null, HttpMethod.GET, new Request.Callback() {
			
			@Override
			public void onCompleted(Response response) {
				Log.e(TAG, "onCompleted()");
				
				// If there is an error, stop executing the rest of the code
				if (response.getError() != null) {
					Log.e(TAG, response.getError().toString());
					return;
				}
				
				JSONObject feedJObj = response.getGraphObject().getInnerJSONObject();
				JSONArray dataArray = JSONUtil.getJSONArray(feedJObj, "data", TAG);
				processuserJSONArray(dataArray);
			}
		}).executeAsync();
		
		
	}
	
	private void processuserJSONArray(JSONArray dataArray) {
		View view = getView();

		LinearLayout feedLayout = getBaseLayout(view);
		LinearLayout.LayoutParams postLayoutParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
		
		// Loop through all the posts
		for (int index = 0; index < dataArray.length(); ++index) {
			JSONObject postJObj = JSONUtil.getJSONObject(dataArray, index, TAG);
			
			if (postJObj == null) {
				Log.e(TAG, "postJObj is null");
				return;
			}
			
			JSONObject fromJObj = JSONUtil.getJSONObject(postJObj, "from", TAG);
			String name = JSONUtil.getString(fromJObj, "name", TAG);
			String comment = JSONUtil.getString(postJObj, "message", TAG);

			JSONArray commentsJArray = null;
			JSONObject commentsJObj = JSONUtil.getJSONObject(postJObj, "comments", TAG);
				if (commentsJObj != null) {
					commentsJArray = JSONUtil.getJSONArray(commentsJObj, "data", TAG);
			}
			
			int margin = ViewUtil.toPixelDimension(10);
			postLayoutParams.setMargins(margin, margin, margin, 0);
			feedLayout.addView(newPostLayout(R.drawable.com_facebook_profile_picture_blank_portrait,
											name, "Yesterday at 11:00 AM", comment, commentsJArray),
											postLayoutParams);
		}
	}
	
	// }}
	
	
	@Override
	public void onResume() {
		super.onResume();
		
		Session session = Session.getActiveSession();
		onStateStatusChange(session, null, null);
	}
	
	private void onStateStatusChange(Session session, SessionState state,
			Exception exception) {
		if (session != null && session.isOpened()) {
			createFeed();
		}
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
	
	// {{ For debuggin
	/*
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
	*/
	
	
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
	// }}
	
	
}
