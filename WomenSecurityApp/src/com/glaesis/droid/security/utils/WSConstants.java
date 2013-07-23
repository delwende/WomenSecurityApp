
/**
 *  @author Tavant
 *
 * This class is contains all constants
 * 
 */

package com.glaesis.droid.security.utils;


public class WSConstants {
	
	public static final String HOST = "";
	public static final String BASEURL = "http://192.168.2.2:8080/WomenSecurityBackEnd";	//	"http://192.168.2.2:8080/WomenSecurityBackEnd";



	public static final String FACEBOOK_APP_KEY = "";

	public static final String FACEBOOK_ACCESS_TOKEN = "fb_access_token";

	public static final String TWITTER_CONSUMER_KEY = "";
	public static final String TWITTER_SECRET_KEY = "";

	public static final int USER_ID_FB = 0;
	public static final int USER_ID_GOOGLE = 1;
	public static final int USER_ID_TWITTER = 2;



	public static final String GCM_SENDER_ID1 = "339234639849";  //  
	public static final String GCM_SENDER_ID2 = "351736632081";
	public static final String FB_APP_KEY = "417172928381965";



	public static final String PROPERTY_REG_ID = "gcm_id";
	public static final String PROPERTY_USER_ID = "user_id";
	public static final String PROPERTY_FB_ID = "fb_id";
	public static final String PROPERTY_FB_ACCESSTOKEN = "fb_access_token";
	
	public static final String PROPERTY_PHONE_NO = "phone_num";

	public static final String URL_USER_DATA = BASEURL+"/user";
	public static final String URL_USER_LOCATION = BASEURL+"/updatelocation";
	public static final String URL_USER_ALERT = BASEURL+"/UserAlert";

	public static final int CODE_USER_API = 1001;
	public static final int CODE_LOCATION_API = 1002;
	public static final int CODE_ALERT_API = 1003;
}

