package com.tavant.droid.security.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tavant.droid.security.service.WSContactUpdateManger;

public class StartupReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Log.i("TAG","I am starting *********");
		arg0.startService(new Intent(arg0, WSContactUpdateManger.class));

	}

}
