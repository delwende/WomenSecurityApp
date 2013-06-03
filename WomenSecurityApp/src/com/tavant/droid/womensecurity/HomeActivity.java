package com.tavant.droid.womensecurity;


import java.util.ArrayList;
import java.util.Locale;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.tavant.droid.womensecurity.activities.FetchContactsActivity;
import com.tavant.droid.womensecurity.activities.SettingsActivity;
import com.tavant.droid.womensecurity.data.BaseData;
import com.tavant.droid.womensecurity.database.ContentDescriptor;

public class HomeActivity extends BaseActivity implements TextToSpeech.OnInitListener{
	
	ImageButton panicButton;
	private String SAVE_ME_TEXT ="Hello. This is an auto generated message.Please call police. I am in danger. PLEASE HELP . My current location is Brigade road";
	
	private TextToSpeech mTts;
    // This code can be any value you want, its just a checksum.
    private static final int MY_DATA_CHECK_CODE = 1234;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle instance3) {
		super.onCreate(instance3);
		setContentView(R.layout.activity_main);
		panicButton = (ImageButton)findViewById(R.id.panic_button);
		
		panicButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				makeEmergencyCalls();
				
			}
		});
		
		// Fire off an intent to check if a TTS engine is installed
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
		
		/*ScreamPlayer soundPlayer = new ScreamPlayer(getApplicationContext());
		soundPlayer.setRepeatCount(2);
		soundPlayer.startRinging();*/
		
	}

	protected void makeEmergencyCalls() {
		
		ArrayList<String> number = retrievePhoneNumbers();				
		if( number != null){
		System.out.println("String phone number in calls >> " + number.get(0));
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + number.get(0)));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
		startActivity(intent);
		}
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable(){

			@Override
			public void run() {
				mTts.speak(SAVE_ME_TEXT,
		                TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
		                null);
				
			}}, 2000);
		
	}

	private ArrayList<String> retrievePhoneNumbers() {
	ArrayList<String> phoneList = new ArrayList<String>();	
		
	Cursor	cur = this.getContentResolver().query(ContentDescriptor.WSContact.CONTENT_URI, null, null, null, null);

	if(cur != null){
	cur.moveToFirst();
    while (cur.isAfterLast() == false) {
    	phoneList.add(cur.getString(5));
    	System.out.println("String phone number >> " + cur.getString(5));
   	    cur.moveToNext();
    }
    cur.close();
	 return phoneList;
	 }
	else{
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

		switch(item.getItemId()){
		
		case R.id.action_settings:
			loadSettings();
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void loadSettings() {
		Intent intent = new Intent(this,SettingsActivity.class);
		startActivity(intent);
		
	}

	private void loadContacts() {
		Intent intent = new Intent(this,FetchContactsActivity.class);
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
     * create a new TTS instance (which in turn calls onInit), if not then we will
     * create an intent to go off and install a TTS engine
     * @param requestCode int Request code returned from the check for TTS engine.
     * @param resultCode int Result code returned from the check for TTS engine.
     * @param data Intent Intent returned from the TTS check.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == MY_DATA_CHECK_CODE)
        {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
            {
                // success, create the TTS instance
                mTts = new TextToSpeech(this, this);
                mTts.setLanguage(Locale.US);
            }
            else
            {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    /**
     * Be kind, once you've finished with the TTS engine, shut it down so other
     * applications can use it without us interfering with it :)
     */
    @Override
    public void onDestroy()
    {
        // Don't forget to shutdown!
        if (mTts != null)
        {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }
	
}
