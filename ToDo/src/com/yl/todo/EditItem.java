package com.yl.todo;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
//import com.google.analytics.tracking.android.MapBuilder;
//import com.yaron.NotificationBar.CustomNotification;

import com.yl.todo.R;
import com.yl.Analytics.Analytics;
import com.yl.DB.DataHandler;
import com.yl.DB.DataHandler.OPEN_DB_FOR;
import com.yl.Geofence.GeofenceReminder;
import com.yl.NotificationAlarm.AlarmReciever;
import com.yl.NotificationAlarm.CustomAlarmNotification;
import com.yl.Utils.InternetConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
//import android.text.format.DateFormat;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.EditText;
import android.widget.Toast;

public class EditItem extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

	static boolean current_connection_available;
	LocationClient geoLocationClient;
	private int google_play_result_code;
    public String monitoredLounges = "";
    private Address address=null;
    private Geocoder geocoder;
    
	private int todo_id;
	private DataHandler db;
	private EditText etxt;
	private DatePicker dpcr;
	private TimePicker tpcr;
	private EditText etxt_location_geofence;
	private boolean is_finished;
	private Analytics analytics;
	//private ToggleButton tglb_finish;
	//private EasyTracker easy_tracker=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_item);
		
		analytics = new Analytics(this);
		
		google_play_result_code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(google_play_result_code == ConnectionResult.SUCCESS) {
            geoLocationClient = new LocationClient(this, this, this);
            geoLocationClient.connect();
            geocoder = new Geocoder(this);
		}
		
		Intent intent = getIntent();
		todo_id = Integer.parseInt(intent.getStringExtra("todo_id"));
		
		Button btn_send = (Button) findViewById(R.id.btn_send);
		Button btn_delete = (Button) findViewById(R.id.btn_delete_current);
		dpcr = (DatePicker) findViewById(R.id.dpcr_date_select);
		tpcr = (TimePicker) findViewById(R.id.tpcr_time_select);
		
		etxt = (EditText) findViewById(R.id.etxt_todo_to_add);
		etxt_location_geofence = (EditText) findViewById(R.id.etxt_location_geofence);
		Calendar cal = Calendar.getInstance();
		db = new DataHandler(getBaseContext());
        db.open(OPEN_DB_FOR.READ);
        Cursor cursor = db.returnData("select * from "+DataHandler._TABLE_NAME+" where ID="+todo_id);
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
        	etxt.setText(cursor.getString(cursor.getColumnIndex("msg")));
        	cal.setTimeInMillis(Long.parseLong(cursor.getString(cursor.getColumnIndex("d_date"))));
        	dpcr.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        	tpcr.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
        	tpcr.setCurrentMinute(cal.get(Calendar.MINUTE));
        	etxt_location_geofence.setText(cursor.getString(cursor.getColumnIndex("geofence_address")));
        	
        	
        	if(cursor.getString(cursor.getColumnIndex("finished")).equals("1"))
        		is_finished = true;
        	else
        		is_finished = false;
        }
        db.close();
        
        
        btn_send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(etxt.getText().toString().equals(""))
					Toast.makeText(getBaseContext(), "Please insert a task.", Toast.LENGTH_LONG).show();
				else {
					if(!etxt_location_geofence.getText().toString().equals("") && address==null)
						Toast.makeText(getBaseContext(), "Address has not been found", Toast.LENGTH_LONG).show();
					else {
						Calendar cal = Calendar.getInstance();
						tpcr.clearFocus();
						
						cal.set(Calendar.MONTH, dpcr.getMonth());
						cal.set(Calendar.DAY_OF_MONTH, dpcr.getDayOfMonth());
						cal.set(Calendar.YEAR, dpcr.getYear());
						cal.set(Calendar.HOUR_OF_DAY, tpcr.getCurrentHour());
						cal.set(Calendar.MINUTE, tpcr.getCurrentMinute());
						
						
						db.open(OPEN_DB_FOR.WRITE);
						db.updateData(todo_id, etxt.getText().toString(), cal, is_finished, etxt_location_geofence.getText().toString());
						db.close();
						
						
						if(Calendar.getInstance().getTimeInMillis()<cal.getTimeInMillis()) { // show alert only for future
							Intent intent = new Intent(getBaseContext(), EditItem.class);
							intent.putExtra("todo_id", Integer.toString(todo_id));
							intent.putExtra("msg", etxt.getText().toString());
							
							CustomAlarmNotification custom_alarm = new CustomAlarmNotification(getBaseContext(), AlarmReciever.class);
							custom_alarm.createAlarm(EditItem.class, intent, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0);
							
							
							intent = new Intent(getBaseContext(), EditItem.class);
							intent.putExtra("todo_id", Integer.toString(todo_id));
							intent.putExtra("msg", etxt.getText().toString());
							
							
							GeofenceReminder geo_reminder = new GeofenceReminder(geoLocationClient, getApplicationContext());
							geo_reminder.removeGeofence(Integer.toString(todo_id));
							if(google_play_result_code == ConnectionResult.SUCCESS && address!=null)
								geo_reminder.addGeofence(String.valueOf(todo_id), address.getLongitude(), address.getLatitude(), 1000.00, intent);
							
						}
						
						analytics.sendData("Button click", "Task has been updated");
						
						finish();
						EditItem.this.overridePendingTransition(R.animator.animation_right_to_left1, R.animator.animation_right_to_left2);
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
					if(InternetConnection.isNetworkConnected(getBaseContext()) && google_play_result_code == ConnectionResult.SUCCESS) {
			        	try {
							List<Address> address_list = geocoder.getFromLocationName(s.toString(), 1);
							if(!address_list.isEmpty()) {
								etxt_location_geofence.setBackgroundColor(Color.WHITE);
								address = address_list.get(0);
								//Toast.makeText(getApplicationContext(), String.valueOf(address.getLatitude()), Toast.LENGTH_SHORT).show();
							} else {
								address=null;
								if(etxt_location_geofence.getText().toString().equals(""))
									etxt_location_geofence.setBackgroundColor(Color.WHITE);
								else {
									etxt_location_geofence.setBackgroundColor(Color.RED);
									Toast.makeText(getApplicationContext(), etxt_location_geofence.getText(), Toast.LENGTH_LONG).show();
								}
							}
						} catch (IOException e) {
							Toast.makeText(getBaseContext(), "Geocoder internal exception device. Please reboot your device.", Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
			        }
				}
			}
		});
        
		/*
		 * those 3 line MUST be after addTextChangedListener,
		 * because if it doesn't, onTextChanged will not invoke
		 * and that will cause to address be NULL!  
		 */
		String s=etxt_location_geofence.getText().toString();
		etxt_location_geofence.setText("");
		etxt_location_geofence.setText(s);
		
		
        btn_delete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				alertMessageDelete(todo_id);
			}
		});
	}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.animation_right_to_left1, R.animator.animation_right_to_left2);   
    }
    
	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

    public void alertMessageDelete(final int id) {
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			if(which==DialogInterface.BUTTON_POSITIVE) {
	    				//Toast.makeText(MainActivity.this, "Yes Clicked: "+id,Toast.LENGTH_LONG).show();
	    				deleteThis(id);
						finish();
						EditItem.this.overridePendingTransition(R.animator.animation_right_to_left1, R.animator.animation_right_to_left2);
    			}
    		}
    	};
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Do you want to delete this?") .setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }
    
    public void deleteThis(int id) {
    	//db = new DataHandler(getBaseContext());
		db.open(OPEN_DB_FOR.WRITE);
		db.deleteData(id);
		
		db.close();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_item, menu);
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