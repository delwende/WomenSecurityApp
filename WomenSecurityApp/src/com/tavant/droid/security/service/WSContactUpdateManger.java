package com.tavant.droid.security.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.IBinder;

public class WSContactUpdateManger extends Service {

	private ContentResolver resolver=null;
	@Override
	public void onCreate() {
		super.onCreate();
		resolver=getContentResolver();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

}
