package com.yl.NotificationAlarm;


import java.util.ArrayList;

import com.yl.todo.R;
import com.yl.NotificationBar.CustomNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReciever extends BroadcastReceiver 
{
    @Override
    public void onReceive(Context context, Intent intent_alarm)
    {
    	Class<?> cls;
    	String class_name=intent_alarm.getStringExtra("class");
    	String todo_id = intent_alarm.getStringExtra("todo_id");
    	String msg = intent_alarm.getStringExtra("msg");
    	try {
			cls=Class.forName(class_name);
		} catch (ClassNotFoundException e1) {
			cls=null;
			e1.printStackTrace();
		}
    	
    	if(cls==null)
    		Toast.makeText(context, "class '"+class_name+"' has not been found.", Toast.LENGTH_LONG).show();
    	else {
    		Intent intent = new Intent(context, cls);
    		intent.putExtra("todo_id", todo_id);
    		
    		//Toast.makeText(context, "Alarm Triggerd", Toast.LENGTH_LONG).show();
	        CustomNotification noti = new CustomNotification(context,intent);
	        noti.setTitle(msg);
	        noti.setIcon(R.drawable.ic_launcher);
	        noti.setContentTitle("New Reminder");
	        noti.setText(msg);
	        
	        noti.create();
	        noti.show();
    	}
     }
    
      
	 /*
     public void CancelAlarm(Context context, Class cls)
     {
         Intent intent = new Intent(context, cls);
         PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
         AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
         alarmManager.cancel(sender);
     }
	public void create() {
		Intent myIntent = new Intent(context, MainActivity.class);     
       AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
       PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, 0);

       Calendar calendar = Calendar.getInstance();
           calendar.set(Calendar.HOUR_OF_DAY, 19);
       calendar.set(Calendar.MINUTE, 46);
       calendar.set(Calendar.SECOND, 00);

      alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000 , pendingIntent);  //set repeating every 24 hours
	}
	*/
}
