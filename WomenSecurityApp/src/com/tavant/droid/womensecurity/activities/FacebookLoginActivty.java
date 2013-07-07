package com.tavant.droid.womensecurity.activities;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpRequestBase;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.chat.XMPPManager;
import com.facebook.chat.XMPPManager.XMPPChatListener;
import com.facebook.model.GraphUser;
import com.google.android.gcm.GCMRegistrar;
import com.tavant.droid.womensecurity.BaseActivity;
import com.tavant.droid.womensecurity.R;
import com.tavant.droid.womensecurity.data.BaseData;
import com.tavant.droid.womensecurity.http.HttpRequestCreater;
import com.tavant.droid.womensecurity.service.LocationAlarmService;
import com.tavant.droid.womensecurity.utils.PhoneStatus;
import com.tavant.droid.womensecurity.utils.WSConstants;

public class FacebookLoginActivty extends BaseActivity implements PhoneStatus {

	private android.support.v4.app.Fragment fragment=null;
	private boolean isResumed = false;
	private UiLifecycleHelper uiHelper=null;
	private SharedPreferences prefs=null;
	private Editor edit=null;
	private ProgressDialog mdialog=null;
	private TelephonyManager phoneManager=null;
	private String fbacesstoken="";
	private String mgcmId="";
	private String phonenumber="";
	private String userId="";
	private AlertDialog.Builder alert = null;
	private EditText text = null;
	String phoneNo="";
	private PhoneStatus mctx=null;
	
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
	      //  	setupXMPPLogin(fbacesstoken);
	        	edit.putString(WSConstants.PROPERTY_FB_ACCESSTOKEN,session.getAccessToken() );
	        	edit.commit();
	        	mdialog=ProgressDialog.show(FacebookLoginActivty.this, "", "fetching user details...");
	        	makeMeRequest(session);
	        } else if (state.isClosed()) {
	            // show error here
	        }
	    }
   }
  
	private void makeMeRequest(final Session session) {
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
	                	userId=user.getId();
	                	mdialog.dismiss();
	                	//CustomPhoneDialog phoneDialog=new CustomPhoneDialog(FacebookLoginActivty.this);
	                	//phoneDialog.show();
	                	
	           		    alert.setMessage("Please Enter Your Mobile No");
	           		    alert.setView(text);
	           		    alert.setPositiveButton("Register", new DialogInterface.OnClickListener() {
	           		        public void onClick(DialogInterface dialog, int whichButton) {
	           		            String value = text.getText().toString().trim();
	           		            Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
	           		            if(text.getText().toString().length()==0)
	           		    			return;
	           		    		else{
	           		    			phoneNo=text.getText().toString();
	           		    			mctx.onEntered(phoneNo);
	           		    		}
	           		        }
	           		    });
	           		 alert.show();
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    request.executeAsync();
   }
	
	
	
	@Override
	public void onEntered(String PhoneNo) {
		phonenumber=PhoneNo;
		edit.putString(WSConstants.PROPERTY_PHONE_NO, phonenumber);
		edit.commit();
		createUser();
	}
	
	private void createUser(){
		mdialog=ProgressDialog.show(FacebookLoginActivty.this, "", "fetching user details...");
		String phoneNumber =""; 
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
		String gcmid=GCMRegistrar.getRegistrationId(this);
		HttpRequestBase put=HttpRequestCreater.createUesr(userId, "facebook", phonenumber, possibleEmail, gcmid, 0,"android",fbacesstoken);
		onExecute(WSConstants.CODE_USER_API, put, false);
	}
	
	
	
	
	
	private void LaunchSettingsScreen(boolean isexplict){
		Intent intent=new Intent(this, SettingsActivity.class);
		intent.putExtra("issetting", isexplict);
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		alert = new AlertDialog.Builder(this);
		text = new EditText(this);
		mctx=(PhoneStatus)this;
		prefs = getSharedPreferences(getPackageName(), 
                Context.MODE_PRIVATE);
		edit=prefs.edit();
		Session session=new Session(FacebookLoginActivty.this);
		phoneManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		
		String phoneNo=prefs.getString(WSConstants.PROPERTY_PHONE_NO, null);
		
		//mdialog=new ProgressDialog(FacebookLoginActivty.this);
		if(session.getAccessToken()!=null&&prefs.getString(WSConstants.PROPERTY_FB_ID, null)!=null&&
				prefs.getString(WSConstants.PROPERTY_FB_ACCESSTOKEN, null) != null&&phoneNo!=null){
			//TODO write condition to check that app is configured settings , if configured launch home screen
			LaunchSettingsScreen(false);	
		}else{
			uiHelper= new UiLifecycleHelper(this, callback);
			setContentView(R.layout.splash);
			android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
			fragment = fm.findFragmentById(R.id.login_fragment);	 			
		}	
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		mgcmId = GCMRegistrar.getRegistrationId(this);
		if (mgcmId.equals("")) {
			GCMRegistrar.register(this, WSConstants.GCM_SENDER_ID1,WSConstants.GCM_SENDER_ID2 );
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
		 raiseLocationUpdateAlarm();
		 LaunchSettingsScreen(false);
		}
	}

	@Override
	protected void onError(int reqCode, int errorCode, String errorMessage) {
		 mdialog.dismiss();
		 Toast.makeText(FacebookLoginActivty.this,errorMessage, Toast.LENGTH_SHORT).show();
		 LaunchSettingsScreen(false);
		
	}

	
	
	
	private void raiseLocationUpdateAlarm() {
		Intent myIntent = new Intent(this,
				LocationAlarmService.class);
		//startService(myIntent);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0,
				myIntent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTimeInMillis(System.currentTimeMillis());
//		calendar.add(Calendar.SECOND, 10);
		long firstTime = SystemClock.elapsedRealtime();
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				firstTime, 3*60* 1000, pendingIntent);
	}
	
	
	
	
	
	
	
}





