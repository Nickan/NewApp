package com.nickan.newapp;


import java.util.ArrayList;
import java.util.Arrays;
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

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FeedFragment extends ListFragment {
	// {{ Local variables
	public static final String COMMENT = "com.nickan.newapp.FeedFragment.COMMENT";
	
	private OnFeedFragmentListener selectedCallback;
	
	public interface OnFeedFragmentListener {
		/**
		 * Called every time a post is clicked
		 * @param comments
		 */
		public void onPostSelected(ArrayList<String> comments);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			selectedCallback = (OnFeedFragmentListener) activity;
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
	// }}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.user_feed, container, false);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
		
		//...
		Log.e(TAG, "onCreateView()");
		return view;
	}
	
	// {{ UI Creation

	/*
	 * UI Creation
	 */
	private RelativeLayout newPostLayout(int picId, String name, String dateAndTime, String userComment, String commentCount) {
		// Main post layout
		RelativeLayout postLayout = new RelativeLayout(getActivity());
		postLayout.setBackgroundColor(Color.WHITE);
		
		ImageView userPhoto = newImageView(1000, 40, 40, picId, 
				RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE, 10, 0, 10, 0);
		
		TextView user_name = newTextView(1001, 10, RelativeLayout.RIGHT_OF, userPhoto.getId(), name, 0, 0, 10, 0);
		
		ArrayList<RuleAlignment> dateRuleAlign = new ArrayList<RuleAlignment>();
		dateRuleAlign.add(new RuleAlignment(RelativeLayout.ALIGN_LEFT, user_name.getId()));
		dateRuleAlign.add(new RuleAlignment(RelativeLayout.BELOW, user_name.getId()));
		TextView date_and_time = newTextView(1002, 10, dateRuleAlign, 
				dateAndTime, 0, 0, 0, 0);
		
		TextView user_comment = newTextView(1003, 10, RelativeLayout.BELOW, userPhoto.getId(), userComment, 10, 0, 0, 0);
		TextView comment_count = newTextView(1004, 10, Arrays.asList(new RuleAlignment(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
									, new RuleAlignment(RelativeLayout.BELOW, user_comment.getId())), commentCount, 10, 0, 0, 0);
		
		postLayout.addView(userPhoto);
		postLayout.addView(user_name);
		postLayout.addView(date_and_time);
		postLayout.addView(user_comment);
		postLayout.addView(comment_count);
		
		postLayout.addView(newPostButton(comment_count.getId()));
		
		return postLayout;
	}
	
	private LinearLayout newPostButton(int parentId) {
		LinearLayout postLayout = new LinearLayout(getActivity());
		
		RelativeLayout.LayoutParams postLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, toPixelDimension(25));
		
		postLayoutParams.addRule(RelativeLayout.BELOW, parentId);
		postLayout.setLayoutParams(postLayoutParams);
		
		// Layout param for like to add weight
		LinearLayout.LayoutParams likeLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.MATCH_PARENT, 1);
		postLayout.addView(newLikeButton(), likeLayoutParams);
		
		LinearLayout.LayoutParams commentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.MATCH_PARENT, 1);
		postLayout.addView(newCommentButton(), commentLayoutParams);
		
		LinearLayout.LayoutParams shareLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.MATCH_PARENT, 1);
		postLayout.addView(newShareButton(), shareLayoutParams);

		return postLayout;
	}
	
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

		ImageView likeImg = newImageView(1011, 14, 14,
				R.drawable.share_button, RelativeLayout.CENTER_IN_PARENT,
				RelativeLayout.TRUE, 0, 0, 0, 0);
		TextView likeMsg = newTextView(1012, 7,
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
	
	private RelativeLayout newCommentButton() {
		RelativeLayout commentLayout = new RelativeLayout(getActivity());
		RelativeLayout.LayoutParams commentParams = new RelativeLayout.LayoutParams(0, 
				RelativeLayout.LayoutParams.MATCH_PARENT);
		
		commentLayout.setLayoutParams(commentParams);
		commentLayout.setBackgroundColor(Color.CYAN);
		
		// Inner layout
		LinearLayout innerLayout = new LinearLayout(getActivity());
		innerLayout.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		innerLayout.setLayoutParams(innerParams);
		
		ImageView commentImg = newImageView(1011, 14, 14, R.drawable.comment_button, 
				RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE, 0, 0, 0, 0);
		TextView commentMsg = newTextView(1012, 7, RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE, "Comment", 0, 0, 0, 0);
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

		ImageView shareImg = newImageView(1011, 14, 14,
				R.drawable.share_button, RelativeLayout.CENTER_IN_PARENT,
				RelativeLayout.TRUE, 0, 0, 0, 0);
		TextView shareMsg = newTextView(1012, 7,
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
	
	private int toPixelDimension(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}
	
	private int toPixelTextSize(int sp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
	}
	
	private ImageView newImageView(int id, int width, int height, int resId, int ruleAlignParent, int anchorViewId, int mLeft, int mRight,
			int mTop, int mBottom) {
		int pixWidth = toPixelDimension(width);
		int pixHeight = toPixelDimension(height);
		
		RelativeLayout.LayoutParams newImgParams = new RelativeLayout.LayoutParams(pixWidth, pixHeight);
		newImgParams.addRule(ruleAlignParent, anchorViewId);
		newImgParams.setMargins(toPixelDimension(mLeft), toPixelDimension(mTop), toPixelDimension(mTop), toPixelDimension(mBottom));
		
		ImageView newImgView = new ImageView(getActivity());
		newImgView.setId(id);
		newImgView.setBackgroundResource(resId);
		newImgView.setLayoutParams(newImgParams);

		return newImgView;
	}
	
	private ImageView newImageView(int id, int width, int height, int resId, List<RuleAlignment> rules, 
			int mLeft, int mRight, int mTop, int mBottom) {
		int pixWidth = toPixelDimension(width);
		int pixHeight = toPixelDimension(height);
		
		RelativeLayout.LayoutParams newImgParams = new RelativeLayout.LayoutParams(pixWidth, pixHeight);

		if (rules != null) {
			for (RuleAlignment tmpRule : rules) {
				newImgParams.addRule(tmpRule.ruleAlign, tmpRule.anchorViewId);
			}
		}
		
		ImageView newImgView = new ImageView(getActivity());
		newImgView.setId(id);
		newImgView.setBackgroundResource(resId);
		newImgView.setLayoutParams(newImgParams);

		return newImgView;
	}
	
	private TextView newTextView(int id, int textSize, int ruleAlign, int anchorViewId, String text, 
			int marginLeft, int marginRight, int marginTop, int marginBottom) {
		
		RelativeLayout.LayoutParams newTextParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		newTextParams.setMargins(toPixelDimension(marginLeft), toPixelDimension(marginTop), 
				toPixelDimension(marginRight), toPixelDimension(marginBottom));
		newTextParams.addRule(ruleAlign, anchorViewId);
		
		TextView newTextView = new TextView(getActivity());
		newTextView.setId(id);
		newTextView.setText(text);
		newTextView.setTextSize(toPixelTextSize(textSize));
		newTextView.setLayoutParams(newTextParams);
		
		return newTextView;
	}
	
	private TextView newTextView(int id, int textSize, List<RuleAlignment> rules, String text, 
			int marginLeft, int marginRight, int marginTop, int marginBottom) {
		
		RelativeLayout.LayoutParams newTextParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		newTextParams.setMargins(toPixelDimension(marginLeft), toPixelDimension(marginTop), 
				toPixelDimension(marginRight), toPixelDimension(marginBottom));
		
		if (rules != null) {
			for (RuleAlignment tmpRule : rules) {
				newTextParams.addRule(tmpRule.ruleAlign, tmpRule.anchorViewId);
			}
		}
		
		TextView newTextView = new TextView(getActivity());
		newTextView.setId(id);
		newTextView.setText(text);
		newTextView.setTextSize(toPixelTextSize(textSize));
		newTextView.setLayoutParams(newTextParams);
		
		return newTextView;
	}
	
	private TextView newView(int id, int ruleAlign, int anchorView, 
			int marginLeft, int marginRight, int marginTop, int marginBottom) {
		
		RelativeLayout.LayoutParams newViewParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		newViewParams.setMargins(toPixelDimension(marginLeft), toPixelDimension(marginTop), 
				toPixelDimension(marginRight), toPixelDimension(marginBottom));
		newViewParams.addRule(ruleAlign, anchorView);
		
		TextView newView = new TextView(getActivity());
		newView.setId(id);
		newView.setLayoutParams(newViewParams);
		
		return newView;
	}
	
	
	private class RuleAlignment {
		
		public RuleAlignment(int ruleAlign, int anchorViewId) {
			this.ruleAlign = ruleAlign;
			this.anchorViewId = anchorViewId;
		}
		public int ruleAlign;
		public int anchorViewId;
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
				JSONArray dataArray = getJSONArray(feedJObj, "data");
				
				
				View view = getView();

				LinearLayout feedLayout = getBaseLayout(view);
				LinearLayout.LayoutParams postLayoutParams = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.MATCH_PARENT);
				
				// Loop through all the posts
				for (int index = 0; index < dataArray.length(); ++index) {
					JSONObject postJObj = null;
					try {
						postJObj = dataArray.getJSONObject(index);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if (postJObj == null) {
						Log.e(TAG, "postJObj is null");
						return;
					}
					
					int likesCount = 0;
					JSONObject fromJObj = getJSONObject(postJObj, "from");
					String name = getString(fromJObj, "name");
					String comment = getString(postJObj, "message");
					
					JSONObject likeJObj = getJSONObject(postJObj, "likes");
					if (likeJObj != null) {
						JSONArray likeJArray = getJSONArray(likeJObj, "data");
						
						if (likeJArray != null) 
							likesCount = likeJArray.length();
					}
					
					int commentsCount = 0;
					JSONObject commentsJObj = getJSONObject(postJObj, "comments");
						if (commentsJObj != null) {
						JSONArray commentsJArray = getJSONArray(commentsJObj, "data");
						
						
						if (commentsJArray != null)
							commentsCount = commentsJArray.length();
					}
					
					int margin = toPixelDimension(10);
					postLayoutParams.setMargins(margin, margin, margin, 0);
					feedLayout.addView(newPostLayout(R.drawable.com_facebook_profile_picture_blank_portrait,
													name, "Yesterday at 11:00 AM", comment, 
													likesCount + " likes " + commentsCount + " comments"),
													postLayoutParams);
				}
			}
		}).executeAsync();
	}
	
	private JSONArray getJSONArray(JSONObject jObjParent, String edgeName) {
		try {
			return jObjParent.getJSONArray(edgeName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, edgeName + " Error getting JSONArray");
		}
		return null;
	}
	
	/**
	 * Getting the JSONObject from the JSONObject parent while handling the exception, returns null if the exception is thrown
	 * @param jsonObjParent
	 * @param edgeType
	 * @return
	 */
	private JSONObject getJSONObject(JSONObject jsonObjParent, String edgeType) {
		try {
			return jsonObjParent.getJSONObject(edgeType);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, edgeType + " Exception is thrown getting the JSONObject");
		}
		return null;
	}
	
	/**
	 * Getting the String from JSONObject, handles the exception, returns null when exception is thrown
	 * @param jsonObject
	 * @param stringName
	 * @return null if there is no String
	 */
	private String getString(JSONObject jsonObject, String stringName) {
		try {
			return jsonObject.getString(stringName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, stringName + "Exception is thrown getting the String");
		}
		return null;
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
	
	/*
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
