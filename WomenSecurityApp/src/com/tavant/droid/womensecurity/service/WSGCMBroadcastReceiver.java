package com.tavant.droid.womensecurity.service;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tavant.droid.womensecurity.R;

public class WSGCMBroadcastReceiver extends BroadcastReceiver {
	
	
	private NotificationManager mNotificationManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("TAG"," i received the message");
		
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            sendNotification(context,"Send error: " + intent.getExtras().toString());
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            sendNotification(context,"Deleted messages on server: " + 
                    intent.getExtras().toString());
        } else {
            sendNotification(context,"Received: " + intent.getExtras().toString());
        }
        setResultCode(Activity.RESULT_OK);	
	}
	

	
	//TODO, i need to wrap this class with classes in gcm client 
	
	
	
	private void sendNotification(Context ctx,String msg) {
	      mNotificationManager = (NotificationManager)
	              ctx.getSystemService(Context.NOTIFICATION_SERVICE);
	     // PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
	        //  new Intent(ctx, DemoActivity.class), 0);
	      
	      NotificationCompat.Builder mBuilder =
	          new NotificationCompat.Builder(ctx)
	          .setSmallIcon(R.drawable.ic_launcher)
	          .setContentTitle("GCM Notification")
	          .setStyle(new NotificationCompat.BigTextStyle()
	                     .bigText(msg))
	          .setContentText(msg);
	      
	     mBuilder.setContentIntent(null);
	     mNotificationManager.notify(10001, mBuilder.build());
	    }
	}
	


