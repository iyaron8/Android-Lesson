package com.yaron.Analytics;

import java.util.HashMap;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.yaron.todo.R;

public class Analytics {
	public static int GENERAL_TRACKER = 0;
	private Context context;
	private Tracker t;
	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
	}
	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
	
	public Analytics(Context context) {
		this.context=context;
		t = getTracker(TrackerName.APP_TRACKER);
	}
	
	public void sendData(String category, String opera) {
		t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(opera).build());
	}

	synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
			Tracker tr = analytics.newTracker(R.xml.app_tracker);
			mTrackers.put(trackerId, tr);
		}
		return mTrackers.get(trackerId);
	}
}
