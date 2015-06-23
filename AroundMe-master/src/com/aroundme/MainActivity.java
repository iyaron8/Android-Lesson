package com.aroundme;

import java.net.URLEncoder;

import com.aroundme.deviceinfoendpoint.model.DeviceInfo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;

/**
 * The Main Activity.
 */
public class MainActivity extends Activity {


	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started."
	 */
	
	//This is the project ID related to 'AroundMe'.
	String SENDER_ID = "1047488186224";
	String regid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btn_reg = (Button) findViewById(R.id.btn_reg);
		
		btn_reg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent registration_intent = new Intent("com.google.android.c2dm.intent.REGISTER");
				registration_intent.putExtra("app", PendingIntent.getBroadcast(v.getContext(), 0, new Intent(), 0));
				registration_intent.putExtra("sender", SENDER_ID);
				startService(registration_intent); 
				
			}
		});
		
		
	}
	
	
}
