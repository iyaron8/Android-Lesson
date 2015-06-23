package com.yl.Geofence;

import com.google.android.gms.location.LocationClient;

import android.content.Context;
import android.content.Intent;

public class GeofenceReminder extends GeofenceManager {
	
	public GeofenceReminder(LocationClient geoLocationClient, Context context) {
		super(geoLocationClient,context);
	}
	
	public void addGeofence(String id, double longitude, double latitude, double radius, Intent intent, ActivityClassGeofence context) {
		super.addGeofence("georemider_"+id, longitude, latitude, radius, intent);
	}

	public void removeGeofence(String id) {
		super.removeGeofence("georeminder_"+id);
	}
}
