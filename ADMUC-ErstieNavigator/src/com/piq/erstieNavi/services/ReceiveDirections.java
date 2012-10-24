package com.piq.erstieNavi.services;

import android.os.AsyncTask;

import com.google.android.maps.GeoPoint;
import com.piq.erstieNavi.googlemaps.GoogleParser;
import com.piq.erstieNavi.googlemaps.Parser;
import com.piq.erstieNavi.googlemaps.Route;

public class ReceiveDirections extends AsyncTask<GeoPoint, Integer, Route> {

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(Route result) {
       
    }

	@Override
	protected Route doInBackground(GeoPoint... params) {
		return directions(params[0], params[1]);
	}
	
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
}
