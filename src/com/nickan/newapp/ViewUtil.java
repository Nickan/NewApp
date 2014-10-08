package com.nickan.newapp;

import java.util.List;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ViewUtil {
	public static Activity activity;
	public static DisplayMetrics dm;
	private ViewUtil() { }
	
	public static int toPixelDimension(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
	}
	
	public static ImageView newImageView(int id, int width, int height, int resId, int ruleAlignParent, int anchorViewId, int mLeft, int mRight,
			int mTop, int mBottom) {
		int pixWidth = toPixelDimension(width);
		int pixHeight = toPixelDimension(height);
		
		RelativeLayout.LayoutParams newImgParams = new RelativeLayout.LayoutParams(pixWidth, pixHeight);
		newImgParams.addRule(ruleAlignParent, anchorViewId);
		newImgParams.setMargins(toPixelDimension(mLeft), toPixelDimension(mTop), toPixelDimension(mRight), toPixelDimension(mBottom));
		
		ImageView newImgView = new ImageView(activity);
		newImgView.setId(id);
		newImgView.setBackgroundResource(resId);
		newImgView.setLayoutParams(newImgParams);

		return newImgView;
	}
	
	public static ImageView newImageView(int id, int width, int height, int resId, List<RuleAlignment> rules, 
			int mLeft, int mRight, int mTop, int mBottom) {
		int pixWidth = toPixelDimension(width);
		int pixHeight = toPixelDimension(height);
		
		RelativeLayout.LayoutParams newImgParams = new RelativeLayout.LayoutParams(pixWidth, pixHeight);
		newImgParams.setMargins(toPixelDimension(mLeft), toPixelDimension(mTop), toPixelDimension(mRight), toPixelDimension(mBottom));

		if (rules != null) {
			for (RuleAlignment tmpRule : rules) {
				newImgParams.addRule(tmpRule.ruleAlign, tmpRule.anchorViewId);
			}
		}
		
		ImageView newImgView = new ImageView(activity);
		newImgView.setId(id);
		newImgView.setBackgroundResource(resId);
		newImgView.setLayoutParams(newImgParams);

		return newImgView;
	}
	
	
	public static TextView newTextView(int id, int textSize, int ruleAlign, int anchorViewId, String text, 
			int marginLeft, int marginRight, int marginTop, int marginBottom) {
		
		RelativeLayout.LayoutParams newTextParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		newTextParams.setMargins(toPixelDimension(marginLeft), toPixelDimension(marginTop), 
				toPixelDimension(marginRight), toPixelDimension(marginBottom));
		newTextParams.addRule(ruleAlign, anchorViewId);
		
		TextView newTextView = new TextView(activity);
		newTextView.setId(id);
		newTextView.setText(text);
		newTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
		newTextView.setLayoutParams(newTextParams);
		
		return newTextView;
	}
	
	public static TextView newTextView(int id, int textSize, List<RuleAlignment> rules, String text, 
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
		
		TextView newTextView = new TextView(activity);
		newTextView.setId(id);
		newTextView.setText(text);
		newTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
		newTextView.setLayoutParams(newTextParams);
		
		return newTextView;
	}
}
