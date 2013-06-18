package com.tavant.droid.womensecurity.http;

import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import com.tavant.droid.womensecurity.utils.WSConstants;
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

	
	
	
	public static HttpRequestBase createUesr(String uid,String Idtype,String phonenumber,String email,String gcmid,int app_type,String osname, String authtoken ) {
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
		put.addHeader("Content-type", "application/json");
		put.setEntity(new StringEntity(object.toString(), "utf-8"));
        return  put;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return  put;	
	}
	
	
	public static HttpRequestBase editUser(String uid,String phone,String email, String gcmid, String authtoken) {
		HttpPost post=new HttpPost(WSConstants.URL_USER_DATA);
		JSONObject object=new JSONObject();
		try{
		object.put("userid", uid);
		if(phone!=null)
		object.put("phone", phone);
		if(email!=null)
		object.put("email", email);
		if(gcmid!=null)
		object.put("gcmid", gcmid);
		if(authtoken!=null)
		object.put("authtoken", authtoken);
		post.addHeader("Content-type", "application/json");
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
		object.put("apptype", 1);
		post.addHeader("Content-type", "application/json");
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
		post.setEntity(new StringEntity(object.toString(), "utf-8"));
		System.out.println("json object >> " + object.toString());
        return post;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return post;		
	}
}
