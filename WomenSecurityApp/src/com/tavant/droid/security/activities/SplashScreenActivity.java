package com.tavant.droid.security.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import com.tavant.droid.security.HomeActivity;
import com.tavant.droid.security.R;
import com.tavant.droid.security.prefs.CommonPreferences;

public class SplashScreenActivity extends Activity {
	
	/**
	 * class for showing the 
	 */
	private CommonPreferences pref=null;
	private Handler handler=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref=CommonPreferences.getInstance();
		//pref.load(this);
		if(pref.isFirstTime()){
			setContentView(R.layout.splashscreen);
			handleSplash();
		}else{
			startLoginScreen();
		}
	}
	
	private void handleSplash(){
		handler=new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				pref.setFirstTime(false);
				startLoginScreen();
			}
		}, 3000L);
	}
	
	private void startLoginScreen() {
		Intent intent=new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	public int pxToDp(int px) {
	    DisplayMetrics displayMetrics =getResources().getDisplayMetrics();
	    int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	    Log.i("TAG","px in dp"+dp);
	    return dp;
	}

}
