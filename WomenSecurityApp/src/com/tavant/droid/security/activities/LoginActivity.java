package com.tavant.droid.security.activities;

import java.util.Arrays;
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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.tavant.droid.security.BaseActivity;
import com.tavant.droid.security.R;
import com.tavant.droid.security.data.BaseData;
import com.tavant.droid.security.http.HttpRequestCreater;
import com.tavant.droid.security.prefs.CommonPreferences;
import com.tavant.droid.security.service.LocationAlarmService;
import com.tavant.droid.security.utils.PhoneStatus;
import com.tavant.droid.security.utils.WSConstants;

public class LoginActivity extends BaseActivity implements PhoneStatus{
	
	private TextView versionText=null;
	private TextView termsText=null;
	private static final int REQ_CODE=125;
	private String mdeviceId =null;
	private LoginButton loginbtn=null;
	private AlertDialog.Builder alert = null;
	private CommonPreferences preferences;
	private TelephonyManager phoneManager=null;
	private UiLifecycleHelper uiHelper=null;
	private String mgcmId="";
	private LicenseCheckerCallback mLicenseCheckerCallback=null;
	private LicenseChecker mChecker;
	private static Handler handler=new Handler();
	private ProgressDialog mdialog=null;
	private boolean isResumed = false;
	private EditText text = null;
	private String fbacesstoken="";
	private String phonenumber="";
	private String userId="";
	private PhoneStatus mctx=null;
	private Pattern emailPattern = null;
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	private LinearLayout alertView=null;
	private LayoutInflater inflater=null;
	
	
	
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
				Log.d("TAG", "my accesstoken"+session.getAccessToken());
				fbacesstoken=session.getAccessToken();
				preferences.setFbAcessToken(fbacesstoken);
				mdialog=ProgressDialog.show(this, "", "fetching user details...");
				makeMeRequest(session);
			} else if (state.isClosed()) {
				handler.post(new DisplayToast("error in login",LoginActivity.this));
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
						userId=user.getId();
						preferences.setFbId(userId);
						mdialog.dismiss();
						alert.setMessage(getString(R.string.enter_mob_number));
						text.setInputType(InputType.TYPE_CLASS_PHONE);
						alertView=(LinearLayout)inflater.inflate(R.layout.custom_dialog, null);
						alert.setView(alertView);
						alert.setPositiveButton("Register", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								String value = text.getText().toString().trim();
								Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
								if(text.getText().toString().length()==0)
									return;
								else{
									mctx.onEntered(text.getText().toString());
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		text = new EditText(this);
		inflater=LayoutInflater.from(this);
		alert = new AlertDialog.Builder(this);
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
			loginbtn.setReadPermissions(Arrays.asList("xmpp_login", "user_online_presence","friends_online_presence")); 
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
		

		/*
		mLicenseCheckerCallback = new GSLicenseCheckerCallback();
		// Construct the LicenseChecker with a policy.
		mChecker = new LicenseChecker(
				this, new ServerManagedPolicy(this,
						new AESObfuscator(WSConstants.SALT, getPackageName(), mdeviceId)),
						WSConstants.BASE64_PUBLIC_KEY);
		mdialog=ProgressDialog.show(this, "", getString(R.string.configure_app));
		mChecker.checkAccess(mLicenseCheckerCallback);
		*/
	}
	
	private void LaunchSettingsScreen(boolean isexplict){
		Intent intent=new Intent(this, SettingsActivity.class);
		intent.putExtra("issetting", isexplict);
		startActivity(intent);
		finish();
	}
	
	
	@Override
	public void onEntered(String PhoneNo) {
		phonenumber=PhoneNo;
		preferences.setPhoneNumber(phonenumber);
		createUser();
	}
	
	
	private void createUser(){
		mdialog=ProgressDialog.show(this, "", getString(R.string.fetch_details));
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
		Log.i("TAG",""+gcmid);
		HttpRequestBase put=HttpRequestCreater.createUesr(userId, "facebook", phonenumber, possibleEmail, gcmid, 0,"android",fbacesstoken);
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
		Toast.makeText(this,errorMessage, Toast.LENGTH_SHORT).show();
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
	
	
	
	
	private class GSLicenseCheckerCallback implements LicenseCheckerCallback {
		public void allow(int policyReason) {
			mdialog.dismiss();
			if (isFinishing()) {
				return;
			}
			Log.d("TAG","LiencedApp");
		}

		public void dontAllow(int policyReason) {
			mdialog.dismiss();
			if (isFinishing()) {
				return;
			}else{
				//Toast.makeText(FacebookLoginActivty.this, getString(R.string.unlicensed_dialog_body), Toast.LENGTH_LONG).show();	
				handler.post(new DisplayToast(getString(R.string.unlicensed_dialog_body),LoginActivity.this));
				finish();
			}
			Log.d("TAG","unlicenced");
		}

		public void applicationError(int errorCode) {
			mdialog.dismiss();
			if (isFinishing()) {
				return;
			}	
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//mChecker.onDestroy();
	}



	private class DisplayToast implements Runnable{
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
