package com.piq.erstieNavi;


import com.piq.erstieNavi.R;
import com.piq.erstieNavi.services.BuildingsManager;
import com.piq.widget.Compass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

public class CompassActivity extends Activity
{
	private BuildingsManager bm = BuildingsManager.getInstance();
	
	private static final float MINDISTANCE = 5f;
	private static final long MINTIME = 1000L;
	
	private Compass compassView;
	
	private Location currentLocation = new Location(LocationManager.GPS_PROVIDER);
	private Location toLoc = new Location(LocationManager.GPS_PROVIDER);
	
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
			Intent intent = new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
			
		}

		public void onProviderEnabled(String provider) {
			Toast.makeText(CompassActivity.this, "Provider enabled by the user. GPS turned on", Toast.LENGTH_LONG).show();
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {

			
		}
		
	};
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.navi_wo_inet);
        
      final Bundle data = getIntent().getExtras();
        
        if(data.getString("from").equals("Use Current Location"))
		{
        	currentLocation = locationService.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		else
		{
			Location fromLoc = new Location(LocationManager.GPS_PROVIDER);
			fromLoc.setLatitude(bm.getRequestedBuilding(data.getString("from")).getLatitude());
			fromLoc.setLongitude(bm.getRequestedBuilding(data.getString("from")).getLongitude());
			currentLocation = fromLoc;
		}
		
		toLoc.setLatitude(bm.getRequestedBuilding(data.getString("to")).getLatitude());
		toLoc.setLongitude(bm.getRequestedBuilding(data.getString("to")).getLongitude());
	
		sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		locationService = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationService.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINTIME, MINDISTANCE, locationListener);

		listener = new SensorEventListener() 
		{
	 
			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				if (compassView != null) {
						compassView.setNorth(- values[0]);
						compassView.invalidate();
				}
			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
 
		magneticFieldListener = new SensorEventListener() {

			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				if(values.length < 3) {
					return;
				}
      
			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
	

	compassView = (Compass)findViewById(R.id.Compass);
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
    protected void onResume()
    {
        sensorManager.registerListener(listener, sensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(magneticFieldListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
        locationService.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINTIME, MINDISTANCE, locationListener);
        super.onResume();
    }

    @Override
    protected void onStop()
    {
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
		if(locationService!=null && locationListener != null)
			locationService.removeUpdates(locationListener);
		super.onPause();
	}
	
}
