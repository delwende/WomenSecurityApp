package com.tavant.droid.womensecurity.activities;

import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpRequestBase;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.tavant.droid.womensecurity.BaseActivity;
import com.tavant.droid.womensecurity.R;
import com.tavant.droid.womensecurity.data.BaseData;
import com.tavant.droid.womensecurity.http.HttpRequestCreater;
import com.tavant.droid.womensecurity.utils.WSConstants;

public class FacebookLoginActivty extends BaseActivity {

	private Fragment fragment=null;
	private boolean isResumed = false;
	private UiLifecycleHelper uiHelper=null;
	private SharedPreferences prefs=null;
	private Editor edit=null;
	private ProgressDialog mdialog=null;
	private TelephonyManager phoneManager=null;
	private String fbacesstoken=null;
	
	private Session.StatusCallback callback = 
		    new Session.StatusCallback() {
		    @Override
		    public void call(Session session, 
		            SessionState state, Exception exception) {
		        onSessionStateChange(session, state, exception);
		    }
   };
	
	
   
   private void onSessionStateChange(Session session,SessionState state,Exception exception){   
	   if (isResumed) {
	        if (state.isOpened()) {
	        	Log.i("TAG", "my accesstoken"+session.getAccessToken());
	        	fbacesstoken=session.getAccessToken();
	        	edit.putString(WSConstants.PROPERTY_FB_ACCESSTOKEN,session.getAccessToken() );
	        	//edit.putString(WSConstants.PROPERTY_FB_ACCESSTOKEN,session.getExpirationDate());
	        	//edit.putString(WSConstants.PROPERTY_FB_ACCESSTOKEN,session.());
	        	edit.commit();
	        	mdialog=ProgressDialog.show(FacebookLoginActivty.this, "", "fetching user details...");
	        	makeMeRequest(session);
	        } else if (state.isClosed()) {
	            // show error here
	        }
	    }
   }
  
	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a 
	    // new callback to handle the response.
	    Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	            if (session == Session.getActiveSession()) {
	                if (user != null) {
	                	Log.i("TAG","myfbid"+user.getId());
	                	edit.putString(WSConstants.PROPERTY_FB_ID,user.getId());
	                	edit.commit();
	                    //LaunchSettingsScreen();     
	                	createUser(user.getId());
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    request.executeAsync();
   }
	
	
	private void createUser(final String fbid){
		String phoneNumber =""; 
		phoneNumber=phoneManager.getLine1Number();
		String possibleEmail="";
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(this).getAccounts();
		for (Account account : accounts) {
		    if (emailPattern.matcher(account.name).matches()) {
		        possibleEmail = account.name;
		        if(possibleEmail.equals(""))
		        	 break;
		    }
		}
		String gcmid=prefs.getString(WSConstants.PROPERTY_REG_ID, "");
		HttpRequestBase put=HttpRequestCreater.createUesr(fbid, "facebook", phoneNumber, possibleEmail, gcmid, 0,"android",fbacesstoken);
		onExecute(WSConstants.CODE_USER_API, put, false);
	}
	
	
	
	
	
	private void LaunchSettingsScreen(){
		Intent intent=new Intent(this, SettingsActivity.class);
		startActivity(intent);
		finish();
	}
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = getSharedPreferences(getPackageName(), 
                Context.MODE_PRIVATE);
		edit=prefs.edit();
		Session session=new Session(FacebookLoginActivty.this);
		phoneManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		//mdialog=new ProgressDialog(FacebookLoginActivty.this);
		if(session.getAccessToken()!=null&&prefs.getString(WSConstants.PROPERTY_FB_ID, null)!=null&&
				prefs.getString(WSConstants.PROPERTY_FB_ACCESSTOKEN, null) != null){
			//TODO write condition to check that app is configured settings , if configured launch home screen
			LaunchSettingsScreen();	
		}else{
			uiHelper= new UiLifecycleHelper(this, callback);
			setContentView(R.layout.splash);
			FragmentManager fm = getSupportFragmentManager();
			fragment = fm.findFragmentById(R.id.login_fragment);	 			
		}	
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    isResumed = true;
	    uiHelper.onResume();
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    isResumed = false;
	}
	
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		uiHelper.onActivityResult(arg0, arg1, arg2);
	}
    	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onComplete(int reqCode, BaseData data) {
		// TODO Auto-generated method stub
		if(data.isSuccess){
		 mdialog.dismiss();
		 LaunchSettingsScreen();
		}
	}

	@Override
	protected void onError(int reqCode, int errorCode, String errorMessage) {
		 mdialog.dismiss();
		 Toast.makeText(FacebookLoginActivty.this,errorMessage, Toast.LENGTH_SHORT).show();
		 LaunchSettingsScreen();
		
	}
	
	
	
	
	
	

}
