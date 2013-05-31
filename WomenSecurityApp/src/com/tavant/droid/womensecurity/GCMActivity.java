package com.tavant.droid.womensecurity;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import com.google.android.gms.gcm.GoogleCloudMessaging;
public class GCMActivity extends Activity {

	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    
    /**
     * You can use your own project ID instead. This sender is a test CCS 
     * echo server.
     */
    String GCM_SENDER_ID = "351736632081";

    // Tag for log messages.
    static final String TAG = "GCM";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    String regid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 // Make sure the app is registered with GCM and with the server
        prefs = getSharedPreferences(GCMActivity.class.getSimpleName(), 
                Context.MODE_PRIVATE);
		
		setContentView(R.layout.activity_gcmfull);
		
		 mDisplay = (TextView) findViewById(R.id.display);

	        regid = prefs.getString(PROPERTY_REG_ID, null);
	 
	        // If there is no registration ID, the app isn't registered.
	        // Call registerBackground() to register it.
	        if (regid == null) {
	            registerBackground();
	        }

	        gcm = GoogleCloudMessaging.getInstance(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void registerBackground() {
		
	    new AsyncTask <Void,Void,String>() {
	    	@Override 
	        protected String doInBackground(Void... params) {
	            String msg = "";
	            try {
	                regid = gcm.register(GCM_SENDER_ID);
	                msg = "Device registered, registration id=" + regid;

	                SharedPreferences.Editor editor = prefs.edit();
	                editor.putString(PROPERTY_REG_ID, regid);
	                editor.commit();
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	            }
	            return msg;
	        }
	        // Once registration is done, display the registration status
	        // string in the Activity's UI.
	        
	        protected void onPostExecute(String msg) {
	            mDisplay.append(msg + "\n");
	        }
	    }.execute(null, null, null);
	}

}
