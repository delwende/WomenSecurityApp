package com.tavant.droid.womensecurity;

import android.app.Application;
import android.content.SharedPreferences;
public class WSecurityApp extends Application {


    static final String TAG = "TAG";
    //GoogleCloudMessaging gcm;

    SharedPreferences prefs;
    String regid;
	
	@Override
	public  void onCreate() {
		super.onCreate();
//        prefs = getSharedPreferences(getPackageName(), 
//                Context.MODE_PRIVATE);
//        regid = prefs.getString(WSConstants.PROPERTY_REG_ID, null);
//        gcm = GoogleCloudMessaging.getInstance(this);
//	    if (regid == null) {
//	            registerBackground();
//	     }		
	}	
//	private void registerBackground() {
//	    new AsyncTask <Void,Void,String>() {
//	    	@Override 
//	        protected String doInBackground(Void... params) {
//	            String msg = "";
//	            try {
//	                regid = gcm.register(WSConstants.GCM_SENDER_ID);
//	                msg = "Device registered, registration id=" + regid;
//	                SharedPreferences.Editor editor = prefs.edit();
//	                editor.putString(WSConstants.PROPERTY_REG_ID, regid);
//	                editor.commit();
//	            } catch (IOException ex) {
//	                msg = "Error :" + ex.getMessage();
//	            }
//	            Log.i(TAG,"registartion id"+msg);
//	            return msg;
//	        }
//	        protected void onPostExecute(String msg) {  
//	        }
//	    }.execute(null, null, null);
//	}

}
