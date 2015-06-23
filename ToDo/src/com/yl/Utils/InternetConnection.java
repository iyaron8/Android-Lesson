package com.yl.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public abstract class InternetConnection {

	public static boolean isNetworkConnected(Context context) {
	  ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	  NetworkInfo ni = cm.getActiveNetworkInfo();
	  if(ni!=null && ni.isConnected() && ni.isAvailable())
		  return(true);
	  else
		  return(false);
	 }
}
