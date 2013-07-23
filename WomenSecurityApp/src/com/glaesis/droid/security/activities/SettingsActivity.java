package com.glaesis.droid.security.activities;


import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import group.pals.android.lib.ui.lockpattern.prefs.SecurityPrefs;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.glaesis.droid.security.HomeActivity;
import com.glaesis.droid.security.database.ContentDescriptor;
import com.glaesis.droid.security.lock.LPEncrypter;
import com.tavant.droid.womensecurity.R;

public class SettingsActivity extends PreferenceActivity{
	Preference facebookPref=null;
	Preference pattrenpref=null;
	Preference buzzerPref=null;
	Preference friendsPref=null;
	SharedPreferences pref=null;

	public static final int REQ_CREATE_PATTERN = 0;
	public static final int REQ_ENTER_PATTERN = 1;
    private ContentResolver resolver;
    private Cursor fbCursor=null;
    private Cursor friendCursor=null;
    
    private boolean issettings=false;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		resolver=getContentResolver();
		fbCursor =resolver.query(ContentDescriptor.WSFacebook.CONTENT_URI, null, null, null, null);
		friendCursor=resolver.query(ContentDescriptor.WSContact.CONTENT_URI, null, null, null, null);
		issettings=getIntent().getBooleanExtra("issetting", false);
		if(fbCursor!=null&&fbCursor.getCount()>0&&friendCursor!=null&&friendCursor.getCount()>0&&!issettings){
			fbCursor.close();
			friendCursor.close();
		    startActivity(new Intent(this, HomeActivity.class));	
		    finish();
		    return;
		}
		resolver=getContentResolver();
		SecurityPrefs.setAutoSavePattern(this, true);
        SecurityPrefs.setEncrypterClass(this, LPEncrypter.class);
		/** Setting Preferences resource to the PreferenceActivity */
		addPreferencesFromResource(R.xml.preferences);
		facebookPref = findPreference("facebook_key");
		pattrenpref=findPreference("security_key");
		buzzerPref=findPreference("buzzer_key");
		friendsPref=findPreference("friends_key");
		
		pref= getApplicationContext().getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
		final Editor editor = pref.edit();
		editor.commit();
		
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
				startPatternLockActivity();
				return true;
			}
		});
		
buzzerPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if((Boolean) newValue){
				editor.putBoolean("buzzer_key", true);
				}else{
					editor.putBoolean("buzzer_key", false);
				}
				editor.commit();
				return true;
			}
		});
		
		friendsPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if((Boolean) newValue){
                editor.putBoolean("friends_key", true);
				}else{
					editor.putBoolean("friends_key", false);
				}
                editor.commit();
				return true;
			}
		});
	}
	
	protected void startPatternLockActivity() {
		Intent intentActivity = new Intent(
                LockPatternActivity.ACTION_CREATE_PATTERN,
                null, this,
                LockPatternActivity.class);
        startActivityForResult(intentActivity,
                REQ_CREATE_PATTERN);
		
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
		switch (requestCode) {
        case REQ_CREATE_PATTERN: 
            if (resultCode == RESULT_OK){
            	 char array[]=data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN); 
            Log.i("drawpattern",new String(array)); 
            // Save this prefrnce and read it from prefrence when we need to validate
            SharedPreferences preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("drawpattern",new String(array));
            editor.commit();
            } 
            break;
        }// REQ_ENTER_PATTERN
    }// onActivityResult()
}
