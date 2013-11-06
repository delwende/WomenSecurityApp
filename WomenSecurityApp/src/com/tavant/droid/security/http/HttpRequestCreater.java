package com.tavant.droid.security.http;

import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

//import android.util.Log;

import com.tavant.droid.security.utils.WSConstants;
/**
 * 
 * @author tavant
 * 
 * This class is used for preparing the Http requests for all Api's
 *
 */

public class HttpRequestCreater {

	private static Vector<NameValuePair> mListNameValuePair;
	/////////////////////////////////////////////////////// NON-SESSION API'S

	
	
	
	public static HttpRequestBase createUesr(String uid,String Idtype,String phonenumber,String email,String gcmid,
			int app_type,String osname, String authtoken, String userName) {
		HttpPut put=new HttpPut(WSConstants.URL_USER_DATA);
		JSONObject object=new JSONObject();
		try{
		object.put("userid", uid);
		object.put("idtype", Idtype);
		object.put("phone", phonenumber);
		object.put("email", email);
		object.put("gcmid", gcmid);
		object.put("apptype", app_type);
		object.put("osname", osname);
		object.put("authtoken", authtoken);
		object.put("username", userName);

		
		put.addHeader("Content-type", "application/json");
		put.addHeader("api-key", "R2xhZGlvQDEyMy10YXZhbnQuY29t");
		put.setEntity(new StringEntity(object.toString(), "utf-8"));
        return  put;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return  put;	
	}
	
	
	public static HttpRequestBase editUser(String uid,String phone,String email, String gcmid, String authtoken,
			int app_type,String userName) {
		HttpPut post=new HttpPut(WSConstants.URL_USER_DATA);
		JSONObject object=new JSONObject();
		try{
		object.put("userid", uid);
		object.put("apptype", app_type);
		if(phone!=null)
		object.put("phone", phone);
		else if(email!=null)
		object.put("email", email);
		else if(gcmid!=null)
		object.put("gcmid", gcmid);
		else if(authtoken!=null)
		object.put("authtoken", authtoken);
		else if(userName!=null)
		object.put("username", userName);
		post.addHeader("Content-type", "application/json");
		post.addHeader("api-key", "R2xhZGlvQDEyMy10YXZhbnQuY29t");
		post.setEntity(new StringEntity(object.toString(), "utf-8"));
        return post;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return post;	
	}
	
	
	public static HttpRequestBase updateLocation(String userid, double latitude,double longitude,int app_type){
		HttpPost post=new HttpPost(WSConstants.URL_USER_LOCATION);
		JSONObject object=new JSONObject();
		try{
		object.put("userid", userid);
		object.put("latitude", latitude);
		object.put("longitude", longitude);
		object.put("apptype", app_type);
		post.addHeader("Content-type", "application/json");
		post.addHeader("api-key", "R2xhZGlvQDEyMy10YXZhbnQuY29t");
		post.setEntity(new StringEntity(object.toString(), "utf-8"));
        return post;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return post;		
	}
	
	public static HttpRequestBase alertUsers(JSONArray phoneNumber,String userId){
		HttpPost post=new HttpPost(WSConstants.URL_USER_ALERT);
		JSONObject object=new JSONObject();
		try{
		object.put("phonenumber", phoneNumber);
		object.put("userid", userId);
		post.addHeader("Content-type", "application/json");
		post.addHeader("api-key", "R2xhZGlvQDEyMy10YXZhbnQuY29t");
		post.setEntity(new StringEntity(object.toString(), "utf-8"));
		System.out.println("json object >> " + object.toString()+post.getURI().toString());
        return post;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return post;		
	}
}
