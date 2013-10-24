package com.tavant.droid.security.prefs;


/**
 * Author  Tavant Technologies
 */
import com.tavant.droid.security.utils.WSConstants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class CommonPreferences {

	/*
	
	private String username;
	private String password;
	private String sessionId;
	private String latitude;
	private String longitude; 
	private String deviceId;
	private String devicetype;
	private String applicationid;
	private String applicationversion;
	private String osVersion;
	*/
	
	private boolean isFirstTime=true;
	private Context mContext;
	private SharedPreferences pref;
	private Editor editor;
	private String phoneNumber=null;
	private String fbId=null;
	private String fbAcessToken=null;
	private String volunteerNumber=null;
	private String userlocation=null;
	
	private boolean isNeedbuzzer=false;
	private boolean isInformFriends=false;
	private boolean isvolunteer=false;
	private String latitude=null;
	private String longtitude=null;
	
	private boolean isTwitterloggedin=false;
	private String acessToken=null;
	private String accessTokenSecret=null;
	
	
	private static CommonPreferences instance;

	public CommonPreferences() {
		
	}
	
	public static CommonPreferences getInstance(){
		
		if(instance == null )
			instance = new CommonPreferences();
		
		return instance;
	}
	/**
	 * 
	 * To retrive username, password, session id, latitude,longitude stored in the application prefernces 
	 * called each  time when application launches, can access through a singleton object
	 * 
	 */
	public void load(Context context) {		
		this.mContext = context;
		pref = mContext.getSharedPreferences(WSConstants.PREF_NAME,
				Activity.MODE_PRIVATE);
		editor = pref.edit();
		isFirstTime=pref.getBoolean(WSConstants.PROPERTY_FIRST_TIME, true);
		phoneNumber=pref.getString(WSConstants.PROPERTY_PHONE_NO,null);
		fbId=pref.getString(WSConstants.PROPERTY_FB_ID, null);
		fbAcessToken=pref.getString(WSConstants.PROPERTY_FB_ACCESSTOKEN, null);
		isNeedbuzzer=pref.getBoolean(WSConstants.PROPERTY_BUZZER, false);
		isInformFriends=pref.getBoolean(WSConstants.PROPERTY_FRIENDS,true);
		isvolunteer=pref.getBoolean(WSConstants.PROPERTY_VOLUNTEER,false);
		volunteerNumber=pref.getString(WSConstants.PROPERTY_VOLUNTEER_NUMBER, null);
		userlocation=pref.getString(WSConstants.PROPERTY_USERLOCATION, "");
		latitude=pref.getString(WSConstants.PROPERTY_USERLAT, "");
		longtitude=pref.getString(WSConstants.PROPERTY_USERLONG, "");
		isTwitterloggedin=pref.getBoolean(WSConstants.PROPERTY_TWITTER, false);
		acessToken=pref.getString(WSConstants.PROPERTY_TWITTER_ACCESS_TOKEN ,null);
		accessTokenSecret=pref.getString(WSConstants.PROPERTY_TWITTER_ACCESS_TOKEN_SECRET ,null);
	}
	
	public boolean isTwitterloggedin() {
		return isTwitterloggedin;
	}

	public void setTwitterloggedin(boolean isTwitterloggedin) {
		editor.putBoolean(WSConstants.PROPERTY_TWITTER, isTwitterloggedin);
		editor.commit();
		this.isTwitterloggedin = isTwitterloggedin;
	}

	public String getAcessToken() {
		return acessToken;
	}

	public void setAcessToken(String acessToken) {
		editor.putString(WSConstants.PROPERTY_TWITTER_ACCESS_TOKEN, acessToken);
		editor.commit();
		this.acessToken = acessToken;
	}

	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	public void setAccessTokenSecret(String accessTokenSecret) {
		editor.putString(WSConstants.PROPERTY_TWITTER_ACCESS_TOKEN_SECRET, accessTokenSecret);
		editor.commit();
		this.accessTokenSecret = accessTokenSecret;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		editor.putString(WSConstants.PROPERTY_USERLAT, latitude);
		editor.commit();
		this.latitude = latitude;
	}

	public String getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(String longtitude) {
		editor.putString(WSConstants.PROPERTY_USERLONG, longtitude);
		editor.commit();
		this.longtitude = longtitude;
	}

	public String getUserlocation() {
		return userlocation;
	}

	public void setUserlocation(String userlocation) {
		editor.putString(WSConstants.PROPERTY_USERLOCATION, userlocation);
		editor.commit();
		this.userlocation = userlocation;
	}

	public String getVolunteerNumber() {
		return volunteerNumber;
	}

	public void setVolunteerNumber(String volunteerNumber) {
		editor.putString(WSConstants.PROPERTY_VOLUNTEER_NUMBER, volunteerNumber);
		editor.commit();
		this.volunteerNumber = volunteerNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		editor.putString(WSConstants.PROPERTY_PHONE_NO, phoneNumber);
		editor.commit();
		this.phoneNumber = phoneNumber;
	}

	public String getFbId() {
		return fbId;
	}

	public void setFbId(String fbId) {
		editor.putString(WSConstants.PROPERTY_FB_ID, fbId);
		editor.commit();
		this.fbId = fbId;
	}

	public String getFbAcessToken() {
		return fbAcessToken;
	}

	public void setFbAcessToken(String fbAcessToken) {
		editor.putString(WSConstants.PROPERTY_FB_ACCESSTOKEN, fbAcessToken);
		editor.commit();
		this.fbAcessToken = fbAcessToken;
	}

	public boolean isFirstTime() {
		return isFirstTime;
	}

	public void setFirstTime(boolean isFirstTime) {
		editor.putBoolean(WSConstants.PROPERTY_FIRST_TIME, isFirstTime);
		editor.commit();
		this.isFirstTime = isFirstTime;
	}

	public boolean isNeedbuzzer() {
		return isNeedbuzzer;
	}

	public void setNeedbuzzer(boolean isNeedbuzzer) {
		editor.putBoolean(WSConstants.PROPERTY_BUZZER, isNeedbuzzer);
		editor.commit();
		this.isNeedbuzzer = isNeedbuzzer;
	}

	public boolean isInformFriends() {
		return isInformFriends;
	}

	public void setInformFriends(boolean isInformFriends) {
		editor.putBoolean(WSConstants.PROPERTY_FRIENDS, isInformFriends);
		editor.commit();
		this.isInformFriends = isInformFriends;
	}

	public boolean isIsvolunteer() {
		return isvolunteer;
	}

	public void setIsvolunteer(boolean isvolunteer) {
		editor.putBoolean(WSConstants.PROPERTY_VOLUNTEER, isvolunteer);
		editor.commit();
		this.isvolunteer = isvolunteer;
	}

	
	public void logout() {
		if(editor!=null)
		{
			
		}
	}


}
