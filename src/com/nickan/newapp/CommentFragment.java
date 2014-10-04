package com.nickan.newapp;

import java.util.ArrayList;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class CommentFragment extends Fragment {
	private static final String TAG = "CommentFragment";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.comment, container, false);
		
		showComment(view);
		return view;
	}
	
	private void showComment(View view) {
		Intent intent = getActivity().getIntent();
		Bundle args = getArguments();
		
		if (args == null) {
			Log.e(TAG, "Bundle is null");
			return;
		}
		ArrayList<String> comments = args.getStringArrayList(FeedFragment.COMMENT);
		
		Resources r = getResources();
		
		int leftMarginDp = 25;
		int leftMarginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftMarginDp, r.getDisplayMetrics());
		
		int topMarginDp = 25;
		int topMarginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topMarginDp, r.getDisplayMetrics());
		
		
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.comment_layout);
		
		if (comments == null) {
			TextView newUserComment = new TextView(getActivity());
			
			newUserComment.setText("No comments yet");
			
			LayoutParams lParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lParams.leftMargin = leftMarginPx;
			lParams.topMargin = topMarginPx;
			
			newUserComment.setLayoutParams(lParams);
			layout.addView(newUserComment);
		} else {
			for (String tmpStr : comments) {
				TextView newUserComment = new TextView(getActivity());
				newUserComment.setText(tmpStr);
				
				LayoutParams lParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lParams.leftMargin = leftMarginPx;
				lParams.topMargin = topMarginPx;
				
				newUserComment.setLayoutParams(lParams);
				
				layout.addView(newUserComment);
			}
		}
		
	}
	
}
