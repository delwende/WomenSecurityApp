package com.tavant.droid.security.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.tavant.droid.security.R;
import com.tavant.droid.security.activities.DummyActivity;
import com.tavant.droid.security.utils.WSConstants;

public class WSGCMIntentService extends GCMBaseIntentService {

	
	
    public WSGCMIntentService(){
		super(WSConstants.GCM_SENDER_ID1);
	}
	
	@Override
	protected void onMessage(Context context, Intent intent) {
		
		String message=getString(R.string.alert_text);
		String telephonenumber="";
		String from="";
		for (String key : intent.getExtras().keySet()) {
			Log.d("TAG", "Message key: " + key + " value: "
					+ intent.getExtras().getString(key));
			if(key.equals("msg"))
				message=intent.getExtras().getString(key);
			else if(key.equals("telno"))
				telephonenumber=intent.getExtras().getString(key);
			else if(key.equals("from"))
				from=intent.getExtras().getString(from);
		}
		Resources res=  context.getResources();
		int icon= res.getIdentifier("ic_launcher", "drawable", context.getPackageName());
		CharSequence tickerText = "An Alert received from your friend. He need your Help";
		long when = System.currentTimeMillis();
		CharSequence contentTitle =getString(R.string.alert_received)+" "+from;  // expanded message title
		CharSequence contentText = message; // expanded
		Intent myintent=new Intent(this, DummyActivity.class);
		myintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		myintent.putExtra("telno", telephonenumber);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				myintent, 0);
		Notification notification = new Notification(icon, tickerText, when);
		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		mNotificationManager.notify(1, notification);
	}

	@Override
	protected void onError(Context context, String errorId) {
		Log.i("TAG","errorId"+errorId);
		

	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i("TAG","received registartio ID"+registrationId);
		
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i("TAG","unregistered registartio ID"+registrationId);
	}

}
