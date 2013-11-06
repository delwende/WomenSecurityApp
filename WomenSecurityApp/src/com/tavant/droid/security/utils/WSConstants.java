
/**
 *  @author Tavant
 *
 * This class is contains all constants
 * 
 */

package com.tavant.droid.security.utils;


public class WSConstants {
	
	
	
	
	public static final String PROTOCOL="https://";	  //https://
	public static final String HOST = "gladio.tavant.com";    //10.129.146.78  //121.240.130.119  //54.254.141.98  //gladio.tavant.com
	public static final String BASEURL =PROTOCOL+HOST+"/twsa/frontend_dev.php/API/%s";	//	"http://192.168.2.2:8080/WomenSecurityBackEnd";

	
	public static final String URL_USER_DATA = String.format(BASEURL, "user");
    public static final String URL_USER_LOCATION = String.format(BASEURL, "location");
    public static final String URL_USER_ALERT =  String.format(BASEURL, "send");
    
   
	
	public static final String PREF_NAME="common_pref";
	public static final String FACEBOOK_ACCESS_TOKEN = "fb_access_token";


	public static final int USER_ID_FB = 0;
	public static final int USER_ID_GOOGLE = 1;
	public static final int USER_ID_TWITTER = 2;



	public static final String GCM_SENDER_ID1 = "351736632081";



	public static final String TYPE= "type";
    public static final int TYPE_LOGIN =1;
    public static final int TYPE_TERMS =2;
    public static final int TYPE_HELP =3;
    
    
	
	
	public static final String PROPERTY_REG_ID = "gcm_id";
	public static final String PROPERTY_USER_ID = "user_id";
	public static final String PROPERTY_FB_ID = "fb_id";
	public static final String PROPERTY_FB_ACCESSTOKEN = "fb_access_token";
	public static final String PROPERTY_FIRST_TIME = "first_time";
	public static final String PROPERTY_PHONE_NO = "phone_num";
	public static final String PROPERTY_BUZZER = "buzzer_key";
	public static final String PROPERTY_FRIENDS = "friends_key";
	public static final String PROPERTY_VOLUNTEER = "volunteer_key";
	public static final String PROPERTY_VOLUNTEER_NUMBER = "volunteer_number";
	public static final String PROPERTY_USERLOCATION = "user_location";
	public static final String PROPERTY_USERLAT = "lat";
	public static final String PROPERTY_USERLONG = "lat";
	
	public static final String PROPERTY_TWITTER = "isTwitterLogin";
	public static final String PROPERTY_TWITTER_ACCESS_TOKEN = "acess_token";
	public static final String PROPERTY_TWITTER_ACCESS_TOKEN_SECRET = "acees_token_secret";
	
	
	
	
	

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

