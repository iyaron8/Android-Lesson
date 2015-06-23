package com.aroundme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SeriousBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals("com.google.android.c2dm.REGISTRATION")) {
				String registration_id=intent.getStringExtra("registration_id");
				Log.i("uo", registration_id);
				String error=intent.getStringExtra("error");
				String unregistered = intent.getStringExtra("unregistarted");
			} else if(action.equals("com.google.android.c2dm.RECEIVE")) {
				String data1=intent.getStringExtra("data1");
				String data2=intent.getStringExtra("data2");
			}
	}

}
