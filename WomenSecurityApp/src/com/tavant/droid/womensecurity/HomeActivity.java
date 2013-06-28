package com.tavant.droid.womensecurity;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.OpenGraphAction;
import com.tavant.droid.womensecurity.activities.SettingsActivity;
import com.tavant.droid.womensecurity.data.BaseData;
import com.tavant.droid.womensecurity.data.FbUser;
import com.tavant.droid.womensecurity.database.ContentDescriptor;
import com.tavant.droid.womensecurity.http.HttpRequestCreater;
import com.tavant.droid.womensecurity.lock.LockScreenActivity;
import com.tavant.droid.womensecurity.service.LocationAlarmService;
import com.tavant.droid.womensecurity.sound.ScreamPlayer;
import com.tavant.droid.womensecurity.utils.LocationData;
import com.tavant.droid.womensecurity.utils.WSConstants;

public class HomeActivity extends BaseActivity implements OnClickListener {

	ImageButton panicButton;
	private static String SAVE_ME_TEXT = "Please call police. I am in danger. PLEASE HELP . My location is:";

	// private TextToSpeech mTts;
	// This code can be any value you want, its just a checksum.
	private static final int MY_DATA_CHECK_CODE = 1234;
	TelephonyManager telephonyManager;
	PhoneStateListener listener;
	private SharedPreferences copPhonePreferences;
	private SharedPreferences.Editor copPrefsEditor;
	String copNumber;
	private PendingIntent pendingIntent;
	String stateString = "N/A";
	boolean callNext = false;
	private static String[] numbers;
	private int callRepeatCount = 1;
	private ContentResolver resolver;
	private SharedPreferences pref = null;
	private boolean buzzer;
	private boolean friends;
	 private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle instance3) {
		super.onCreate(instance3);
		setContentView(R.layout.activity_main);
		resolver = getContentResolver();
		pref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
		// Get the telephony manager
		copPhonePreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

		// copNumber = copPhonePreferences.getString("COP_NUMBER", "");

		panicButton = (ImageButton) findViewById(R.id.panic_button);
		panicButton.setOnClickListener(this);

		// Fire off an intent to check if a TTS engine is installed
		/*
		 * Intent checkIntent = new Intent();
		 * checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		 * startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
		 */

		buzzer = pref.getBoolean("buzzer_key", false);
		friends = pref.getBoolean("friends_key", true);

	}

	protected void onResume() {
		super.onResume();
		if (callNext == true) {
			for (int i = 0; i < callRepeatCount; i++) {

				for (int j = 0; j < numbers.length; j++) {
					makeEmergencyCalls(numbers[j]);
				}
			}
			callNext = false;
			callRepeatCount = 0;
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.panic_button) {
			copNumber = copPhonePreferences.getString("COP_NUMBER", "");
			System.out.println("String phone number in calls >> " + copNumber);
			numbers = retrievePhoneNumbers();
			getCallStates();
			if (buzzer == true) {
				ScreamPlayer screamPlayer = new ScreamPlayer(this);
				screamPlayer.setRepeatCount(3);
				screamPlayer.startRinging();
			}
			if (friends == true) {
				//posttoFBTimeLine();
				postToWall();
				// raiseLocationUpdateAlarm();
				if (numbers.length > 0) {
					notifyFriendsByPushNotification();
					makeSmsAlert(numbers);
					// makeEmergencyCalls(numbers.get(0));
				}
			}
			makeEmergencyCallToNearestCop();
		}
	}

	public void posttoFBTimeLine() {
		final List<FbUser> fbids = new ArrayList<FbUser>();
		FbUser user;
		Cursor cursor = resolver.query(
				ContentDescriptor.WSFacebook.CONTENT_URI, null,
				ContentDescriptor.WSFacebook.Cols.FBSTATUS + " = 1 ", null,
				null);
		int i = 0;
		while (cursor.moveToNext()) {

			String fbid = cursor.getString(cursor
					.getColumnIndex(ContentDescriptor.WSFacebook.Cols.FBID));
			user = new FbUser();
			user.setId(fbid);
			fbids.add(user);
			i++;
			if (i == 1)
				break;
		}

		Session session = new Session(this);
		
		
		 List<String> permissions = session.getPermissions();
	        if (!permissions.containsAll(PERMISSIONS)) {
	           
	            return;
	        }
		
		
		AccessToken token = AccessToken.createFromExistingAccessToken(
				pref.getString(WSConstants.PROPERTY_FB_ACCESSTOKEN, null),
				null, null, AccessTokenSource.FACEBOOK_APPLICATION_NATIVE, PERMISSIONS);
		session.open(token, new StatusCallback() {
			@Override
			public void call(Session msession, SessionState state,
					Exception exception) {
				if(state.isOpened()){
				   postToWall(fbids, msession);
				}
			}
		});
	}

	private void postToWall(List<FbUser> fbid, Session msession) {
//		OpenGraphAction action = GraphObject.Factory
//				.create(OpenGraphAction.class);
//		FbUser me = new FbUser();
//		me.setId(pref.getString(WSConstants.PROPERTY_FB_ID, ""));
//		action.setTags(fbid);
//		action.setFrom(me);
//		action.setCreatedTime(Calendar.getInstance().getTime());
//		action.setMessage("please help me i am in Danger");
		
//		Request request = new Request(Session.getActiveSession(),
//				"me/women_security:Ws", null, HttpMethod.POST);
//		request.setCallback(new Request.Callback() {
//			
//			@Override
//			public void onCompleted(Response response) {
//				Log.i("TAG", "post to facebook failed");
//
//			}
//		});
//		
//		request.setGraphObject(action);
//		request.executeAsync();
		Bundle params = new Bundle();
		//.putString("type", "women_security:alert");
		params.putString("url", "http://samples.ogp.me/431195650313026");
		params.putString("title", "Sample Alert");
		params.putString("description", "");

		
	
		
		Request request = new Request(
				msession,
		    "me/objects/women_security:alert",
		    params,
		    HttpMethod.POST
		);
		request.setCallback(new Request.Callback() {
			
			@Override
			public void onCompleted(Response response) {
				Log.i("TAG", response.toString());
				
			}
		});
		request.executeAsync();
		
		// handle the response

		// Request req=Request.newPostRequest(msession, "me/women_security:Ws",
		// action, new Request.Callback() {
		// @Override
		// public void onCompleted(Response response) {
		// // TODO Auto-generated method stub
		// if(response.getError()!=null){
		// Log.i("TAG","post to facebook failed");
		// }
		// }
		// });
		// request.executeAsync();
	}
	
	
	private void postToWall(){
		SharedPreferences prefs = getSharedPreferences(getPackageName(), 
                Context.MODE_PRIVATE);
		final String fbappaid=prefs.getString(WSConstants.PROPERTY_FB_ID, null);
		final String fbauthtoken=prefs.getString(WSConstants.PROPERTY_FB_ACCESSTOKEN, null);
		
			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						
					DefaultHttpClient client=new DefaultHttpClient();
					HttpPost post=new HttpPost("https://graph.facebook.com/"+fbappaid+"/feed");
					post.addHeader("Content-Type", "multipart/form-data");
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		            nameValuePairs.add(new BasicNameValuePair("access_token", fbauthtoken));
		            nameValuePairs.add(new BasicNameValuePair("message", SAVE_ME_TEXT
							+ LocationData.getInstance().getCurrentLocation()));
		            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		            HttpResponse res=client.execute(post);
		            Log.i("TAG","htpp status code"+res.getStatusLine().getStatusCode());
					}catch(Exception e){
						e.printStackTrace();
						
					}
				}
			}).start();
		
	}
	
	

	private void notifyFriendsByPushNotification() {
		List<String> phNumbers = Arrays.asList(numbers);
		JSONArray jsonNumbers = new JSONArray(phNumbers);
		if(copNumber.length()!=0){
			jsonNumbers.put(copNumber);
		}
		pref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
		String userid = pref.getString(WSConstants.PROPERTY_FB_ID, null);

		onExecute(WSConstants.CODE_ALERT_API,
				HttpRequestCreater.alertUsers(jsonNumbers, userid), false);
	}

	private void getCallStates() {
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		// Create a new PhoneStateListener
		listener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {

				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					stateString = "Idle";
					callNext = true;
					/*
					 * Toast.makeText(HomeActivity.this, "Phone state Idle",
					 * Toast.LENGTH_LONG).show();
					 */
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					stateString = "Off Hook";
					callNext = false;
					Toast.makeText(HomeActivity.this, "Phone state Off hook",
							Toast.LENGTH_LONG).show();
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					stateString = "Ringing";
					Toast.makeText(HomeActivity.this, "Phone state Ringing",
							Toast.LENGTH_LONG).show();
					break;
				}
			}
		};
		// Register the listener wit the telephony manager
		telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	private void raiseLocationUpdateAlarm() {
		Intent myIntent = new Intent(HomeActivity.this,
				LocationAlarmService.class);
		pendingIntent = PendingIntent.getService(HomeActivity.this, 0,
				myIntent, 0);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, 10);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), 50 * 1000, pendingIntent);
	}

	public static void makeSmsAlert(final String[] phNumber) {
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				System.out.println("Text to send.." + SAVE_ME_TEXT
						+ LocationData.getInstance().getCurrentLocation());
				if (LocationData.getInstance().getCurrentLocation() != null) {
					try {
						sendSmsMessage(phNumber, SAVE_ME_TEXT
								+ LocationData.getInstance()
										.getCurrentLocation());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}, 10000);
	}

	protected void makeEmergencyCalls(String numbers) {

		if (numbers != null) {
			System.out.println("String phone number in calls >> " + numbers);
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + numbers));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
			startActivity(intent);
		}

	}

	private void makeEmergencyCallToNearestCop() {

		
		// getCallStates();
		if (copNumber.length() != 0) {
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + copNumber));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
			startActivity(intent);
		}

	}

	public static void sendSmsMessage(String[] number, String message)
			throws Exception {
		SmsManager smsMgr = SmsManager.getDefault();
		smsMgr.sendTextMessage(number[0], null, message, null, null);

		for (int i = 0; i < number.length; i++) {
			smsMgr.sendTextMessage(number[i], null, message, null, null);
		}
	}

	private String[] retrievePhoneNumbers() {
		ArrayList<String> phoneList = new ArrayList<String>();

		Cursor cur = getContentResolver()
				.query(ContentDescriptor.WSContact.CONTENT_URI, null, null,
						null, null);

		if (cur != null) {
			cur.moveToFirst();
			while (cur.isAfterLast() == false) {
				phoneList.add(cur.getString(5));
				System.out
						.println("String phone number >> " + cur.getString(5));
				cur.moveToNext();
			}
			cur.close();

			String[] phoneArray = new String[phoneList.size()];
			phoneArray = phoneList.toArray(phoneArray);
			for (String s : phoneArray)
				System.out.println(s);
			return phoneArray;
		} else {
			return null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.action_settings:
			loadSettings();
		}

		return true;
	}

	private void loadSettings() {

		SharedPreferences prfs = getSharedPreferences(
				"AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
		String pattaren = prfs.getString("drawpattern", null);
		System.out.println("pattern code >>>>>>>>>>>>>>>>>> " + pattaren);
		if (pattaren != null) {
			Intent intentActivity = new Intent(
					LockPatternActivity.ACTION_COMPARE_PATTERN, null, this,
					LockPatternActivity.class);
			intentActivity.putExtra(LockPatternActivity.EXTRA_PATTERN,
					pattaren.toCharArray());
			startActivityForResult(intentActivity,
					LockScreenActivity.REQ_ENTER_PATTERN);
		} else {
			Intent intent=new Intent(HomeActivity.this, SettingsActivity.class);
			intent.putExtra("issetting", true);
			startActivity(intent);
			
		}

	}

	@Override
	protected void onComplete(int reqCode, BaseData data) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onError(int reqCode, int errorCode, String errorMessage) {
		// TODO Auto-generated method stub

	}

	/**
	 * This is the callback from the TTS engine check, if a TTS is installed we
	 * create a new TTS instance (which in turn calls onInit), if not then we
	 * will create an intent to go off and install a TTS engine
	 * 
	 * @param requestCode
	 *            int Request code returned from the check for TTS engine.
	 * @param resultCode
	 *            int Result code returned from the check for TTS engine.
	 * @param data
	 *            Intent Intent returned from the TTS check.
	 */
	/*
	 * public void onActivityResult(int requestCode, int resultCode, Intent
	 * data) { if (requestCode == MY_DATA_CHECK_CODE) { if (resultCode ==
	 * TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) { // success, create the TTS
	 * instance mTts = new TextToSpeech(this, this);
	 * mTts.setLanguage(Locale.US); } else { // missing data, install it Intent
	 * installIntent = new Intent(); installIntent
	 * .setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
	 * startActivity(installIntent); } } }
	 */

	/**
	 * Be kind, once you've finished with the TTS engine, shut it down so other
	 * applications can use it without us interfering with it :)
	 */
	@Override
	public void onDestroy() {
		// Don't forget to shutdown!
		/*
		 * if (mTts != null) { mTts.stop(); mTts.shutdown(); }
		 */
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case LockScreenActivity.REQ_ENTER_PATTERN: {
			int msgId = 0;

			switch (resultCode) {
			case RESULT_OK:
				msgId = android.R.string.ok;
				Intent intent = new Intent(this, SettingsActivity.class);
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

			String msg = String.format("%s (%,d tries)", getString(msgId),
					data.getIntExtra(LockPatternActivity.EXTRA_RETRY_COUNT, 0));

			Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
			toast.show();

			break;
		}

		}
	}

}
