package com.piq.erstieNavi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;

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
	Location locFrom = new Location(LocationManager.GPS_PROVIDER);
	Location locTo = new Location(LocationManager.GPS_PROVIDER);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		extras = getIntent().getExtras();
		if (extras != null) {
			locFrom.setLongitude(extras.getDouble("fromLocationLong"));
			locFrom.setLatitude(extras.getDouble("fromLocationLat"));
			locTo.setLongitude(extras.getDouble("toLocationLong"));
			locTo.setLatitude(extras.getDouble("toLocationLat"));
		}

		Building from = BuildingsManager.getInstance().getRequestedBuilding(locFrom);
		Building to = BuildingsManager.getInstance().getRequestedBuilding(locTo);

		setContentView(R.layout.googlemap);

		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawableF = this.getResources().getDrawable(R.drawable.arrow_from);
		MyItemizedOverlay itemizedoverlayF = new MyItemizedOverlay(drawableF, this);
		GeoPoint fromPoint = new GeoPoint((int) (locFrom.getLatitude() * 1E6), (int) (locFrom.getLongitude() * 1E6));
		MyOverlayItem overlayitemF = new MyOverlayItem(fromPoint, from.getName(), from.getAbbrev());

		Drawable drawableT = this.getResources().getDrawable(R.drawable.arrow_to);
		MyItemizedOverlay itemizedoverlayT = new MyItemizedOverlay(drawableT, this);
		GeoPoint toPoint = new GeoPoint((int) (locTo.getLatitude() * 1E6), (int) (locTo.getLongitude() * 1E6));
		MyOverlayItem overlayitemT = new MyOverlayItem(toPoint, to.getName(), to.getAbbrev());


		//TODO: draw the connecton between google´s startpoint and the given geoStart - maybe just draw it by hand in routeoverlay 
		
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
		
		
		ArrayList<GeoPoint> geoItems = new ArrayList<GeoPoint>();
		geoItems.add(fromPoint);
		geoItems.add(toPoint);

		itemizedoverlayF.addOverlay(overlayitemF);
		itemizedoverlayT.addOverlay(overlayitemT);

		mapOverlays.add(itemizedoverlayF);
		mapOverlays.add(itemizedoverlayT);

		//to fit the zoom of the map - see all geopoints of interets
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

		ImageView image = (ImageView) findViewById(R.id.navi_arrow);
		Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.navi_arrow);
		Matrix mat = new Matrix();
		mat.postRotate((float) Compass.getDegrees(locFrom.getLatitude(), locFrom.getLongitude(), locTo.getLatitude(), locTo.getLongitude(), 0));
		Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), mat, true);
		image.setImageBitmap(bMapRotate);
	}

	//not available anymore, cause google dont deliver results as kml-files anymore
//	  private void drawPath(GeoPoint src, GeoPoint dest, int color,
//		      MapView mMapView01) {
//
//		    // connect to map web service
//		    StringBuilder urlString = new StringBuilder();
//		    urlString.append("http://maps.google.com/maps?f=d&hl=en");
//		    urlString.append("&saddr=");//from
//		    urlString.append( Double.toString((double)src.getLatitudeE6()/1.0E6 ));
//		    urlString.append(",");
//		    urlString.append( Double.toString((double)src.getLongitudeE6()/1.0E6 ));
//		    urlString.append("&daddr=");//to
//		    urlString.append( Double.toString((double)dest.getLatitudeE6()/1.0E6 ));
//		    urlString.append(",");
//		    urlString.append( Double.toString((double)dest.getLongitudeE6()/1.0E6 ));
//		    urlString.append("&ie=UTF8&0&om=0&output=kml&dirflg=w");
//		    Log.d("xxx","URL="+urlString.toString());
//
//		    //System.out.println(urlString);
//		    // get the kml (XML) doc. And parse it to get the coordinates(direction route).
//		    Document doc = null;
//		    HttpURLConnection urlConnection= null;
//		    URL url = null;
//		    try
//		    {
//		      url = new URL(urlString.toString());
//		      urlConnection=(HttpURLConnection)url.openConnection();
//		      urlConnection.setRequestMethod("GET");
//		      urlConnection.setDoOutput(true);
//		      urlConnection.setDoInput(true);
//		      urlConnection.connect();
//
//		      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		      DocumentBuilder db = dbf.newDocumentBuilder();
//		      //doc = db.parse(url.openStream());
//		      doc = db.parse(urlConnection.getInputStream());
//
//		      if(doc.getElementsByTagName("GeometryCollection").getLength()>0)
//		      {
//		        //String path = doc.getElementsByTagName("GeometryCollection").item(0).getFirstChild().getFirstChild().getNodeName();
//		        String path = doc.getElementsByTagName("GeometryCollection").item(0).getFirstChild().getFirstChild().getFirstChild().getNodeValue() ;
//		        Log.d("xxx","path="+ path);
//		        String [] pairs = path.split(" ");
//		        String[] lngLat = pairs[0].split(","); // lngLat[0]=longitude lngLat[1]=latitude lngLat[2]=height
//		        // src
//		        GeoPoint startGP = new GeoPoint((int)(Double.parseDouble(lngLat[1])*1E6),(int)(Double.parseDouble(lngLat[0])*1E6));
//		        //mMapView01.getOverlays().add(overlayitem);
//		        GeoPoint gp1;
//		        GeoPoint gp2 = startGP;
//		        for(int i=1;i<pairs.length;i++) // the last one would be crash
//		        {
//		          lngLat = pairs[i].split(",");
//		          gp1 = gp2;
//		          // watch out! For GeoPoint, first:latitude, second:longitude
//		          gp2 = new GeoPoint((int)(Double.parseDouble(lngLat[1])*1E6),(int)(Double.parseDouble(lngLat[0])*1E6));
//		          mMapView01.getOverlays().add(new MyOverlay(gp1,gp2,2,color));
//		          Log.d("xxx","pair:" + pairs[i]);
//		        }
//		        //mMapView01.getOverlays().add(new MapRouteOverlay(dest,dest, 3)); // use the default color
//		      }
//		    }
//		    catch (MalformedURLException e)
//		    {
//		      e.printStackTrace();
//		    }
//		    catch (IOException e)
//		    {
//		      e.printStackTrace();
//		    }
//		    catch (ParserConfigurationException e)
//		    {
//		      e.printStackTrace();
//		    }
//		    catch (SAXException e)
//		    {
//		      e.printStackTrace();
//		    }
//
//
//		  }
//	  

	private Route directions(final GeoPoint start, final GeoPoint dest) {
	    Parser parser;
	    String jsonURL = "http://maps.google.com/maps/api/directions/json?";
	    final StringBuffer sBuf = new StringBuffer(jsonURL);
	    sBuf.append("origin=");
	    sBuf.append(start.getLatitudeE6()/1E6);
	    sBuf.append(',');
	    sBuf.append(start.getLongitudeE6()/1E6);
	    sBuf.append("&destination=");
	    sBuf.append(dest.getLatitudeE6()/1E6);
	    sBuf.append(',');
	    sBuf.append(dest.getLongitudeE6()/1E6);
	    sBuf.append("&sensor=true&mode=walking");
	    parser = new GoogleParser(sBuf.toString());
	    Route r =  parser.parse(start, dest);
	    return r;
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
