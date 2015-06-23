package com.yl.Geofence;

import java.util.ArrayList;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public abstract class GeofenceManager implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	private LocationClient geoLocationClient;
	private Context context;
	
	public GeofenceManager(LocationClient geoLocationClient, Context context) {
		this.geoLocationClient=geoLocationClient;
		this.context=context;
	}
	
	public void addGeofence(String id, double longitude, double latitude, double radius, Intent intent) {
		ArrayList<Geofence> geofenceList = new ArrayList<Geofence>();
		
		Geofence geofence = new Geofence.Builder()
        .setRequestId(id)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
        .setCircularRegion(latitude, longitude, (float) radius)
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .build();
		geofenceList.add(geofence);
		
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		geoLocationClient.addGeofences(geofenceList, pi, new OnAddGeofencesResultListener() {

			@Override
			public void onAddGeofencesResult(int arg0, String[] arg1) {
				Toast.makeText(context, "Geofence added", Toast.LENGTH_SHORT).show();
			}
			
		});
			
	}
	
	public void removeGeofence(String id) {
		ArrayList<String> arr_list = new ArrayList<String>();
		arr_list.add(id);
		geoLocationClient.removeGeofences(arr_list, new OnRemoveGeofencesResultListener() {
			
			@Override
			public void onRemoveGeofencesByRequestIdsResult(int arg0, String[] arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onRemoveGeofencesByPendingIntentResult(int arg0,
					PendingIntent arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}
}
