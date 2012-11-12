package com.piq.erstieNavi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.piq.erstieNavi.model.Building;
import com.piq.erstieNavi.services.BuildingsManager;

public class NaviPointsActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navi_points);
		
	}
	
	public void save(View v) {
		EditText abbrevE = (EditText) findViewById(R.id.abbrevE);
		EditText longiE = (EditText) findViewById(R.id.longiE);
		EditText latE = (EditText) findViewById(R.id.latE);
		EditText nameE = (EditText) findViewById(R.id.nameE);
		if (!nameE.getText().toString().equals("") && !abbrevE.getText().toString().equals("") && !latE.getText().toString().equals("") && !longiE.getText().toString().equals("")) {
			Building b = new Building(nameE.getText().toString(), abbrevE.getText().toString(), Double.parseDouble(latE.getText().toString()), Double.parseDouble(longiE.getText().toString()));
			BuildingsManager.getInstance().addBuilding(b);
			toastL("New Point added: " + b.toString());
			Intent i = new Intent(v.getContext(), MainActivity.class);
			startActivity(i);
		} else {
			toastL("Not every field was filled. Please fill every field and try again! Thanks.");
		}
	}
	
	public void toastL(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	} // end toast
	
	public void receiveLocation(View v) {
		// getCurrentLocation();
		// performReverseGeocodingInBackground();
		// while (geoCodeResult == null) {
		// }
		// Geocoder geocoder;
		// String bestProvider;
		// List<Address> user = null;
		double lat = 0;
		double lng = 0;
		
		// LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		//
		// Criteria criteria = new Criteria();
		// bestProvider = lm.getBestProvider(criteria, false);
		// Location location = lm.getLastKnownLocation(bestProvider);
		//
		// if (location == null) {
		// toastL("Location Not found");
		// } else {
		// geocoder = new Geocoder(this);
		// try {
		// user = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
		// lat = (double) user.get(0).getLatitude();
		// lng = (double) user.get(0).getLongitude();
		// System.out.println(" DDD lat: " + lat + ",  longitude: " + lng);
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		LocationManager locManager;
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, locationListener);
		Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null) {
			lat = location.getLatitude();
			lng = location.getLongitude();
		}
		EditText longiE = (EditText) findViewById(R.id.longiE);
		EditText latE = (EditText) findViewById(R.id.latE);
		longiE.setText("" + lat);
		latE.setText("" + lng);
	}
	
	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}
		
		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}
		
		public void onProviderEnabled(String provider) {
		}
		
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
	
	private void updateWithNewLocation(Location location) {
		String latLongString = "";
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			latLongString = "Lat:" + lat + "\nLong:" + lng;
		} else {
			latLongString = "No location found";
		}
	}
}
