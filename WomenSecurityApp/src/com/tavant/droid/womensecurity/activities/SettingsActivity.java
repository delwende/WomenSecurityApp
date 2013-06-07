package com.tavant.droid.womensecurity.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import com.facebook.widget.PickerFragment;
import com.tavant.droid.womensecurity.R;

public class SettingsActivity extends PreferenceActivity{
	Preference facebookPref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		/** Setting Preferences resource to the PreferenceActivity */
		addPreferencesFromResource(R.xml.preferences);
		facebookPref = findPreference("facebook_key");
		facebookPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startPickerActivity();
				return true;
			}
		});
	}
	protected void startPickerActivity() {
		 	Intent intent = new Intent();
	       // intent.setData(PickerActivity.FRIEND_PICKER);
	        intent.setClass(this, FBFriendListActivity.class);
	        startActivityForResult(intent, 2000);		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
}
