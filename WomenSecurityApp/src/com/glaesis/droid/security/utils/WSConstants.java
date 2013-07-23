
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
	
	
	public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhTdp9Fl6kmzxRqpiXWco/O00XsZb9RWdLgpkLS2KzWthw9oOhoUR14BW7EdQL3iGrG/foqy/8QcIbmUeVb/OHNLPRgvHsw8Kj0yOPVPnwXIKJFv+YxRZUMq8l8XPfWDVosSPMAnZ9eIELC6lsrFFOECiyVIFWX8JO6DSVgeWOtPe0t+I9Ozqb26/AHuYPIrhDlU23asKTm3ipW13bBQt5IXw2zQK5RHedpVD7m73wh2hPgfZBhAIHor/ZEdJpW7TbUazwbHyEBBEoFPaXpqrkLwIRZ16iwByQyd8kd325FGJ2k99yzArYWUct7716/eezqQmREFNbw4peO9+A5MRIwIDAQAB";

	 // Generate your own 20 random bytes, and put them here.
	public static final byte[] SALT = new byte[] {
	   -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95, -45, 77, -117, -36, -113, -11, 32, -64,
	    89
	 };
}

