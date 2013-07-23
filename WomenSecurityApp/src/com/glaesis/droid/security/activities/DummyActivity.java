package com.glaesis.droid.security.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.glaesis.droid.security.BaseActivity;
import com.glaesis.droid.security.data.BaseData;

public class DummyActivity extends Activity {

	
	private String telephonenumber="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		telephonenumber=getIntent().getStringExtra("telno");
		if(telephonenumber.length()>0){
			String number = "tel:" + telephonenumber;
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number)); 
            startActivity(callIntent);
		}
		finish();
	}
}
