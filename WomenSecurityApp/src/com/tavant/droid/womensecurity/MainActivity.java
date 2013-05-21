package com.tavant.droid.womensecurity;

import android.app.Activity;
import android.os.Bundle;

import com.tavant.droid.womensecurity.sound.ScreamPlayer;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle instance3) {
		super.onCreate(instance3);
		setContentView(R.layout.activity_main);
		ScreamPlayer soundPlayer = new ScreamPlayer(getApplicationContext());
		soundPlayer.setRepeatCount(2);
		soundPlayer.startRinging();
	}



}
