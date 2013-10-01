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
		/*
		password = pref.getString(DListConstants.PREF_PASS, null);
		sessionId = pref.getString(DListConstants.PREF_SESSION, null);
		latitude=pref.getString(DListConstants.LATITUDE,"0");
		longitude=pref.getString(DListConstants.LONGITUDE,"0");

		deviceId=pref.getString(DListConstants.DEVICEID,null);
		devicetype=pref.getString(DListConstants.DEVICETYPE,null);
		applicationid=pref.getString(DListConstants.APPLICATIONID,null);
		applicationversion=pref.getString(DListConstants.APPLICATIONVERSION,null);
		osVersion=pref.getString(DListConstants.OSVERSION,null);
		*/
		
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

	/*
	public String getUsername() {
		return username;
	}

	

	public void setUsername(String username) {
		editor.putString(DListConstants.PREF_USER, username);
		editor.commit();
		this.username = username;
	}
	
	
	

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		editor.putString(DListConstants.PREF_PASS, password);
		this.password = password;
		editor.commit();
	}
	
	

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		editor.putString(DListConstants.PREF_SESSION, sessionId);
		editor.commit();
		this.sessionId = sessionId;
	}
	
	
	
    public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		editor.putString(DListConstants.LATITUDE, latitude);
		editor.commit();
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		editor.putString(DListConstants.LONGITUDE, longitude);
		editor.commit();
		this.longitude=longitude;
	}
	
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		editor.putString(DListConstants.DEVICEID, deviceId);
		editor.commit();
		this.deviceId = deviceId;
	}

	public String getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(String devicetype) {
		editor.putString(DListConstants.DEVICETYPE, devicetype);
		editor.commit();
		this.devicetype = devicetype;
	}

	public String getApplicationid() {
		return applicationid;
	}

	public void setApplicationid(String applicationid) {
		editor.putString(DListConstants.APPLICATIONID, applicationid);
		editor.commit();
		this.applicationid = applicationid;
	}

	public String getApplicationversion() {
		return applicationversion;
	}

	public void setApplicationversion(String applicationversion) {
		editor.putString(DListConstants.APPLICATIONVERSION, applicationversion);
		editor.commit();
		this.applicationversion = applicationversion;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		editor.putString(DListConstants.OSVERSION, osVersion);
		editor.commit();
		this.osVersion = osVersion;
	}

	/**
     * Make all the prefernces value to null and saving the prefrences
     * setting state event to LAUNCHING Again
     * 
     */
	/*
	public void logout() {
		if(editor!=null)
		{
			editor.putString(DListConstants.PREF_PASS, null);
			editor.putString(DListConstants.PREF_USER, null);
			editor.putString(DListConstants.PREF_SESSION, null);
			editor.putString(DListConstants.LATITUDE,"0");
			editor.putString(DListConstants.LONGITUDE,"0");
			
			editor.putString(DListConstants.DEVICEID, null);
			editor.putString(DListConstants.DEVICETYPE, null);
			editor.putString(DListConstants.APPLICATIONID, null);
			editor.putString(DListConstants.APPLICATIONVERSION,null);
			editor.putString(DListConstants.OSVERSION,null);
		
			this.sessionId = null;
			this.latitude="0";
			this.longitude="0";
			this.deviceId=null;
			this.devicetype=null;
			this.applicationid=null;
			this.applicationversion=null;
			this.osVersion=null;
			editor.commit();
			Log.w("Logout","Logout Successfully");
			
		}
		DListConstants.APP_STATE_EVENT = DListConstants.LAUNCHING;
	}
*/

}
