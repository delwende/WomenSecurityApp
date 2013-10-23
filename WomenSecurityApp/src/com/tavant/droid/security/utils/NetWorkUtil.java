
package com.tavant.droid.security.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * 
 * @author Tavant
 * This class is used for checking the network connection for evry request.
 */
public class NetWorkUtil {

	private static NetWorkUtil mUtil;
	private ConnectivityManager mManager;
	private boolean isNetworkAlive = false;
	
	private NetWorkUtil(Context context){
		mManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		checkNetworkConnection();
	}
	
	public static NetWorkUtil getInstance(Context context){
		if(mUtil==null)
			mUtil = new NetWorkUtil(context);
		
		return mUtil;
	}
	
	public boolean isNetWorkAvail(){
		checkNetworkConnection();
		return isNetworkAlive;
	}
	
	private void checkNetworkConnection(){
		NetworkInfo info = mManager.getActiveNetworkInfo();
		if(info != null && info.isAvailable())
			isNetworkAlive = true;
		else 
			isNetworkAlive = false;
	}
}
