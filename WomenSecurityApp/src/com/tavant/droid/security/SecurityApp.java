package com.tavant.droid.security;

import com.tavant.droid.security.prefs.CommonPreferences;
import com.tavant.droid.security.utils.FontLoader;

import android.app.Application;
import android.content.SharedPreferences;
public class SecurityApp extends Application {

	@Override
	public  void onCreate() {
		super.onCreate();
		FontLoader.getMngr().setTypeFont(getAssets());
		CommonPreferences.getInstance().load(getApplicationContext());
	}	


}
