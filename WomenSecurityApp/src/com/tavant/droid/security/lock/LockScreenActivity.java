package com.tavant.droid.security.lock;


import com.tavant.droid.security.BaseActivity;
import com.tavant.droid.security.HomeActivity;
import com.tavant.droid.security.activities.SettingsActivity;
import com.tavant.droid.security.data.BaseData;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import group.pals.android.lib.ui.lockpattern.prefs.SecurityPrefs;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


public class LockScreenActivity extends BaseActivity {
	
	
	  public static final int REQ_CREATE_PATTERN = 0;
	  public static final int REQ_ENTER_PATTERN = 1;
	  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SecurityPrefs.setAutoSavePattern(this, true);
        SecurityPrefs.setEncrypterClass(this, LPEncrypter.class);
        // TODO prashanth have to check here is it for creating the pattern or validating the pattern
        if(true){
        Intent intentActivity = new Intent(
                LockPatternActivity.ACTION_CREATE_PATTERN,
                null, this,
                LockPatternActivity.class);
        startActivityForResult(intentActivity,
                REQ_CREATE_PATTERN);
        }else{
        	 Intent intentActivity = new Intent(
                     LockPatternActivity.ACTION_COMPARE_PATTERN,
                     null, this,
                     LockPatternActivity.class);
        	 
        	 SharedPreferences prfs = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        	 String pattaren = prfs.getString("drawpattern", null);
        	 intentActivity.putExtra(LockPatternActivity.EXTRA_PATTERN, pattaren.toCharArray());

             startActivityForResult(intentActivity,
                     REQ_ENTER_PATTERN);
        }
	}
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        
        case REQ_ENTER_PATTERN: {
            int msgId = 0;
            switch (resultCode) {
            case RESULT_OK:
                msgId = android.R.string.ok;
                Intent intent=new Intent(LockScreenActivity.this, SettingsActivity.class);
    			intent.putExtra("issetting", true);
    			startActivity(intent);
                break;
            case RESULT_CANCELED:
                msgId = android.R.string.cancel;
                break;
            case LockPatternActivity.RESULT_FAILED:
                msgId = 0;
                break;
            default:
                return;
            }
            break;
        }
        default:
        break;
        }
    }// onActivityResult()


	@Override
	protected void onComplete(int reqCode, BaseData data) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void onError(int reqCode, int errorCode, String errorMessage) {
		// TODO Auto-generated method stub
		
	}
}
