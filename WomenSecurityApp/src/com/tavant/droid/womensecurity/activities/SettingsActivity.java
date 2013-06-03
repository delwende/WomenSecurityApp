package com.tavant.droid.womensecurity.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import com.tavant.droid.womensecurity.R;

public class SettingsActivity extends PreferenceActivity{
	Preference facebookPref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		/** Setting Preferences resource to the PreferenceActivity */
		addPreferencesFromResource(R.xml.preferences);
		/*facebookPref = findPreference("facebook_key");
		facebookPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startPickerActivity(PickerActivity.FRIEND_PICKER,2 );
				return false;
			}
		});*/
	}
	protected void startPickerActivity(Uri data, int i) {
		 	/*Intent intent = new Intent();
	        intent.setData(data);
	        intent.setClass(this, PickerActivity.class);
	        startActivityForResult(intent, 2);*/		
	}
}
