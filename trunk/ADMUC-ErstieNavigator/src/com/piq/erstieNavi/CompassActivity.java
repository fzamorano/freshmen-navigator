package com.piq.erstieNavi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import com.piq.erstieNavi.services.BuildingsManager;
import com.piq.widget.Compass;

public class CompassActivity extends Activity {
	private BuildingsManager bm = BuildingsManager.getInstance();
	
	private static float MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 2f;
	private static long MINIMUM_TIME_BETWEEN_UPDATES = 100L;
	private static String LOCATIONIZE_METHOD;
	private static String PROVIDER;
	
	private Compass compassView;
	
	private Location currentLocation = new Location(PROVIDER);
	private Location toLoc = new Location(PROVIDER);
	
	private SensorManager sensorManager;
	private Sensor sensor;
	private SensorEventListener listener;
	private SensorEventListener magneticFieldListener;
	
	private LocationManager locationService;
	private LocationListener locationListener = new LocationListener() {
		
		public void onLocationChanged(Location location) {
			setCurrentLocation(location);
		}
		
		public void onProviderDisabled(String provider) {
			Toast.makeText(CompassActivity.this, "Provider disabled by the user. GPS turned off", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
			
		}
		
		public void onProviderEnabled(String provider) {
			Toast.makeText(CompassActivity.this, "Provider enabled by the user. GPS turned on", Toast.LENGTH_LONG).show();
			
		}
		
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navi_wo_inet);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = Float.parseFloat(preferences.getString("update_meters", "2"));
		MINIMUM_TIME_BETWEEN_UPDATES = Long.parseLong(preferences.getString("update_seconds", "100"));
		LOCATIONIZE_METHOD = preferences.getString("locationizeMethod", "gps");
		if (LOCATIONIZE_METHOD.equals("gps")) {
			PROVIDER = LocationManager.GPS_PROVIDER;
		}
		if (LOCATIONIZE_METHOD.equals("network")) {
			PROVIDER = LocationManager.NETWORK_PROVIDER;
		}
		if (LOCATIONIZE_METHOD.equals("passive")) {
			PROVIDER = LocationManager.PASSIVE_PROVIDER;
		}
		
		final Bundle data = getIntent().getExtras();
		
		locationService = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationService.requestLocationUpdates(PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
		
		if (data.getString("from").equals("Use Current Location")) {
			currentLocation = locationService.getLastKnownLocation(PROVIDER);
			if (currentLocation == null) {
				currentLocation = locationService.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
				Toast.makeText(getApplicationContext(), "No gps-location found.\n\nRequesting location with location-determination: PASSIVE", Toast.LENGTH_LONG).show();
				Location l = new Location(PROVIDER);
				int tmp = (int) (currentLocation.getLatitude() * 1000000);
				l.setLatitude((double) tmp / 1000000);
				tmp = (int) (currentLocation.getLongitude() * 1000000);
				l.setLongitude((double) tmp / 1000000);
				currentLocation = l;
			}
		} else {
			Location fromLoc = new Location(PROVIDER);
			fromLoc.setLatitude(bm.getRequestedBuilding(data.getString("from")).getLatitude());
			fromLoc.setLongitude(bm.getRequestedBuilding(data.getString("from")).getLongitude());
			currentLocation = fromLoc;
		}
		
		toLoc.setLatitude(bm.getRequestedBuilding(data.getString("to")).getLatitude());
		toLoc.setLongitude(bm.getRequestedBuilding(data.getString("to")).getLongitude());
		
		TextView from = (TextView) findViewById(R.id.locationFrom);
		TextView to = (TextView) findViewById(R.id.locationTo);
		TextView distance = (TextView) findViewById(R.id.distance);
		
		from.setText("Navigating from " + data.getString("from"));
		to.setText("Navigating to " + data.getString("to"));
		distance.setText("Distance in km: " + com.piq.erstieNavi.services.Compass.getHaverSineDistance(currentLocation.getLatitude(), currentLocation.getLongitude(), toLoc.getLatitude(), toLoc.getLongitude()));
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		
		listener = new SensorEventListener() {
			
			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				if (compassView != null) {
					compassView.setNorth(-values[0]);
					compassView.invalidate();
				}
			}
			
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		
		magneticFieldListener = new SensorEventListener() {
			
			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				if (values.length < 3) {
					return;
				}
				
			}
			
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		
		compassView = (Compass) findViewById(R.id.Compass);
		refreshCompass();
	}
	
	private void refreshCompass() {
		if (compassView != null) {
			compassView.setCurrentLocation(currentLocation);
			compassView.setCurrentPlace(bm.getRequestedBuilding(getIntent().getExtras().getString("to")));
			compassView.invalidate();
		}
	}
	
	@Override
	protected void onResume() {
		sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(magneticFieldListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
		locationService.requestLocationUpdates(PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		sensorManager.unregisterListener(listener);
		sensorManager.unregisterListener(magneticFieldListener);
		super.onStop();
	}
	
	private void setCurrentLocation(Location location) {
		if (compassView != null) {
			currentLocation = location;
			refreshCompass();
			
		}
	}
	
	@Override
	protected void onPause() {
		sensorManager.unregisterListener(listener);
		sensorManager.unregisterListener(magneticFieldListener);
		if (locationService != null && locationListener != null)
			locationService.removeUpdates(locationListener);
		super.onPause();
	}
	
}
