package com.tavant.droid.security.utils;

import com.tavant.droid.security.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class CustomAlert extends Dialog{

	private View.OnClickListener btnListener=null;
	private Button mOkbtn=null;
	private Button mCancelbtn=null;
	
	
	public CustomAlert(Context ctx,View.OnClickListener listener){
		super(ctx);
		this.btnListener=listener;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_alert);
		mOkbtn=(Button)findViewById(R.id.cancelbtn);
		mOkbtn.setOnClickListener(btnListener);
		mCancelbtn=(Button)findViewById(R.id.okbtn);
		mCancelbtn.setOnClickListener(btnListener);
	}

}
