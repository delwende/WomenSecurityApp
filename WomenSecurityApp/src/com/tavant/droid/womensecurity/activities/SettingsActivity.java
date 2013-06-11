package com.tavant.droid.womensecurity.activities;


import group.pals.android.lib.ui.lockpattern.prefs.SecurityPrefs;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import com.facebook.widget.PickerFragment;
import com.tavant.droid.womensecurity.R;
import com.tavant.droid.womensecurity.lock.LPEncrypter;

public class SettingsActivity extends PreferenceActivity{
	Preference facebookPref=null;
	Preference pattrenpref=null;

	public static final int REQ_CREATE_PATTERN = 0;
	public static final int REQ_ENTER_PATTERN = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		SecurityPrefs.setAutoSavePattern(this, true);
        SecurityPrefs.setEncrypterClass(this, LPEncrypter.class);
		/** Setting Preferences resource to the PreferenceActivity */
		addPreferencesFromResource(R.xml.preferences);
		facebookPref = findPreference("facebook_key");
		pattrenpref=findPreference("security_key");
		facebookPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				startPickerActivity();
				return true;
			}
		});

		pattrenpref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				//startPatternLockActivity();
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
