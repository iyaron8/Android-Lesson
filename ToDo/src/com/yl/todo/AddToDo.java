package com.yl.todo;

import java.io.IOException;
import java.security.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.yl.todo.R;
import com.yl.Analytics.Analytics;
import com.yl.DB.DataHandler;
import com.yl.DB.DataHandler.OPEN_DB_FOR;
import com.yl.Geofence.GeofenceReminder;
import com.yl.NotificationAlarm.AlarmReciever;
import com.yl.NotificationAlarm.CustomAlarmNotification;
import com.yl.Utils.InternetConnection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddToDo extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	
	static boolean current_connection_available;
	LocationClient geoLocationClient;
	private int google_play_result_code;
    public String monitoredLounges = "";
    private Address address=null;
    private Geocoder geocoder;
    private Analytics analytics;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_to_do);
		
		final Button btn_send = (Button) findViewById(R.id.btn_send);
		final EditText etxt_todo_to_add = (EditText) findViewById(R.id.etxt_todo_to_add);
		final EditText etxt_location_geofence = (EditText) findViewById(R.id.etxt_location_geofence);
		analytics = new Analytics(this);
		
		
        google_play_result_code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(google_play_result_code == ConnectionResult.SUCCESS) {
            geoLocationClient = new LocationClient(this, this, this);
            geoLocationClient.connect();
            
            geocoder = new Geocoder(this);
		}
		
		btn_send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(etxt_todo_to_add.getText().toString().equals(""))
					Toast.makeText(getBaseContext(), "Please insert a task.", Toast.LENGTH_LONG).show();
				else {
					if(!etxt_location_geofence.getText().toString().equals("") && address==null)
						Toast.makeText(getBaseContext(), "Address has not been found", Toast.LENGTH_LONG).show();
					else {
						Calendar cal = Calendar.getInstance();
						DatePicker dpcr = (DatePicker) findViewById(R.id.dpcr_date_select);
						TimePicker tpcr = (TimePicker) findViewById(R.id.tpcr_time_select);
						tpcr.clearFocus();
						
						cal.set(Calendar.MONTH, dpcr.getMonth());
						cal.set(Calendar.DAY_OF_MONTH, dpcr.getDayOfMonth());
						cal.set(Calendar.YEAR, dpcr.getYear());
						cal.set(Calendar.HOUR_OF_DAY, tpcr.getCurrentHour());
						cal.set(Calendar.MINUTE, tpcr.getCurrentMinute());
						
						DataHandler db = new DataHandler(getBaseContext());
						db.open(OPEN_DB_FOR.WRITE);
						db.insertData(etxt_todo_to_add.getText().toString(), cal, etxt_location_geofence.getText().toString());
						
						Cursor cursor = db.returnData("select * from "+DataHandler._TABLE_NAME+" order by ID DESC LIMIT 1");
						cursor.moveToFirst();
						Intent intent = new Intent(getBaseContext(), EditItem.class);
						intent.putExtra("todo_id", cursor.getString(cursor.getColumnIndex("ID")));
						intent.putExtra("msg", cursor.getString(cursor.getColumnIndex("msg")));
				        int todo_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ID")));
				        
						CustomAlarmNotification custom_alarm = new CustomAlarmNotification(getBaseContext(), AlarmReciever.class);
						custom_alarm.createAlarm(EditItem.class, intent, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0);
						
						
						
						if(InternetConnection.isNetworkConnected(getBaseContext()) && google_play_result_code == ConnectionResult.SUCCESS && address!=null) {
							GeofenceReminder geo_reminder = new GeofenceReminder(geoLocationClient, getApplicationContext());
							geo_reminder.addGeofence(String.valueOf(todo_id), address.getLongitude(), address.getLatitude(), 1000.00, intent);
						}
						
						analytics.sendData("Button click", "New task added");
						
						db.close();
						finish();
						AddToDo.this.overridePendingTransition(R.animator.animation_right_to_left1, R.animator.animation_right_to_left2);
					}
				}
			}
		});
		
		

		etxt_location_geofence.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if(InternetConnection.isNetworkConnected(getBaseContext()))
					current_connection_available=true;
				else
					current_connection_available=false;
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(!current_connection_available) {
					address=null;
					Toast.makeText(getBaseContext(), "Connection is not available. Can't find address.", Toast.LENGTH_SHORT).show();
				} else {
			        if (google_play_result_code == ConnectionResult.SUCCESS) {
						try {
							
							List<Address> address_list = geocoder.getFromLocationName(s.toString(), 1);
							if(!address_list.isEmpty()) {
								etxt_location_geofence.setBackgroundColor(Color.WHITE);
								address = address_list.get(0);
							} else {
								address=null;
								if(etxt_location_geofence.getText().equals(""))
									etxt_location_geofence.setBackgroundColor(Color.WHITE);
								else
									etxt_location_geofence.setBackgroundColor(Color.RED);
							}
						} catch (IOException e) {
							Toast.makeText(getBaseContext(), "Geocoder internal device exception. Please reboot your device.", Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
			        }
				}
			}
		});
	}
	
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.animation_right_to_left1, R.animator.animation_right_to_left2);   
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_to_do, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
}