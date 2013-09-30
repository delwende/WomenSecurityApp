package com.tavant.droid.security.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.tavant.droid.security.R;

public class LoginActivity extends ActionBarActivity {
	
	private TextView versionText=null;
	private TextView termsText=null;
	private static final int REQ_CODE=125;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
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

}
