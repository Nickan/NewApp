package com.nickan.newapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CommentFragment extends Fragment {
	private static final String TAG = "CommentFragment";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.comment, container, false);
		
		showComment(view);
		return view;
	}
	
	private void showComment(View view) {
		TextView userComment = (TextView) view.findViewById(R.id.user_comment);
		Intent intent = getActivity().getIntent();
		Bundle args = getArguments();
		String comment = args.getString(UserProfileFragment.COMMENT);
		
		Log.e(TAG, comment);
		userComment.setText(comment);
	}
	
}
