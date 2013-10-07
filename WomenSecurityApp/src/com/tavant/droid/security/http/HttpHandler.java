package com.tavant.droid.security.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;

import android.util.Log;

import com.tavant.droid.security.data.BaseData;
import com.tavant.droid.security.parser.LocationResponseParser;
import com.tavant.droid.security.parser.UserDataParser;
import com.tavant.droid.security.utils.WSConstants;


/**
 * 
 * @author tavant
 * This class is used HTTP requests. It will return a BaseData model or null to BaseActivity
 * This class uses one more class for HTTP connections, i.e HttpManager.
 */
public class HttpHandler {

	private static final String TAG = "WomenSecurity";

	private HttpHandler() {

	}

	private static HttpHandler mSingleInstance;

	/**
	 * @return HttpHandler
	 */
	public static HttpHandler getInstance() {
		if (mSingleInstance == null)
			mSingleInstance = new HttpHandler();
		return mSingleInstance;
	}

	public BaseData makeHttpRequest(int reqCode, HttpRequestBase method)
	throws IOException {
		HttpResponse res = HttpManager.execute(method);
		if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
		{
			HttpEntity response = res.getEntity();
			if (response != null) {
				InputStream inputStream = response.getContent();
				switch (reqCode) {
				case WSConstants.CODE_USER_API:
					return parseUserData(inputStream);
				case WSConstants.CODE_LOCATION_API:
					return parseLocationAPI(inputStream);	
				case WSConstants.CODE_ALERT_API:
					//return parseUserData(inputStream);		
				default:
					break;
				}
				response.consumeContent();			
			}
			else
			{
				BaseData responseError = new BaseData();
				responseError.isSuccess = false;
				responseError.serverMessages = "Server Issue - Response null";
				responseError.reqCode = reqCode;
				return responseError;			
			}
			response.consumeContent();
		}// for response from server.
		else
		{
			BaseData responseError = new BaseData();
			responseError.isSuccess = false;
			responseError.serverMessages = res.getStatusLine().getReasonPhrase();
			responseError.reqCode = reqCode;
			return responseError;
		}
		return null;
	}

	
	private BaseData parseUserData(InputStream inputStream)	throws IOException {
		UserDataParser parser = new UserDataParser(inputStream);
		return parser.getData();
	}
	
	private BaseData parseLocationAPI(InputStream inputStream)	throws IOException {
		LocationResponseParser parser = new LocationResponseParser(inputStream);
		BaseData data=parser.getData();
		return data;
	}
	

private static String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		return sb.toString();
	}

}
