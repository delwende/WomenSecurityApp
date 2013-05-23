package com.tavant.droid.womensecurity.activities;

import com.tavant.droid.womensecurity.R;
import com.tavant.droid.womensecurity.R.xml;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		/** Setting Preferences resource to the PreferenceActivity */
		addPreferencesFromResource(R.xml.preferences);
	}
}
