package com.tavant.droid.security.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.tavant.droid.security.R;
import com.tavant.droid.security.utils.FontLoader;

public class CustomizableTextView extends TextView {
	private static final String TAG = "CustomizableTextView";

	public CustomizableTextView(android.content.Context context) {
		super(context.getApplicationContext());
	}

	public CustomizableTextView(android.content.Context context, android.util.AttributeSet attrs) {
		super(context.getApplicationContext(), attrs);
		setCustomFont(context.getApplicationContext(), attrs);
	}

	public CustomizableTextView(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
		super(context.getApplicationContext(), attrs, defStyle);
		setCustomFont(context.getApplicationContext(), attrs);
	}

	private void setCustomFont(Context ctx, AttributeSet attrs) {
		TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomizableTextView);
		String customFont = a.getString(R.styleable.CustomizableTextView_customFont);
		Log.i(TAG,"custom Font"+customFont);
		setCustomFont(ctx, customFont);
		a.recycle();
	}

	public boolean setCustomFont(Context ctx, String fontName) {
		if(fontName!=null)
		{
			Typeface tf = null;
			if(fontName.compareTo("Roboto-Bold")==0)
			tf=FontLoader.getMngr().getTfRobotBold();
			else if(fontName.compareTo("Roboto-Regular")==0)
			tf=FontLoader.getMngr().getTfRobotNormal();			
            if(tf!=null)
			setTypeface(tf);
			return true;
		}
		else
			return false;	
	}
}
