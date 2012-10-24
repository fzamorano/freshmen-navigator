package com.piq.erstieNavi.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class AppStatus {

	private static AppStatus instance = new AppStatus();
	static Context context;
	ConnectivityManager connectivityManager;
	NetworkInfo wifiInfo, mobileInfo;
	boolean connected = false;

	public static AppStatus getInstance(Context ctx) {
		context = ctx;
		return instance;
	}

	public boolean isOnline(Context con) {
		try {
			connectivityManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
			return connected;

		} catch (Exception e) {
			System.out.println("CheckConnectivity Exception: " + e.getMessage());
			Log.v("connectivity", e.toString());
		}
		return connected;
	}

	public boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

	public boolean haveGPSConnection() {
		LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return true;
		} else {
			return false;
		}
	}

}
