package com.tavant.droid.security.activities;


import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import group.pals.android.lib.ui.lockpattern.prefs.SecurityPrefs;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.tavant.droid.security.BaseActivity;
import com.tavant.droid.security.R;
import com.tavant.droid.security.adapters.SettingsAdapter;
import com.tavant.droid.security.data.BaseData;
import com.tavant.droid.security.http.HttpRequestCreater;
import com.tavant.droid.security.lock.LPEncrypter;
import com.tavant.droid.security.prefs.CommonPreferences;
import com.tavant.droid.security.utils.VolunteerStatus;
import com.tavant.droid.security.utils.WSConstants;

public class SettingsActivity extends BaseActivity implements OnItemClickListener,VolunteerStatus{
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
    private ListView listview=null;
    
    private boolean issettings=false;
	
    private  int title[]={R.string.str_facebook,
    		R.string.str_contacts,R.string.str_buzzer,
    		R.string.str_friends,R.string.str_volunteer,
    		R.string.str_pattern};
	private int desc[]={R.string.str_facebook_desc,
			R.string.str_contacts_desc,R.string.str_buzzer_desc,
			R.string.str_friends_desc,R.string.str_volunteer_desc,
			R.string.str_pattern_desc};
	
	private SettingsAdapter adapter=null;
	private CommonPreferences prefs=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendslist);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		listview=(ListView) findViewById(R.id.friendslist);
		prefs=CommonPreferences.getInstance();
		adapter=new SettingsAdapter(this,title,desc);
		listview.setAdapter(adapter);
		listview.setVisibility(View.VISIBLE);
		listview.setOnItemClickListener(this);
		SecurityPrefs.setAutoSavePattern(this, true);
        SecurityPrefs.setEncrypterClass(this, LPEncrypter.class);
		
		/*
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
		*/
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg1.getId()) {
		case 0:
			starPickerActivity("facebook");
		case 1:
			starPickerActivity("contacts");
	    break;
		case 5:
			startPatternLockActivity();
		break;
		default:
			break;
		}		
	}

	protected void startPatternLockActivity() {
		Intent intentActivity = new Intent(
                LockPatternActivity.ACTION_CREATE_PATTERN,
                null, this,
                LockPatternActivity.class);
        startActivityForResult(intentActivity,
                REQ_CREATE_PATTERN);
		
	}
	
	
	
	private void starPickerActivity(String type) {
		Intent intent = new Intent();
		// intent.setData(PickerActivity.FRIEND_PICKER);
		intent.setClass(this, FBFriendListActivity.class);
		intent.putExtra("type", type);
		startActivityForResult(intent, 2000);		
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
        case REQ_CREATE_PATTERN: 
            if (resultCode == RESULT_OK){
            	 char array[]=data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN); 
            Log.d("drawpattern",new String(array)); 
            // Save this prefrnce and read it from prefrence when we need to validate
            SharedPreferences preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("drawpattern",new String(array));
            editor.commit();
            } 
            break;
        }// REQ_ENTER_PATTERN
    }// onActivityResult()

	@Override
	public void changetoVolunteer(boolean status) {
		HttpRequestBase post=HttpRequestCreater.editUser(prefs.getFbId(), null,null,null,null,status ? 1:0);
		onExecute(WSConstants.CODE_EDIT_USER, post, false);
	}

	@Override
	protected void onComplete(int reqCode, BaseData data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onError(int reqCode, int errorCode, String errorMessage) {
		// TODO Auto-generated method stub
		
	}

}
	


