package com.tavant.droid.security.activities;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpRequestBase;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gcm.GCMRegistrar;
import com.tavant.droid.security.BaseActivity;
import com.tavant.droid.security.R;
import com.tavant.droid.security.data.BaseData;
import com.tavant.droid.security.http.HttpRequestCreater;
import com.tavant.droid.security.prefs.CommonPreferences;
import com.tavant.droid.security.service.LocationAlarmService;
import com.tavant.droid.security.utils.CustomDialog;
import com.tavant.droid.security.utils.NetWorkUtil;
import com.tavant.droid.security.utils.PhoneStatus;
import com.tavant.droid.security.utils.WSConstants;

public class LoginActivity extends BaseActivity implements PhoneStatus{
	
	private TextView versionText=null;
	private TextView termsText=null;
	private static final int REQ_CODE=125;
	private String mdeviceId =null;
	private LoginButton loginbtn=null;
	private CustomDialog alert = null;
	private CommonPreferences preferences;
	private TelephonyManager phoneManager=null;
	private UiLifecycleHelper uiHelper=null;
	private String mgcmId="";
	private static Handler handler=new Handler();
	private ProgressDialog mdialog=null;
	private boolean isResumed = false;
	private EditText phoneText = null;
	private EditText userNametext = null;
	
	private String phonenumber="";
	private String userName="";
	private PhoneStatus mctx=null;
	private Pattern emailPattern = null;
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	
	
	
	
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
				loginbtn.setVisibility(View.INVISIBLE);
				Log.d("TAG", "my accesstoken"+session.getAccessToken());
				preferences.setFbAcessToken(session.getAccessToken());
				mdialog=ProgressDialog.show(this, "",getString(R.string.fetch_details));
				makeMeRequest(session);
			} else if (state.isClosed()) {
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
						Log.d("TAG","myfbid"+user.getId());
						preferences.setFbId(user.getId());
						mdialog.dismiss();
						alert.setTitle(getString(R.string.app_name));
						alert.show();
					}
				}
				if (response.getError() != null) {
					handler.post(new DisplayToast(getString(R.string.error_login),LoginActivity.this));
				}
			}
		});
		request.executeAsync();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		alert = new CustomDialog(LoginActivity.this);
		emailPattern=Pattern.compile(EMAIL_PATTERN);
		mctx=(PhoneStatus)this;
		mdeviceId =Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		preferences=CommonPreferences.getInstance();
		Session session=new Session(LoginActivity.this);
		phoneManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		if(session.getAccessToken()!=null&&preferences.getFbId()!=null&&
				preferences.getFbAcessToken() != null&&preferences.getPhoneNumber()!=null){
			LaunchSettingsScreen(false);	
		}else{
			uiHelper= new UiLifecycleHelper(this, callback); 	
			setContentView(R.layout.login);
			loginbtn=(LoginButton)findViewById(R.id.fb_btn);
			loginbtn.setReadPermissions(Arrays.asList("xmpp_login", "user_online_presence","friends_online_presence","read_friendlists","read_stream","read_requests",
					"read_friendlists")); 
			versionText=(TextView)findViewById(R.id.version);
			versionText.setText(versionText.getText()+getApplicationversionName());
			termsText=(TextView)findViewById(R.id.terms);
			termsText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivityForResult(new Intent(LoginActivity.this, TermsAbout.class), REQ_CODE);
				}
			});
		}
		
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		mgcmId = GCMRegistrar.getRegistrationId(this);
		if (mgcmId.equals("")) {
			GCMRegistrar.register(this, WSConstants.GCM_SENDER_ID1);
		}
	}
	
	private void LaunchSettingsScreen(boolean isexplict){
		Intent intent=new Intent(this, SettingsActivity.class);
		intent.putExtra("issetting", isexplict);
		startActivity(intent);
		finish();
	}
	
	
	@Override
	public void onEntered(String PhoneNo, String uName) {
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
		alert.dismiss();
		phonenumber=PhoneNo;
		userName=uName;
		createUser();
	}
	
	
	private void createUser(){
		if(!NetWorkUtil.getInstance(this).isNetWorkAvail()){
			Toast.makeText(this,getString(R.string.no_internet), Toast.LENGTH_LONG).show();
			return;
		}
		mdialog=ProgressDialog.show(this, "", getString(R.string.registering));
		String possibleEmail="";
		Account[] accounts = AccountManager.get(this).getAccounts();
		for (Account account : accounts) {
			if (emailPattern.matcher(account.name).matches()) {
				possibleEmail = account.name;
				if(possibleEmail.equals(""))
					break;
			}
		}
		String gcmid=GCMRegistrar.getRegistrationId(this);
		String accessToken=preferences.getFbAcessToken();
		String userId=preferences.getFbId();
		Log.i("TAG",""+gcmid);
		HttpRequestBase put=HttpRequestCreater.createUesr(userId, "facebook", phonenumber, possibleEmail, gcmid, 0,"android",accessToken,userName);
		onExecute(WSConstants.CODE_USER_API, put, false);
	}
	
	private String getApplicationversionName(){
		PackageManager manager = getPackageManager();
		PackageInfo info=null;
		try {
			   info = manager.getPackageInfo(this.getPackageName(), 0);
			   String versionName = info.versionName;
			   return versionName;
			   } catch (NameNotFoundException e) {
			     return "";
			   }
		
	}

	@Override
	public void onResume() {
		super.onResume();
		isResumed = true;
		uiHelper.onResume();	
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if(preferences.getPhoneNumber()==null&&preferences.getFbAcessToken()!=null){
			loginbtn.setVisibility(View.INVISIBLE);
			alert.show();
		}
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
		preferences.setPhoneNumber(phonenumber);
		if(data.isSuccess){
			mdialog.dismiss();
			raiseLocationUpdateAlarm();
			LaunchSettingsScreen(false);
		}
	}

	@Override
	protected void onError(int reqCode, int errorCode, String errorMessage) {
		mdialog.dismiss();
		Toast.makeText(this,errorMessage, Toast.LENGTH_SHORT).show();
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
				firstTime, 1*60* 1000, pendingIntent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}



	private static class DisplayToast implements Runnable{
		String mText;
		Context mContext;

		public DisplayToast(String text, Context context){
			mText = text;
			mContext = context;
		}

		public void run(){
			Toast.makeText(mContext, mText, Toast.LENGTH_LONG).show();
		}
	}
	
	

}
