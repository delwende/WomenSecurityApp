package com.tavant.droid.security;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.chat.XMPPManager;
import com.facebook.chat.XMPPManager.XMPPChatListener;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.tavant.droid.security.activities.SettingsActivity;
import com.tavant.droid.security.data.BaseData;
import com.tavant.droid.security.database.ContentDescriptor;
import com.tavant.droid.security.http.HttpRequestCreater;
import com.tavant.droid.security.lock.LockScreenActivity;
import com.tavant.droid.security.prefs.CommonPreferences;
import com.tavant.droid.security.service.LocationAlarmService;
import com.tavant.droid.security.sound.ScreamPlayer;
import com.tavant.droid.security.utils.CustomAlert;
import com.tavant.droid.security.utils.NetWorkUtil;
import com.tavant.droid.security.utils.WSConstants;



public class HomeActivity extends BaseActivity implements OnClickListener {

	ImageButton panicButton;
	TelephonyManager telephonyManager;
	PhoneStateListener listener;
	private String volunteerNumber;
	private PendingIntent pendingIntent;
	String stateString = "N/A";
	boolean callNext = false;
	private static String[] numbers;
	private int callRepeatCount = 1;
	private ContentResolver resolver;
	private boolean buzzer;
	private boolean isSocialnetworkingenabled;
	private String alertText="";
	private Timer timer=null;
	private int mNumberofClicks=0;
	private CommonPreferences commonpref=null;
	private boolean isTriggered=false;
	private CustomAlert locationAlert = null;
	private LocationManager manager=null;
	
		
	private String locationImage="http://maps.googleapis.com/maps/api/staticmap?center=%s,%s&zoom=13&size=320x480&maptype=roadmap&markers=color:blue|label:S|%s,%s&sensor=false";
	
	@Override
	protected void onCreate(Bundle instance3) {
		super.onCreate(instance3);
		setContentView(R.layout.activity_main);
		resolver = getContentResolver();
		commonpref=CommonPreferences.getInstance();
		panicButton = (ImageButton) findViewById(R.id.panic_button);
		panicButton.setOnClickListener(this);
		alertText=getString(R.string.alert_text);
		manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	}
	

	
	@Override
	protected void onResume() {
		buzzer = commonpref.isNeedbuzzer();
		isSocialnetworkingenabled = commonpref.isInformFriends();
		super.onResume();
		if (callNext == true) {
			for (int i = 0; i < callRepeatCount; i++) {

				for (int j = 0; j < numbers.length; j++) {
					makeEmergencyCalls(numbers[j]);
				}
			}
			callNext = false;
			callRepeatCount = 0;
			resetTriggeringStatus();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
       /*
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.panic_button) {
			volunteerNumber =commonpref.getVolunteerNumber();
			Log.d("TAG","cop number"+volunteerNumber);
			numbers = retrievePhoneNumbers();
			
			if (buzzer == true) {
				ScreamPlayer screamPlayer = new ScreamPlayer(this);
				screamPlayer.setRepeatCount(3);
				screamPlayer.startRinging();
			}
			if (friends == true) {
				//posttoFBTimeLine();
				postToWall();
				sendFBChatMessage();
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
	*/
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.panic_button) {
			if(!isTriggered){
			  isTriggered=true; 
			  mNumberofClicks++;
			  volunteerNumber =commonpref.getVolunteerNumber();
			  numbers=retrieveFriendnumbers();  // getting friend numbers
			  Toast.makeText(HomeActivity.this, getString(R.string.confirm_click_alert), Toast.LENGTH_SHORT).show();
			  try{
			  makeSmsAlert(numbers);
			  }catch(Exception e){e.printStackTrace();}
			  try{
			  timer=new Timer();	 
			  timer.schedule(new TimerTask() {
				@Override
				public void run() {
					resetTriggeringStatus();
				}
			}, 7*1000);
			  }catch(Exception e){e.printStackTrace();}
			}else{
				mNumberofClicks++;
				if(mNumberofClicks==3)
				{
					if (buzzer) {
						ScreamPlayer screamPlayer = new ScreamPlayer(this);
						screamPlayer.setRepeatCount(3);
						screamPlayer.startRinging();
					}
					try{
					notifyFriendsByPushNotification();
					}catch(Exception e){}
					if(isSocialnetworkingenabled){
					  postToWall();   // posting in the wall
					 
					}
					getCallStates();
					makeEmergencyCallToNearestVolunteer();
				}
			}
		}
	}
	
	
	
	private void resetTriggeringStatus(){
		isTriggered=false;
		mNumberofClicks=0;
	}
	private void postToWall(){
		final String fbID=commonpref.getFbId();
		final String fbauthtoken=commonpref.getFbAcessToken();
			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
					DefaultHttpClient client=new DefaultHttpClient();
					HttpPost post=new HttpPost("https://graph.facebook.com/"+fbID+"/feed");
					post.addHeader("Content-Type", "multipart/form-data");
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		            nameValuePairs.add(new BasicNameValuePair("access_token", fbauthtoken));
		            nameValuePairs.add(new BasicNameValuePair("message", alertText+" "
							+  commonpref.getUserlocation()));
		            nameValuePairs.add(new BasicNameValuePair("privacy","{'value':'ALL_FRIENDS'}"));
		            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		            HttpResponse res=client.execute(post);
		            Log.d("TAG","htpp status codefb"+res.getStatusLine().getStatusCode());
		            sendFBChatMessage();
					}catch(Exception e){
						e.printStackTrace();
						 sendFBChatMessage();
					}
				}
			}).start();
	}
	
	private void sendFBChatMessage(){

		final String fbauthtoken=commonpref.getFbAcessToken();
		final List<String> fbids = new ArrayList<String>();
		Cursor cursor = resolver.query(
				ContentDescriptor.WSFacebook.CONTENT_URI, null,
				ContentDescriptor.WSFacebook.Cols.FBSTATUS + " = 1 ", null,
				null);
		while (cursor.moveToNext()) {
			String fbid = cursor.getString(cursor
					.getColumnIndex(ContentDescriptor.WSFacebook.Cols.FBID));	
			fbids.add(fbid);
		}
		if(fbids.size()>0){
			setupXMPPLogin(fbauthtoken,fbids);
		}	
	}
	
	

	private void notifyFriendsByPushNotification() {
		if(!NetWorkUtil.getInstance(this).isNetWorkAvail()){
			//Toast.makeText(this,getString(R.string.no_internet), Toast.LENGTH_LONG).show();
			return;
		}
		List<String> phNumbers = Arrays.asList(numbers);
		JSONArray jsonNumbers = new JSONArray(phNumbers);
		if(volunteerNumber!=null&&volunteerNumber.length()!=0){
			jsonNumbers.put(volunteerNumber);
		}
		String userid = commonpref.getFbId();
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
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					stateString = "Off Hook";
					callNext = false;
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					stateString = "Ringing";
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

	private   void makeSmsAlert(final String[] phNumber) {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				
				if ( commonpref.getUserlocation() != null) {
					try {
						String msg=alertText+" "+commonpref.getUserlocation();
						sendSmsMessage(phNumber, msg);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		},0);
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

	private void makeEmergencyCallToNearestVolunteer() {
		if (volunteerNumber!=null&&volunteerNumber.length() != 0) {
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + volunteerNumber));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
			startActivity(intent);
		}

	}

	public static void sendSmsMessage(String[] number, String message)
			throws Exception {
		SmsManager smsMgr = SmsManager.getDefault();
		for (int i = 0; i < number.length; i++) {
			smsMgr.sendMultipartTextMessage(number[i], null, smsMgr.divideMessage(message), null, null);
		}
	}

	private String[] retrieveFriendnumbers() {
		ArrayList<String> phoneList = new ArrayList<String>();

		Cursor cur = getContentResolver()
				.query(ContentDescriptor.WSContact.CONTENT_URI, null, null,
						null, null);

		if (cur != null) {
			cur.moveToFirst();
			while (cur.isAfterLast() == false) {
				phoneList.add(cur.getString(cur.getColumnIndex(ContentDescriptor.WSContact.Cols.PHONE)));
				System.out
						.println("String phone number >> " + cur.getString(cur.getColumnIndex(ContentDescriptor.WSContact.Cols.PHONE)));
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
		switch (item.getItemId()){ 
		case R.id.action_settings:
			loadSettings();
			break;
		case R.id.item_email:
			sendEmail();
			break;
		case R.id.item_facebook:
			shareFacebook();
			break;
		case R.id.item_twitter:
			break;		
		}
		return true;
	}
	
	
	 private void requestPublishPermissions(Session session) {
	        if (session != null) {
	            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, Arrays.asList("publish_actions"))
	                    // demonstrate how to set an audience for the publish permissions,
	                    // if none are set, this defaults to FRIENDS
	                    .setDefaultAudience(SessionDefaultAudience.FRIENDS)
	                    .setRequestCode(0);
	            session.requestNewPublishPermissions(newPermissionsRequest);
	        }
	    }
	
	private void shareFacebook(){
		
	    Session session=Session.openActiveSessionFromCache(this);
	    		/*
	    requestPublishPermissions(session);
	    Session.OpenRequest request=new Session.OpenRequest(this);
	    request.setCallback(callback);
	    session.openForPublish(request);
	    */
	    
		
		    Bundle params = new Bundle();
		    params.putString("name", getString(R.string.fb_share_name));
		    params.putString("caption", getString(R.string.fb_share_caption));
		    params.putString("description", getString(R.string.fb_share_desciption));
		    String url=String.format(getString(R.string.fb_share_link, getPackageName()));
		    params.putString("link", url);
		    String image=String.format(getString(R.string.fb_share_picture,WSConstants.HOST));
		    params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
		  // Session session= Session.getActiveSession();
		    WebDialog feedDialog = (
		        new WebDialog.FeedDialogBuilder(this,
		        		session,
		            params))
		        .setOnCompleteListener(new OnCompleteListener() {

		            @Override
		            public void onComplete(Bundle values,
		                FacebookException error) {
		                if (error == null) {
		                    // When the story is posted, echo the success
		                    // and the post Id.
		                    final String postId = values.getString("post_id");
		                    if (postId != null) {
		                        Toast.makeText(HomeActivity.this,
		                            "Posted story, id: "+postId,
		                            Toast.LENGTH_SHORT).show();
		                    } else {
		                        // User clicked the Cancel button
		                        Toast.makeText(HomeActivity.this.getApplicationContext(), 
		                            "Publish cancelled", 
		                            Toast.LENGTH_SHORT).show();
		                    }
		                } else if (error instanceof FacebookOperationCanceledException) {
		                    // User clicked the "x" button
		                    Toast.makeText(HomeActivity.this.getApplicationContext(), 
		                        "Publish cancelled", 
		                        Toast.LENGTH_SHORT).show();
		                } else {
		                    // Generic, ex: network error
		                    Toast.makeText(HomeActivity.this.getApplicationContext(), 
		                        "Error posting story", 
		                        Toast.LENGTH_SHORT).show();
		                }
		            }

		        })
		        .build();
		    feedDialog.show();
		    
	}
	
	
	private Session.StatusCallback callback = 
			new Session.StatusCallback() {
		@Override
		public void call(Session session, 
				SessionState state, Exception exception) {
			Bundle params = new Bundle();
		    params.putString("name", "Facebook SDK for Android");
		    params.putString("caption", "Build great social apps and get more installs.");
		    params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
		    params.putString("link", "https://developers.facebook.com/android");
		    params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
			
			WebDialog feedDialog = (
			        new WebDialog.FeedDialogBuilder(HomeActivity.this,session,
			            params))
			        .setOnCompleteListener(new OnCompleteListener() {

			            @Override
			            public void onComplete(Bundle values,
			                FacebookException error) {
			                if (error == null) {
			                    // When the story is posted, echo the success
			                    // and the post Id.
			                    final String postId = values.getString("post_id");
			                    if (postId != null) {
			                        Toast.makeText(HomeActivity.this,
			                            "Posted story, id: "+postId,
			                            Toast.LENGTH_SHORT).show();
			                    } else {
			                        // User clicked the Cancel button
			                        Toast.makeText(HomeActivity.this, 
			                            "Publish cancelled", 
			                            Toast.LENGTH_SHORT).show();
			                    }
			                } else if (error instanceof FacebookOperationCanceledException) {
			                    // User clicked the "x" button
			                    Toast.makeText(HomeActivity.this, 
			                        "Publish cancelled", 
			                        Toast.LENGTH_SHORT).show();
			                } else {
			                    // Generic, ex: network error
			                    Toast.makeText(HomeActivity.this, 
			                        "Error posting story", 
			                        Toast.LENGTH_SHORT).show();
			                }
			            }

			        })
			        .build();
			    feedDialog.show();
			
		}
	};
	
	
	
	
	
	private void sendEmail(){
		
		 Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		 emailIntent.setType("text/html");
		 emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sub:"+getString(R.string.app_name));
		// emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Uri.parse("market://details?id="+getPackageName()+"").toString());
		 emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,  Html.fromHtml("<a>http://details?id="+getPackageName()+"</a>"));
		 //startActivity(Intent.createChooser(emailIntent, "Send mail..."));
		 startActivity(emailIntent);
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
			break;
		}

		}
	}
	
	private void setupXMPPLogin(String fbtocken, List<String>fbids) {
		if(fbtocken!=null){
			String decodedTocken = null;
			try {
				String msg=alertText+ commonpref.getUserlocation()+" "+String.format(locationImage,commonpref.getLatitude(),commonpref.getLongtitude(),
						commonpref.getLatitude(), commonpref.getLongtitude());
				decodedTocken = URLDecoder.decode(fbtocken, "utf-8");
				XMPPManager.getInstance().init(decodedTocken,fbids,msg);
				XMPPManager.getInstance().setXMPPChatListener(this, mChatListener);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	XMPPChatListener mChatListener = new XMPPChatListener() {

		@Override
		public void receivedChatMessage(String receiverid, String message,
				boolean isNew) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sendChatMessage(String senderId, String message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void showChatNotification(String receiverid, String mess) {
			
			
		}

	
	};
}
