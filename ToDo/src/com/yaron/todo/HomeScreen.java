package com.yaron.todo;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.yaron.Analytics.Analytics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class HomeScreen extends Activity {
	
	private ImageButton imgb_blue, imgb_purple, imgb_orange, imgb_green, imgb_red, imgb_gray;
	private Analytics analytics;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		analytics = new Analytics(this);
		btnClick();
		
		((Button) findViewById(R.id.btn_add)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				analytics.sendData("Button click", "Add task was clicked");
				
				Intent intent = new Intent(HomeScreen.this, AddToDo.class);
		        startActivity(intent);
				overridePendingTransition(R.animator.animation_left_to_right1, R.animator.animation_left_to_right2);
			}
		});
		
		servicesConnected();
	}
	

	public void btnClick() {
		final Intent intent = new Intent(HomeScreen.this, MainActivity.class);
		
		imgb_blue = (ImageButton) findViewById(R.id.imgb_blue);
		imgb_purple = (ImageButton) findViewById(R.id.imgb_purple);
		imgb_orange = (ImageButton) findViewById(R.id.imgb_orange);
		imgb_green = (ImageButton) findViewById(R.id.imgb_green);
		imgb_red = (ImageButton) findViewById(R.id.imgb_red);
		imgb_gray = (ImageButton) findViewById(R.id.imgb_gray);

		imgb_blue.setOnClickListener(new View.OnClickListener() {
            
			@Override
			public void onClick(View v) {
				intent.putExtra("page", "all");
	            HomeScreen.this.startActivity(intent);
				overridePendingTransition(R.animator.animation_left_to_right1, R.animator.animation_left_to_right2);
			}
		});
		
		imgb_blue.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
		           imgb_blue.setImageResource(R.drawable.blue_circle_button_press);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	imgb_blue.setImageResource(R.drawable.blue_circle_button);
		        }
				
				return false;
			}
		});

		imgb_purple.setOnClickListener(new View.OnClickListener() {
            
			@Override
			public void onClick(View v) {
				intent.putExtra("page", "today");
	            HomeScreen.this.startActivity(intent);
				overridePendingTransition(R.animator.animation_left_to_right1, R.animator.animation_left_to_right2);
			}
		});

		imgb_purple.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					imgb_purple.setImageResource(R.drawable.purple_circle_button_press);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	imgb_purple.setImageResource(R.drawable.purple_circle_button);
		        }
				
				return false;
			}
		});

		imgb_green.setOnClickListener(new View.OnClickListener() {
            
			@Override
			public void onClick(View v) {
				intent.putExtra("page", "7days");
	            HomeScreen.this.startActivity(intent);
				overridePendingTransition(R.animator.animation_left_to_right1, R.animator.animation_left_to_right2);
			}
		});

		imgb_green.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) { 
					imgb_green.setImageResource(R.drawable.green_circle_button_press);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	imgb_green.setImageResource(R.drawable.green_circle_button);
		        }
				
				return false;
			}
		});

		imgb_orange.setOnClickListener(new View.OnClickListener() {
            
			@Override
			public void onClick(View v) {
				intent.putExtra("page", "30days");
	            HomeScreen.this.startActivity(intent);
				overridePendingTransition(R.animator.animation_left_to_right1, R.animator.animation_left_to_right2);
			}
		});

		imgb_orange.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					imgb_orange.setImageResource(R.drawable.orange_circle_button_press);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	imgb_orange.setImageResource(R.drawable.orange_circle_button);
		        }
				
				return false;
			}
		});

		imgb_red.setOnClickListener(new View.OnClickListener() {
            
			@Override
			public void onClick(View v) {
				intent.putExtra("page", "passed");
	            HomeScreen.this.startActivity(intent);
				overridePendingTransition(R.animator.animation_left_to_right1, R.animator.animation_left_to_right2);
			}
		});

		imgb_red.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					imgb_red.setImageResource(R.drawable.red_circle_button_press);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	imgb_red.setImageResource(R.drawable.red_circle_button);
		        }
				
				return false;
			}
		});

		imgb_gray.setOnClickListener(new View.OnClickListener() {
            
			@Override
			public void onClick(View v) {
				intent.putExtra("page", "done");
	            HomeScreen.this.startActivity(intent);
				overridePendingTransition(R.animator.animation_left_to_right1, R.animator.animation_left_to_right2);
			}
		});

		imgb_gray.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					imgb_gray.setImageResource(R.drawable.gray_circle_button_press);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	imgb_gray.setImageResource(R.drawable.gray_circle_button);
		        }
				
				return false;
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_screen, menu);
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
	
	private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Geofence Detection", "Google Play services is available.");
            return(true);
        } else {
        	Log.d("Error","Google Play Services is not available");
        	return(false);
        }
    }
}
