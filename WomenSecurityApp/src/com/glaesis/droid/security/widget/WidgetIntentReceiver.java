package com.glaesis.droid.security.widget;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;

import com.glaesis.droid.security.R;
import com.glaesis.droid.security.database.ContentDescriptor;

/**
 * Author Tavant
 */

public class WidgetIntentReceiver extends BroadcastReceiver {

	private static int clickCount = 0;
	private static String[] numbers;
	private SharedPreferences copPhonePreferences;
	private String copNumber;
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals("womansecurity.intent.action.ALERT_FRIENDS_AND_COP")){
			updateWidgetPictureAndButtonListener(context);
		}
	}

	private void updateWidgetPictureAndButtonListener(Context context) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.ws_widget);
		//remoteViews.setImageViewResource(R.id.widget_image, getImageToSet());
		//REMEMBER TO ALWAYS REFRESH YOUR BUTTON CLICK LISTENERS!!!
		remoteViews.setOnClickPendingIntent(R.id.widget_button, WidgetProvider.buildButtonPendingIntent(context));
		
		WidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
		
		
		
		startEmergencyAlert(context);
	}

	private void startEmergencyAlert(Context context) {
		numbers = retrievePhoneNumbers(context);
		//HomeActivity.makeSmsAlert(numbers);
		makeEmergencyCallToNearestCop(context);
	}
	
	private String[] retrievePhoneNumbers(Context context) {
		ArrayList<String> phoneList = new ArrayList<String>();

		Cursor cur = context.getContentResolver()
				.query(ContentDescriptor.WSContact.CONTENT_URI, null, null,
						null, null);

		if (cur != null) {
			cur.moveToFirst();
			while (cur.isAfterLast() == false) {
				phoneList.add(cur.getString(5));
				System.out
						.println("String phone number >> " + cur.getString(5));
				cur.moveToNext();
			}
			cur.close();

			String[] phoneArray = new String[phoneList.size()];
			phoneArray = phoneList.toArray(phoneArray);
			for (String s : phoneArray)
				System.out.println(s);
			return phoneArray;
		} else {
			return null;
		}
	}
	
	private void makeEmergencyCallToNearestCop(Context context) {
		copPhonePreferences = context.getSharedPreferences("loginPrefs",0);
		copNumber = copPhonePreferences.getString("COP_NUMBER", "9538432555");
		System.out.println("String phone number in calls >> " + copNumber);
		// getCallStates();
		if (copNumber.length() != 0) {
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + copNumber));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
			context.startActivity(intent);
		}

	}

	/*private int getImageToSet() {
		clickCount++;
		return clickCount % 2 == 0 ? R.drawable.ic_launcher : R.drawable.ic_contact_picture_holo_light;
	}*/
}
