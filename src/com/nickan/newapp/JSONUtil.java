package com.nickan.newapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONUtil {
	private JSONUtil() {}
	
	public static JSONArray getJSONArray(JSONObject jObjParent, String edgeName, String TAG) {
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
	public static JSONObject getJSONObject(JSONObject jsonObjParent, String edgeType, String TAG) {
		try {
			return jsonObjParent.getJSONObject(edgeType);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, edgeType + " Exception is thrown getting the JSONObject");
		}
		return null;
	}
	
	public static JSONObject getJSONObject(JSONArray jsonArray, int index, String TAG) {
		try {
			return jsonArray.getJSONObject(index);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "Error getting JSONObject from JSONArray at index: " + index);
		}
		return null;
	}
	
	/**
	 * Getting the String from JSONObject, handles the exception, returns null when exception is thrown
	 * @param jsonObject
	 * @param stringName
	 * @param TAG
	 * @return
	 */
	public static String getString(JSONObject jsonObject, String stringName, String TAG) {
		try {
			return jsonObject.getString(stringName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, stringName + "Exception is thrown getting the String");
		}
		return null;
	}
}
