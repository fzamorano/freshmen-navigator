package com.piq.erstieNavi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.piq.erstieNavi.model.Building;
import com.piq.erstieNavi.services.BuildingsManager;

public class NaviPointsActivity extends Activity {
	
private Location recLoc = null;
private CheckBox locCB;
	
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navi_points);
		
		
		locCB = (CheckBox) this.findViewById(R.id.locCheckBox);
		
		locCB.setOnClickListener(new View.OnClickListener()
		{
			TextView t1 = (TextView) findViewById(R.id.longi);
			EditText e1 = (EditText) findViewById(R.id.longiE);
			TextView t2 = (TextView) findViewById(R.id.lat);
			EditText e2 = (EditText) findViewById(R.id.latE);
			
			public void onClick(View v) {
				
				if (locCB.isChecked())
				{
					t1.setEnabled(false);
					e1.setEnabled(false);
					t2.setEnabled(false);
					e2.setEnabled(false);
					
					receiveLocation();
					
					Double lat = recLoc.getLatitude();
					Double longi = recLoc.getLongitude();
					
					toastL("Long.:" + longi.toString() + "/" + "Lat.:" + lat.toString());
				}
				else
				{
					t1.setEnabled(true);
					e1.setEnabled(true);
					t2.setEnabled(true);
					e2.setEnabled(true);
				}
				
			}
		});
		
	}
	
	public void save(View v) 
	{
		EditText abbrevE = (EditText) findViewById(R.id.abbrevE);
		EditText nameE = (EditText) findViewById(R.id.nameE);
		
		if (!nameE.getText().toString().equals("") && !abbrevE.getText().toString().equals(""))
		{
			if(!locCB.isChecked())
			{
				EditText longiE = (EditText) findViewById(R.id.longiE);
				EditText latE = (EditText) findViewById(R.id.latE);
				
				if(!latE.getText().toString().equals("") && !longiE.getText().toString().equals(""))
				{
					Building b = new Building(nameE.getText().toString(), 
											  abbrevE.getText().toString(), 
											  Double.parseDouble(latE.getText().toString()), 
											  Double.parseDouble(longiE.getText().toString()));
					BuildingsManager.getInstance().addBuilding(b);
					toastL("New Point added: " + b.toString());
					Intent i = new Intent(v.getContext(), Main.class);
					startActivity(i);
				}
				else
				{
					toastL("Not every field was filled. Please fill every field and try again! Thanks.");
				}
			}
			else
			{
				Building b = new Building(nameE.getText().toString(), 
										  abbrevE.getText().toString(), recLoc.getLatitude(),
										  recLoc.getLongitude());
				BuildingsManager.getInstance().addBuilding(b);
				toastL("New Point added: " + b.toString());
				Intent i = new Intent(v.getContext(), Main.class);
				startActivity(i);
			}
		}
		else
		{
			toastL("Not every field was filled. Please fill every field and try again! Thanks.");
		}
				
					
	}
	
	public void toastL(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}
	
	public void receiveLocation() 
	{
		LocationManager locManager;
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, locationListener);
		
		recLoc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		int lat = (int) (recLoc.getLatitude() *1E6);
		int longi = (int) (recLoc.getLongitude() *1E6);
		recLoc.setLatitude((double)lat /1E6);
		recLoc.setLongitude((double)longi/1E6);
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
