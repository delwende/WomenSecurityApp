package com.glaesis.droid.security.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.glaesis.droid.security.R;



public class CustomPhoneDialog extends Dialog implements android.view.View.OnClickListener {

	
	private Button register_btn=null;
	private PhoneStatus mctx=null;
	private EditText text=null;
	
	private AlertDialog.Builder alert = null;
	
	public CustomPhoneDialog(Context context) {
		super(context);
		mctx=(PhoneStatus)context;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_phone);
		//setCancelable(false);
		register_btn=(Button)findViewById(R.id.btn);
		register_btn.setOnClickListener(this);
		text=(EditText)findViewById(R.id.editTextDialogUserInput);
	}

	@Override
	public void onClick(View v) {
      if(text.getText().length()==0){
    	  return;
      }
      else{
    	  mctx.onEntered(text.getText().toString());
    	  dismiss();
      }
		
	}

}
