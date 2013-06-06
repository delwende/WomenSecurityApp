package com.tavant.droid.womensecurity;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tavant.droid.womensecurity.activities.SettingsActivity;
import com.tavant.droid.womensecurity.data.BaseData;
import com.tavant.droid.womensecurity.database.ContentDescriptor;
import com.tavant.droid.womensecurity.service.LocationAlarmService;
import com.tavant.droid.womensecurity.utils.LocationData;

public class HomeActivity extends BaseActivity implements
		TextToSpeech.OnInitListener {

	ImageButton panicButton;
	private String SAVE_ME_TEXT = "Please call police. I am in danger. PLEASE HELP . My location is:";

	//private TextToSpeech mTts;
	// This code can be any value you want, its just a checksum.
	private static final int MY_DATA_CHECK_CODE = 1234;
	TelephonyManager telephonyManager;
	PhoneStateListener listener;

	private SharedPreferences copPhonePreferences;
	private SharedPreferences.Editor copPrefsEditor;
	String copNumber;
	private PendingIntent pendingIntent;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle instance3) {
		super.onCreate(instance3);
		setContentView(R.layout.activity_main);
		// Get the telephony manager
		copPhonePreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

		copNumber = copPhonePreferences.getString("COP_NUMBER", "9538432555");

		panicButton = (ImageButton) findViewById(R.id.panic_button);

		panicButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

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
				Toast.makeText(HomeActivity.this, "Start Alarm",
						Toast.LENGTH_LONG).show();
				final ArrayList<String> number = retrievePhoneNumbers();

				/*Handler handler = new Handler();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						System.out.println("Text to send.."
								+ SAVE_ME_TEXT
								+ LocationData.getInstance()
										.getCurrentLocation());
						if (LocationData.getInstance().getCurrentLocation() != null) {
							try {
								sendSmsMessage(number, SAVE_ME_TEXT
										+ LocationData.getInstance()
												.getCurrentLocation());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}, 10000);
*/
				
				try {
					 sendSmsMessage(number, SAVE_ME_TEXT +
					 LocationData.getInstance().getCurrentLocation());
					System.out.println("Text to send.." + SAVE_ME_TEXT
							+ LocationData.getInstance().getCurrentLocation());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				makeEmergencyCalls(number);
			}
		});

		// Fire off an intent to check if a TTS engine is installed
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		// Create a new PhoneStateListener
		listener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				String stateString = "N/A";
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					stateString = "Idle";
					Toast.makeText(HomeActivity.this, "Phone state Idle",
							Toast.LENGTH_LONG).show();
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					stateString = "Off Hook";
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

	protected void makeEmergencyCalls(ArrayList<String> numbers) {

		System.out.println("String phone number in calls >> " + copNumber);
		/*
		 * if (numbers != null) {
		 * System.out.println("String phone number in calls >> " +
		 * numbers.get(0)); Intent intent = new Intent(Intent.ACTION_CALL);
		 * intent.setData(Uri.parse("tel:" + numbers.get(0)));
		 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * intent.addFlags(Intent.FLAG_FROM_BACKGROUND); startActivity(intent);
		 * }
		 */
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + copNumber));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
		startActivity(intent);
	}

	public void sendSmsMessage(ArrayList<String> numbers, String message)
			throws Exception {
		SmsManager smsMgr = SmsManager.getDefault();
		smsMgr.sendTextMessage(numbers.get(0), null, message, null, null);
		
		  for (int i = 0; i < numbers.size(); i++) {
		  smsMgr.sendTextMessage(numbers.get(i), null, message, null, null); 
		  }
	}

	private ArrayList<String> retrievePhoneNumbers() {
		ArrayList<String> phoneList = new ArrayList<String>();

		Cursor cur = this.getContentResolver()
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
			return phoneList;
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

		return super.onOptionsItemSelected(item);
	}

	private void loadSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);

	}

	@Override
	protected void onComplete(int reqCode, BaseData data) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onError(int reqCode, int errorCode, String errorMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInit(int initStatus) {
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
	/*public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// success, create the TTS instance
				mTts = new TextToSpeech(this, this);
				mTts.setLanguage(Locale.US);
			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
	}*/

	/**
	 * Be kind, once you've finished with the TTS engine, shut it down so other
	 * applications can use it without us interfering with it :)
	 */
	@Override
	public void onDestroy() {
		// Don't forget to shutdown!
		/*if (mTts != null) {
			mTts.stop();
			mTts.shutdown();
		}*/
		super.onDestroy();
	}
}
