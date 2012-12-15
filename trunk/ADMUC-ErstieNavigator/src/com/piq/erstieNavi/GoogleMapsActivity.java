package com.piq.erstieNavi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.piq.erstieNavi.googlemaps.GoogleParser;
import com.piq.erstieNavi.googlemaps.Parser;
import com.piq.erstieNavi.googlemaps.Route;
import com.piq.erstieNavi.googlemaps.RouteOverlay;
import com.piq.erstieNavi.model.Building;
import com.piq.erstieNavi.model.MyItemizedOverlay;
import com.piq.erstieNavi.model.MyOverlayItem;
import com.piq.erstieNavi.services.BuildingsManager;
import com.piq.erstieNavi.services.Compass;
import com.piq.erstieNavi.services.ReceiveDirections;

public class GoogleMapsActivity extends MapActivity {
	
	private Bundle extras = null;
	Location locFrom;
	Location locTo;
	protected LocationManager locationManager;
	private Location currentLocation;
	private ArrayList<GeoPoint> geoItems;
	private List<Overlay> mapOverlaysList;
	private MyItemizedOverlay itemizedOverlayC;
	private MyOverlayItem overlayitemC;
	private MapView mapView;
	private static float MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 5f; // in Meters
	private static long MINIMUM_TIME_BETWEEN_UPDATES = 1000L; // in Milliseconds
	private static String LOCATIONIZE_METHOD;
	private static String PROVIDER;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = Float.parseFloat(preferences.getString("update_meters", "5"));
		MINIMUM_TIME_BETWEEN_UPDATES = Long.parseLong(preferences.getString("update_seconds", "1000"));
		LOCATIONIZE_METHOD = preferences.getString("locationizeMethod", "gps");
		if(LOCATIONIZE_METHOD.equals("gps")) {
			PROVIDER = LocationManager.GPS_PROVIDER;
		}
		if(LOCATIONIZE_METHOD.equals("network")) {
			PROVIDER = LocationManager.NETWORK_PROVIDER;
		}
		if(LOCATIONIZE_METHOD.equals("passive")) {
			PROVIDER = LocationManager.PASSIVE_PROVIDER;
		}
		
		Location locFrom = new Location(PROVIDER);
		Location locTo = new Location(PROVIDER);
		
		extras = getIntent().getExtras();
		if (extras != null) {
			locFrom.setLongitude(extras.getDouble("fromLocationLong"));
			locFrom.setLatitude(extras.getDouble("fromLocationLat"));
			locTo.setLongitude(extras.getDouble("toLocationLong"));
			locTo.setLatitude(extras.getDouble("toLocationLat"));
		}
		
		Building from = BuildingsManager.getInstance().getRequestedBuilding(locFrom);
		Building to = BuildingsManager.getInstance().getRequestedBuilding(locTo);
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
		
		setContentView(R.layout.googlemap);
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawableF = this.getResources().getDrawable(R.drawable.arrow_from);
		MyItemizedOverlay itemizedOverlayF = new MyItemizedOverlay(drawableF, this);
		GeoPoint fromPoint = new GeoPoint((int) (locFrom.getLatitude() * 1E6), (int) (locFrom.getLongitude() * 1E6));
		MyOverlayItem overlayitemF = new MyOverlayItem(fromPoint, from.getName(), from.getAbbrev());
		
		Drawable drawableT = this.getResources().getDrawable(R.drawable.arrow_to);
		MyItemizedOverlay itemizedOverlayT = new MyItemizedOverlay(drawableT, this);
		GeoPoint toPoint = new GeoPoint((int) (locTo.getLatitude() * 1E6), (int) (locTo.getLongitude() * 1E6));
		MyOverlayItem overlayitemT = new MyOverlayItem(toPoint, to.getName(), to.getAbbrev());
		
		getCurrentLocation();
		Drawable drawableC = this.getResources().getDrawable(R.drawable.current_point);
		itemizedOverlayC = new MyItemizedOverlay(drawableC, this);		
		GeoPoint currentPoint = new GeoPoint((int) (currentLocation.getLatitude() * 1E6), (int) (currentLocation.getLongitude() * 1E6));
		overlayitemC = new MyOverlayItem(currentPoint, "You are here", "Your position");
		
		// TODO: draw the connecton between google´s startpoint and the given geoStart - maybe just draw it by hand in routeoverlay
		
		Route route = null;
		try {
			route = new ReceiveDirections().execute(fromPoint, toPoint).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RouteOverlay routeOverlay = new RouteOverlay(route, Color.BLUE);
		mapView.getOverlays().add(routeOverlay);
		
		geoItems = new ArrayList<GeoPoint>();
		geoItems.add(fromPoint);
		geoItems.add(toPoint);
		geoItems.add(currentPoint);
		
		itemizedOverlayF.addOverlay(overlayitemF);
		itemizedOverlayT.addOverlay(overlayitemT);
		itemizedOverlayC.addOverlay(overlayitemC);
		
		mapOverlaysList = mapOverlays;
		mapOverlaysList.add(itemizedOverlayF);
		mapOverlaysList.add(itemizedOverlayT);
		mapOverlaysList.add(itemizedOverlayC);
		
		// to fit the zoom of the map - see all geopoints of interets
		int minLat = Integer.MAX_VALUE;
		int maxLat = Integer.MIN_VALUE;
		int minLon = Integer.MAX_VALUE;
		int maxLon = Integer.MIN_VALUE;
		
		for (GeoPoint item : geoItems) {
			int lat = item.getLatitudeE6();
			int lon = item.getLongitudeE6();
			
			maxLat = Math.max(lat, maxLat);
			minLat = Math.min(lat, minLat);
			maxLon = Math.max(lon, maxLon);
			minLon = Math.min(lon, minLon);
		}
		
		double fitFactor = 1.0;
		mapView.getController().zoomToSpan((int) (Math.abs(maxLat - minLat) * fitFactor), (int) (Math.abs(maxLon - minLon) * fitFactor));
		mapView.getController().animateTo(new GeoPoint((maxLat + minLat) / 2, (maxLon + minLon) / 2));
		
		createCompass();
		update();
		
	}
	
//	private Handler handler = new Handler();
//
//	private Runnable refreshTask = new Runnable()
//	{
//	  public void run()
//	  {
//	    handler.removeCallbacks(this);
//
//	    mapView.postInvalidate();
//
//	    handler.postDelayed(this, MINIMUM_TIME_BETWEEN_UPDATES);
//
//	  }
//	};
	
	private void createCompass() {
		// fromlocation to tolocation
		ImageView image = (ImageView) findViewById(R.id.navi_arrow);
		Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.navi_arrow);
		Matrix mat = new Matrix();
		mat.postRotate((float) Compass.getDegrees(locFrom.getLatitude(), locFrom.getLongitude(), locTo.getLatitude(), locTo.getLongitude(), 0));
		Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), mat, true);
		image.setImageBitmap(bMapRotate);
	}
	
	private void update() {
		getCurrentLocation();
		// currentlocation to tolocation
		ImageView image2 = (ImageView) findViewById(R.id.navi_arrow2);
		Bitmap bMap2 = BitmapFactory.decodeResource(getResources(), R.drawable.navi_arrow2);
		Matrix mat2 = new Matrix();
		mat2.postRotate((float) Compass.getDegrees(currentLocation.getLatitude(), currentLocation.getLongitude(), locTo.getLatitude(), locTo.getLongitude(), 0));
		Bitmap bMapRotate2 = Bitmap.createBitmap(bMap2, 0, 0, bMap2.getWidth(), bMap2.getHeight(), mat2, true);
		image2.setImageBitmap(bMapRotate2);
		
		getCurrentLocation();
		GeoPoint currentPoint = new GeoPoint((int) (currentLocation.getLatitude() * 1E6), (int) (currentLocation.getLongitude() * 1E6));
		overlayitemC = new MyOverlayItem(currentPoint, "You are here", "Your position");
		
		geoItems.remove(2);
		geoItems.add(currentPoint);

		itemizedOverlayC.getmOverlays().remove(0);
		itemizedOverlayC.addOverlay(overlayitemC);

		mapOverlaysList.add(itemizedOverlayC);
		
		mapView.postInvalidate();
		//mapView.getController().scrollBy(1, 1);
		//mapView.getController().scrollBy(-1, -1);
	}
	
	protected void getCurrentLocation() {
		currentLocation = locationManager.getLastKnownLocation(PROVIDER);
		if(currentLocation == null) {
			currentLocation = locFrom;
		}			
	}
	
	private Route directions(final GeoPoint start, final GeoPoint dest) {
		Parser parser;
		String jsonURL = "http://maps.google.com/maps/api/directions/json?";
		final StringBuffer sBuf = new StringBuffer(jsonURL);
		sBuf.append("origin=");
		sBuf.append(start.getLatitudeE6() / 1E6);
		sBuf.append(',');
		sBuf.append(start.getLongitudeE6() / 1E6);
		sBuf.append("&destination=");
		sBuf.append(dest.getLatitudeE6() / 1E6);
		sBuf.append(',');
		sBuf.append(dest.getLongitudeE6() / 1E6);
		sBuf.append("&sensor=true&mode=walking");
		parser = new GoogleParser(sBuf.toString());
		Route r = parser.parse(start, dest);
		return r;
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private final LocationListener locationListener = new LocationListener() {
		
		public void onLocationChanged(Location location) {
			// String message = String.format("New Location \n Longitude: %1$s \n Latitude: %2$s", location.getLongitude(), location.getLatitude());
			// Toast.makeText(GoogleMapsActivity.this, message, Toast.LENGTH_LONG).show();
			updateWithNewLocation(location);
			System.out.println("location changed");
			update();
		}
		
		public void onStatusChanged(String s, int i, Bundle b) {
			//Toast.makeText(GoogleMapsActivity.this, "Provider status changed", Toast.LENGTH_LONG).show();
		}
		
		public void onProviderDisabled(String s) {
			Toast.makeText(GoogleMapsActivity.this, "Provider disabled by the user. GPS turned off", Toast.LENGTH_LONG).show();
			updateWithNewLocation(null);
		}
		
		public void onProviderEnabled(String s) {
			Toast.makeText(GoogleMapsActivity.this, "Provider enabled by the user. GPS turned on", Toast.LENGTH_LONG).show();
		}
		
	};
	
	private void updateWithNewLocation(Location location) {
		currentLocation = location;
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
