package com.tavant.droid.security.utils;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tavant.droid.security.R;

public class CustomDialog extends Dialog implements android.view.View.OnClickListener {

	private Button register=null;
	private EditText phoneText = null;
	private EditText userNametext = null;
	private Context mctx;
	
	public CustomDialog(Context context) {
		super(context);
		mctx=context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog);
		register=(Button)findViewById(R.id.register_btn);
		register.setOnClickListener(this);
		userNametext=(EditText)findViewById(R.id.dialog_name);
		phoneText=(EditText)findViewById(R.id.dialog_phone);
	}

	@Override
	public void onClick(View v) {
		String phoneNo = phoneText.getText().toString().trim();
		String userName = userNametext.getText().toString().trim();
		if(phoneNo.length()==0||userName.length()==0){
			Toast.makeText(mctx, getContext().getString(R.string.enter_mob_number), Toast.LENGTH_LONG).show();
			return;
		}else
		{
			((PhoneStatus)mctx).onEntered(phoneNo,userName);
		}	
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(isShowing())
				dismiss();
			((Activity)mctx).finish();
		}
		return super.onKeyDown(keyCode, event);
		
	}
	

}
