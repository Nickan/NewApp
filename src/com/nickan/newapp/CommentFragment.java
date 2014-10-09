package com.nickan.newapp;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nickan.newapp.util.IdCreator;
import com.nickan.newapp.util.JSONUtil;
import com.nickan.newapp.util.RuleAlignment;
import com.nickan.newapp.util.ViewUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CommentFragment extends Fragment {
	private static final String TAG = "CommentFragment";
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.comment, container, false);
		
		showComment(view);
		return view;
	}
	
	public void showComment(View view) {
		Log.e(TAG, "showComment()");
		
		Intent intent = getActivity().getIntent();
		Bundle args = getArguments();
		
		String strComments = args.getString(FeedFragment.COMMENTS);
		if (strComments == null) {
			Log.e(TAG, "Comment is null");
			return;
		} else {
			Log.e(TAG, "Comment: " + strComments);
		}
		
		JSONArray comments = null;
		
		try {
			comments = new JSONArray(strComments);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (comments == null) return;
		
		for (int index = 0; index < comments.length(); ++index) {
			JSONObject tmpCom = JSONUtil.getJSONObject(comments, index, TAG);
			
			JSONObject from = JSONUtil.getJSONObject(tmpCom, "from", TAG);
			
			String name = JSONUtil.getString(from, "name", TAG);
			String msg = JSONUtil.getString(tmpCom, "message", TAG);
			String createdTime = JSONUtil.getString(tmpCom, "created_time", TAG);
			
			int defaultPicIdRes = R.drawable.com_facebook_profile_picture_blank_portrait;
			createUserPost(defaultPicIdRes, name, msg, createdTime, comments.length());
		}
		
	}
	

	private void createUserPost(int resId, String name, String msg, String createdTime, int likeCount) {
		Activity activity = getActivity();
		RelativeLayout postLayout = new RelativeLayout(activity);
		
		ImageView userPic = ViewUtil.newImageView(IdCreator.getId(), 40, 40, resId, 
				RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE, 10, 0, 10, 0);
		
		int textSize = 15;
		TextView userName = ViewUtil.newTextView(IdCreator.getId(), textSize, RelativeLayout.RIGHT_OF, 
				userPic.getId(), name, 5, 0, 10, 0);
		TextView userMsg = ViewUtil.newTextView(IdCreator.getId(), textSize, 
				Arrays.asList(new RuleAlignment(RelativeLayout.ALIGN_LEFT, userName.getId()),
						new RuleAlignment(RelativeLayout.BELOW, userName.getId())), msg, 0, 0, 5, 0);
		TextView msgCreatedTime = ViewUtil.newTextView(IdCreator.getId(), textSize,
				Arrays.asList(new RuleAlignment(RelativeLayout.ALIGN_LEFT, userName.getId()), 
						new RuleAlignment(RelativeLayout.BELOW, userMsg.getId())), createdTime, 0, 0, 5, 0);
		
		ImageView likeIcon = ViewUtil.newImageView(IdCreator.getId(), 10, 10, R.drawable.like_button, 
				Arrays.asList(new RuleAlignment(RelativeLayout.RIGHT_OF, msgCreatedTime.getId()), 
						new RuleAlignment(RelativeLayout.BELOW, userMsg.getId())), 10, 0, 10, 0);
		
		TextView msgLikeCount = ViewUtil.newTextView(IdCreator.getId(), textSize, 
				Arrays.asList(new RuleAlignment(RelativeLayout.RIGHT_OF, likeIcon.getId()), 
						new RuleAlignment(RelativeLayout.BELOW, userMsg.getId())), Integer.toString(likeCount), 5, 0, 5, 0);
		
		postLayout.addView(userPic);
		postLayout.addView(userName);
		postLayout.addView(userMsg);
		postLayout.addView(msgCreatedTime);
		postLayout.addView(likeIcon);
		postLayout.addView(msgLikeCount);
		
		LinearLayout commentLayout = (LinearLayout) view.findViewById(R.id.comment_layout);
		commentLayout.addView(postLayout);
	}
	
	
}
