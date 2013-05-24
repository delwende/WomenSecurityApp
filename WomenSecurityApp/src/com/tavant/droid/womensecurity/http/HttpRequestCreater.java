package com.tavant.droid.womensecurity.http;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

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

	public static HttpRequestBase getAppConfigReq(String gcmid, String osversion,String phonenumber, String mcc,String mnc,String uid, int type) {
		HttpPost post=new HttpPost(WSConstants.URL_USER_DATA);
		mListNameValuePair = new Vector<NameValuePair>();
		mListNameValuePair.add(new BasicNameValuePair("userId", uid));
		mListNameValuePair.add(new BasicNameValuePair("osVersion", osversion));		
		mListNameValuePair.add(new BasicNameValuePair("phoneNumber", phonenumber));
		mListNameValuePair.add(new BasicNameValuePair("gcmId", gcmid));	
		mListNameValuePair.add(new BasicNameValuePair("userIdType", ""+type));	
		getPostURLForm(post);
		return post;	
	}
	
	private static void getPostURLForm(HttpPost post) {
		try {
			post.setEntity(new UrlEncodedFormEntity(mListNameValuePair,
					HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
