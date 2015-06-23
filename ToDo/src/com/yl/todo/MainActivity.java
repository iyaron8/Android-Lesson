package com.yl.todo;

//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.android.gms.location.Geofence;
//import com.yaron.NotificationBar.CustomNotification;

import com.yl.todo.R;
import com.yl.Analytics.Analytics;
import com.yl.DB.DataHandler;
import com.yl.DB.DataHandler.OPEN_DB_FOR;
import com.yl.NotificationAlarm.AlarmReciever;
import com.yl.NotificationAlarm.CustomAlarmNotification;
import com.yl.Utils.SwipeDetector;
import com.yl.Utils.SwipeDetector.Action;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
//import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
//import android.widget.AdapterView.OnItemLongClickListener;
//import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.AbsListView;
import android.widget.AbsListView;
import android.widget.Toast;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import android.widget.Toast;


public class MainActivity extends Activity {
	private Geofence gf;
	private StringAdapter listAdapter ;  
	private Button btn_add;
	private DataHandler db; 
	private ListView mainListView;
	private String sql_extra="";
	private Analytics analytics;
	private SwipeDetector swipe_detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        analytics = new Analytics(this);
        
        swipe_detector = new SwipeDetector();
        Button btn_add = (Button) findViewById(R.id.btn_add);
        TextView txtv_id = (TextView) findViewById(R.id.txtv_id);
        RelativeLayout top_bar = (RelativeLayout) findViewById(R.id.top_bar);
        ListView mainListView = (ListView) findViewById( R.id.list );  
        Calendar cal = Calendar.getInstance();
        long d_date_start, d_date_end;
        
        
        

    	cal.setTime(new Date());
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
		d_date_start = cal.getTimeInMillis();
    	
    	cal.setTime(new Date());
    	cal.set(Calendar.HOUR_OF_DAY, 23);
    	cal.set(Calendar.MINUTE, 59);
    	cal.set(Calendar.SECOND, 59);
		d_date_end = cal.getTimeInMillis();

        
        Intent myIntent = getIntent();
        String page = myIntent.getStringExtra("page");
        if(page.equals("today")) {
        	top_bar.setBackgroundColor(Color.rgb(158, 61, 109));
        	txtv_id.setText("Today");
        	
        	sql_extra="where d_date >= "+ d_date_start+" and d_date <= "+d_date_end;
        } else {
        	if(page.equals("7days")) {
        		top_bar.setBackgroundColor(Color.rgb(71, 137, 83));
        		txtv_id.setText("7 Days up comings");

    			d_date_end+=1000*60*60*24*6; // 6 days + current day = 7
            	sql_extra="where d_date >= "+ d_date_start+" and d_date <= "+d_date_end;
        	} else {
        		if(page.equals("30days")) {
        			top_bar.setBackgroundColor(Color.rgb(254, 134, 15));
        			txtv_id.setText("30 Days up comings");

    				cal.add(Calendar.DAY_OF_MONTH, 29);
        			d_date_end = cal.getTimeInMillis();
                	sql_extra="where d_date >= "+ d_date_start+" and d_date <= "+d_date_end;
        		} else {
        			if(page.equals("passed")) {
        				top_bar.setBackgroundColor(Color.rgb(117, 56, 56));
        				txtv_id.setText("Passed Tasks");
        				

        		    	cal.setTime(new Date());
        				d_date_end = cal.getTimeInMillis();
                    	sql_extra="where d_date <= "+d_date_end+" and finished=0";
        			} else {
        				if(page.equals("done")) {
        					top_bar.setBackgroundColor(Color.rgb(178, 178, 178));
        					txtv_id.setText("Done Tasks");
        					
        	            	sql_extra="where finished=1";
        				} else {
        					// for 'all' and other that not defined
        					txtv_id.setText("All Tasks");
        				}
        			}
        		}
        	}
        }
        
        updateListView();  
        
        mainListView.setOnTouchListener(swipe_detector);
        
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	final int touch_color=Color.parseColor("#cbe6f9");
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//SwipeDetector swipe_detector = new SwipeDetector();
				final View vv = view;
				if(swipe_detector.swipeDetected() && swipe_detector.getAction() == Action.RL) {
					//Toast.makeText(getApplicationContext(), "Swipe Left!", Toast.LENGTH_SHORT).show();
					
					RelativeLayout relativelayout_right_side = (RelativeLayout)(view.findViewById(R.id.relativelayout_right_side));
					Button btn_delete = (Button) view.findViewById(R.id.btn_delete);
					//Button btn_done = (Button) view.findViewById(R.id.btn_done);
					
					relativelayout_right_side.setVisibility(View.VISIBLE);
					btn_delete.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							alertMessageDelete(Integer.parseInt(((TextView) vv.findViewById(R.id.txtv_id)).getText().toString()));
							//Toast.makeText(getApplicationContext(), "clieck delete", Toast.LENGTH_SHORT).show();
						}
					});
				} else { 
					if(swipe_detector.swipeDetected() && swipe_detector.getAction() == Action.LR) {
						//Toast.makeText(getApplicationContext(), "Swipe Right!", Toast.LENGTH_SHORT).show();
						if(vv!=null) {
							RelativeLayout relativelayout_right_side = (RelativeLayout)(vv.findViewById(R.id.relativelayout_right_side));
							relativelayout_right_side.setVisibility(View.GONE);
						}
					} else {
						//LinearLayout inearlayout_main = (LinearLayout) view.findViewById(R.id.linearlayout_main);
						view.setBackgroundColor(touch_color);
						TextView txtv_id = (TextView) view.findViewById(R.id.txtv_id);
						Intent intent = new Intent(MainActivity.this, EditItem.class);
						intent.putExtra("todo_id", txtv_id.getText().toString());
				        startActivity(intent);
				        overridePendingTransition(R.animator.animation_left_to_right1, R.animator.animation_left_to_right2);
					}
	            }
			}
		});
        
        mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				alertMessageDelete(Integer.parseInt(((TextView) arg1.findViewById(R.id.txtv_id)).getText().toString()));
				return false;
			}
		});
        
        btn_add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, AddToDo.class);
		        startActivity(intent);
		        overridePendingTransition(R.animator.animation_left_to_right1, R.animator.animation_left_to_right2);
			}
		});
        
        
    }
 
    public void onResume() {
    	super.onResume();
    	updateListView();
    }
    
    public void updateListView() { 
          
        // Create ArrayAdapter using the planet list.  
    	ListView mainListView = (ListView) findViewById( R.id.list ); 
        listAdapter = new StringAdapter(this, new ArrayList<ContentValues>());  
        
        db = new DataHandler(getBaseContext());
        db.open(OPEN_DB_FOR.READ);
        String sql="select * from "+DataHandler._TABLE_NAME+" "+sql_extra+" order by finished ASC, ID DESC";
        Log.d("updateListView()",sql);
        Cursor cursor = db.returnData(sql);
        cursor.moveToFirst();
        ContentValues conv;
        for(int i=0;i<cursor.getCount();i++) {
        	conv = new ContentValues();
        	conv.put("text", cursor.getString(cursor.getColumnIndex("msg")));
        	conv.put("id", cursor.getString(cursor.getColumnIndex("ID")));
        	conv.put("d_date", cursor.getString(cursor.getColumnIndex("d_date")));
        	conv.put("finished", cursor.getString(cursor.getColumnIndex("finished")));
        	listAdapter.add(conv);
        	cursor.moveToNext();
        }
        db.close();
        
        // Add more planets. If you passed a String[] instead of a List<String>   
        // into the ArrayAdapter constructor, you must not add more items.   
        // Otherwise an exception will occur.  
        /*
        listAdapter.add( "Ceres" );  
        listAdapter.add( "Pluto" );  
        listAdapter.add( "Haumea" );  
        listAdapter.add( "Makemake" );  
        listAdapter.add( "Eris" );  
        */  
        // Set the ArrayAdapter as the ListView's adapter.  
        if(mainListView!=null)
        	mainListView.setAdapter( listAdapter );  
    }
    
    
    public void alertMessageDelete(final int id) {
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			if(which==DialogInterface.BUTTON_POSITIVE) {
    				CustomAlarmNotification custom_alarm = new CustomAlarmNotification(getBaseContext(), AlarmReciever.class);
    				custom_alarm.cancelAlarm(EditItem.class, id);
    				//Toast.makeText(MainActivity.this, "Yes Clicked: "+id,Toast.LENGTH_LONG).show();
    				db = new DataHandler(getBaseContext());
    				db.open(OPEN_DB_FOR.WRITE);
					db.deleteData(id);
					db.close();
					updateListView();
    			}
    		}
    	};
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Do you want to delete this?") .setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }
    
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.animation_right_to_left1, R.animator.animation_right_to_left2);   
    }
    
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    
    private class StringAdapter extends ArrayAdapter<ContentValues> {
		Context context;
		List<ContentValues> list_con;
		
		public StringAdapter(Context context, List<ContentValues> list_con) {
			super(context, R.layout.listview_todo, list_con);
			this.context=context;
			this.list_con=list_con;
			
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView = convertView;
			LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			if(itemView==null)
				itemView = inflater.inflate(R.layout.listview_todo, parent, false);
			
			ContentValues curr = list_con.get(position);

			LinearLayout ll = (LinearLayout) itemView.findViewById(R.id.linearlayout_cont);
			TextView place_name = (TextView) itemView.findViewById(R.id.txtv_lv_text);
			TextView date_and_time = (TextView) itemView.findViewById(R.id.txtv_time_and_date);
			Button btn_done = (Button) itemView.findViewById(R.id.btn_done);
			//TextView txtv_task_finish = (TextView) itemView.findViewById(R.id.txtv_task_finish);
			final TextView id = (TextView) itemView.findViewById(R.id.txtv_id);
			
			
			Calendar cal = Calendar.getInstance();
			
			//txtv_task_finish.setText(curr.get("finished").toString());
			cal.setTimeInMillis(Long.parseLong(curr.get("d_date").toString()));
			String d_date = DateFormat.format("dd/MM/yyyy h:m:s", cal).toString();
			final boolean is_finish=curr.get("finished").toString().equals("0");
			
			if(is_finish) {
				ll.setBackgroundColor(Color.WHITE);
				btn_done.setText("Done");
			} else {
				ll.setBackgroundColor(Color.rgb(202, 209, 213));
				btn_done.setText("Undone");
			}
			btn_done.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//Toast.makeText(getApplicationContext(), "clieck done", Toast.LENGTH_SHORT).show();
					db.open(OPEN_DB_FOR.WRITE);
					db.finishTask(Integer.parseInt(id.getText().toString()), is_finish);
					db.close();
					updateListView();
				}
			});
			place_name.setText(curr.get("text").toString());
			date_and_time.setText(d_date);
			//place_name.setText(d_date);
			id.setText(curr.get("id").toString());
			/*
			if(curr.get("finished").toString().equals("1"))
				cbx_finish.setChecked(true);
			else
				cbx_finish.setChecked(false);
			*/
			//return super.getView(position, convertView, parent);
			return(itemView);
		}
		
	}
}
