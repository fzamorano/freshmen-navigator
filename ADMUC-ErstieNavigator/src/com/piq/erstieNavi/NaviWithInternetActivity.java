package com.piq.erstieNavi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.piq.erstieNavi.services.BuildingsManager;
import com.piq.erstieNavi.services.Compass;
import com.piq.erstieNavi.services.GeoCoderGoogle;

public class NaviWithInternetActivity extends Activity {

	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 5; // in Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds

	private GeoCoderGoogle geoCoder = new GeoCoderGoogle();
	//public GeoCodeResult geoCodeResult;
	public String geoCodeResult = null;

	protected LocationManager locationManager;
	protected Location targetLocation;

	protected Button googleMapsButton;

	private Bundle extras = null;
	private String from;
	private String to;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.navi_inet);

		extras = getIntent().getExtras();
		if (extras != null) {
			from = (String) extras.getString("from");
			to = (String) extras.getString("to");
		}

		googleMapsButton = (Button) findViewById(R.id.reverse_geocoding_button);

		TextView tvFrom = (TextView) findViewById(R.id.from);
		TextView tvTo = (TextView) findViewById(R.id.to);
		TextView tvDistance = (TextView) findViewById(R.id.distance);
		TextView tvDirection = (TextView) findViewById(R.id.direction);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());

		final Location fromL = createLocationWithAbbr(from);
		final Location toL = createLocationWithAbbr(to);
		googleMapsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// String uri = "http://maps.google.com/maps?saddr=" + fromL.getLatitude()+","+fromL.getLongitude()+"&daddr="+toL.getLatitude()+","+toL.getLongitude();
				// Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
				// intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
				// startActivity(intent);
				Intent myIntent = new Intent(v.getContext(), GoogleMapsActivity.class);
				Bundle b = new Bundle();

				b.putDouble("fromLocationLong", fromL.getLongitude());
				b.putDouble("fromLocationLat", fromL.getLatitude());
				b.putDouble("toLocationLong", toL.getLongitude());
				b.putSerializable("toLocationLat", toL.getLatitude());
				myIntent.putExtras(b);
				startActivity(myIntent);
			}
		});

		// request current location and set label with coords and address
		if (from.equals("use current Location")) {
			getCurrentLocation();
			performReverseGeocodingInBackground();
			while (geoCodeResult == null) {
			}
			tvFrom.setText("Navigating from: " + geoCodeResult.toString() + "\nLong.: " + targetLocation.getLongitude() + " Lat.: " + targetLocation.getLatitude());
			geoCodeResult = null;
		} else {
			createLocationWithAbbrAndReverseGeocoding(from);
			while (geoCodeResult == null) {
			}
			tvFrom.setText("Navigating from: " + geoCodeResult.toString() + "\nLong.: " + targetLocation.getLongitude() + " Lat.: " + targetLocation.getLatitude());
			geoCodeResult = null;
		}
		createLocationWithAbbrAndReverseGeocoding(to);
		while (geoCodeResult == null) {
		}

		tvTo.setText("Navigating to: " + geoCodeResult.toString() + "\nLong.: " + targetLocation.getLongitude() + " Lat.: " + targetLocation.getLatitude());
		geoCodeResult = null;

		tvDistance.setText("Distance in km: " + Compass.getHaverSineDistance(fromL.getLatitude(), fromL.getLongitude(), toL.getLatitude(), toL.getLongitude()));

		tvDirection.setText("Just move into the following directon:");
		ImageView image = (ImageView) findViewById(R.id.navi_arrow);
		Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.navi_arrow);
		Matrix mat = new Matrix();
		mat.postRotate((float) Compass.getDegrees(fromL.getLatitude(), fromL.getLongitude(), toL.getLatitude(), toL.getLongitude(), 0));
		Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), mat, true);
		image.setImageBitmap(bMapRotate);
	}

	private void createLocationWithAbbrAndReverseGeocoding(String abbr) {
		Location l = new Location(LocationManager.GPS_PROVIDER);
		l.setLatitude(BuildingsManager.getInstance().getRequestedBuilding(abbr).getLatitude());
		l.setLongitude(BuildingsManager.getInstance().getRequestedBuilding(abbr).getLongitude());
		targetLocation = l;
		performReverseGeocodingInBackground();
	}

	private Location createLocationWithAbbr(String abbr) {
		Location l = new Location(LocationManager.GPS_PROVIDER);
		l.setLatitude(BuildingsManager.getInstance().getRequestedBuilding(abbr).getLatitude());
		l.setLongitude(BuildingsManager.getInstance().getRequestedBuilding(abbr).getLongitude());
		return l;
	}

	protected void performReverseGeocodingInBackground() {
		new ReverseGeocodeLookupTask().execute((Void[]) null);
	}

	protected void showCurrentLocation() {
		getCurrentLocation();
		if (targetLocation != null) {
			String message = String.format("Current Location \n Longitude: %1$s \n Latitude: %2$s", targetLocation.getLongitude(), targetLocation.getLatitude());
			// Toast.makeText(NaviWithInternetActivity.this, message, Toast.LENGTH_LONG).show();
		}
	}

	protected void getCurrentLocation() {
		targetLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			String message = String.format("New Location \n Longitude: %1$s \n Latitude: %2$s", location.getLongitude(), location.getLatitude());
			Toast.makeText(NaviWithInternetActivity.this, message, Toast.LENGTH_LONG).show();
		}

		public void onStatusChanged(String s, int i, Bundle b) {
			Toast.makeText(NaviWithInternetActivity.this, "Provider status changed", Toast.LENGTH_LONG).show();
		}

		public void onProviderDisabled(String s) {
			Toast.makeText(NaviWithInternetActivity.this, "Provider disabled by the user. GPS turned off", Toast.LENGTH_LONG).show();
		}

		public void onProviderEnabled(String s) {
			Toast.makeText(NaviWithInternetActivity.this, "Provider enabled by the user. GPS turned on", Toast.LENGTH_LONG).show();
		}

	}

	public class ReverseGeocodeLookupTask extends AsyncTask<Void, Void, String> {

		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			this.progressDialog = ProgressDialog.show(NaviWithInternetActivity.this, "Please wait...contacting Google!", // title
					"Requesting reverse geocode lookup", // message
					true // indeterminate
			);
		}

		//		@Override
		//		protected GeoCodeResult doInBackground(Void... params) {
		//			if (targetLocation != null) {
		//				geoCodeResult = geoCoder.reverseGeoCode(targetLocation.getLatitude(), targetLocation.getLongitude());
		//			}
		//			return geoCodeResult;
		//		}

		//		@Override
		//		protected void onPostExecute(GeoCodeResult result) {
		//			this.progressDialog.cancel();
		//			// Toast.makeText(NaviWithInternetActivity.this, result.toString(), Toast.LENGTH_LONG).show();
		//		}

		@Override
		protected String doInBackground(Void... params) {
			geoCodeResult = null;
			if (targetLocation != null) {
				geoCodeResult = geoCoder.reverseGeoCode(targetLocation.getLatitude(), targetLocation.getLongitude());
			}
			return geoCodeResult;
		}

		@Override
		protected void onPostExecute(String result) {
			this.progressDialog.cancel();
			// Toast.makeText(NaviWithInternetActivity.this, result.toString(), Toast.LENGTH_LONG).show();
		}

	}

}