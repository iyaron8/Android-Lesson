package com.yaron.NotificationAlarm;

import java.util.Calendar;

import com.yaron.NotificationBar.CustomNotification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CustomAlarmNotification {

	public Context context;
	public Class<? extends BroadcastReceiver> receiver;
	
	public CustomAlarmNotification(Context context, Class<? extends BroadcastReceiver > cls_receiver) {
		this.context=context;
		receiver=cls_receiver;
	}

	/*
	 * The intent here is ONLY for get data by: intent.getStringExtra(...)
	 */
	public void createAlarm(Class<?> cls_to_go, Intent intent, int day, int month, int year, int hour, int minute, int second) {
		String todo_id = intent.getStringExtra("todo_id");
		String msg = intent.getStringExtra("msg");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        
		Long time=calendar.getTimeInMillis(); //new GregorianCalendar().getTimeInMillis()+5000;
		Intent intent_alarm = new Intent(context, AlarmReciever.class);
		
		// we muse send the class name (name&path) of the class we want to open the activity
		intent_alarm.putExtra("class", cls_to_go.getName()); // Class.getName() return the full path of the class. for example: com.yaron.todo.EditItem
		intent_alarm.putExtra("todo_id", todo_id);
		intent_alarm.putExtra("msg", msg);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(context, Integer.parseInt(todo_id), intent_alarm, PendingIntent.FLAG_UPDATE_CURRENT));
        
	}
	
	public void cancelAlarm(Class<?> cls, int alarm_id) {
		Intent intent_alarm = new Intent(context, AlarmReciever.class); // needs to be the same as intent_alarm of the method createAlarm
		intent_alarm.putExtra("class", cls.getName()); // Class.getName() return the full path of the class. for example: com.yaron.todo.EditItem
		
		// alarm_id is the key to recognized what to cancel 
        PendingIntent sender = PendingIntent.getBroadcast(context, alarm_id, intent_alarm, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
	}
	
}
