package com.tavant.droid.womensecurity;


import com.tavant.droid.womensecurity.activities.FetchContactsActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

public class HomeActivity extends FragmentActivity {

	

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle instance3) {
		super.onCreate(instance3);
		setContentView(R.layout.activity_main);
		/*ScreamPlayer soundPlayer = new ScreamPlayer(getApplicationContext());
		soundPlayer.setRepeatCount(2);
		soundPlayer.startRinging();*/
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
			loadContacts();
		
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void loadContacts() {
		Intent intent = new Intent(this,FetchContactsActivity.class);
		startActivity(intent);
	}
}
