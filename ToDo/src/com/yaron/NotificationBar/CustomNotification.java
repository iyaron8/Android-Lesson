package com.yaron.NotificationBar;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.widget.Toast;

public class CustomNotification {
	private Context context;
	private String title;
	private String content_title;
	private String text;
	private int drawable;
	private boolean close_on_click;
	private Notification notification;
	private Intent intent;
	
	public CustomNotification(Context context, Intent intent) {
		this.context=context;
		this.intent=intent;
		//Toast.makeText(context, "id="+intent.getExtras().getString("todo_id"), Toast.LENGTH_LONG).show();
		close_on_click=true; // close on touch by default
	}
	
	public void setTitle(String title) {
		this.title=title;
	}
	
	public void setContentTitle(String ct) {
		this.content_title=ct;
	}
	
	public void setText(String text) {
		this.text=text;
	}
	
	public void setIcon(int drawable) {
		this.drawable=drawable;
	}
	
	public void setCloseOnClick(boolean flag) {
		this.close_on_click=flag;
	}
	
	public void create() {
		
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification = new Notification.Builder(context)
		.setTicker(title)
		.setDefaults(Notification.DEFAULT_VIBRATE)
		.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
		.setContentTitle(content_title)
		.setContentText(text)
		.setSmallIcon(drawable)
		.setContentIntent(pIntent)
		.getNotification();
		
		if(this.close_on_click)
			notification.flags=Notification.FLAG_AUTO_CANCEL;
		
		
	}
	
	public void show() {
		NotificationManager notification_manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE); // display notification
		notification_manager.notify(0, notification);
	}
}
