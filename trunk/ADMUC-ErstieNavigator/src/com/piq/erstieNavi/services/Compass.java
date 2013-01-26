package com.piq.erstieNavi.services;

import android.location.Location;
import android.location.LocationManager;

public class Compass {
	
	/**
	 * Params: lat1, long1 => Latitude and Longitude of current point lat2, long2 => Latitude and Longitude of target point headX => x-Value of built-in phone-compass Returns the degree of a direction from current point to target point
	 */
	public static double getDegrees(double lat1, double long1, double lat2, double long2, double headX) {
		Location locFrom = new Location(LocationManager.GPS_PROVIDER);
		Location locTo = new Location(LocationManager.GPS_PROVIDER);
		locFrom.setLongitude(long1);
		locFrom.setLatitude(lat1);
		locTo.setLongitude(long2);
		locTo.setLatitude(lat2);
		return locTo.bearingTo(locFrom);
	}
	
	// http://en.wikipedia.org/wiki/Haversine_formula ;
	public static double getHaverSineDistance(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 3958.75;
		// http://en.wikipedia.org/wiki/Haversine_formula
		
		// convert to radians
		lat1 = Math.toRadians(lat1);
		lng1 = Math.toRadians(lng1);
		lat2 = Math.toRadians(lat2);
		lng2 = Math.toRadians(lng2);
		
		double dlon = lng2 - lng1;
		double dlat = lat2 - lat1;
		
		double a = Math.pow((Math.sin(dlat / 2)), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
		
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double temp = earthRadius * c * 1.60934 * 1000; // converts miles into km
		temp = Math.round(temp);
		temp = temp / 1000;
		return temp;
	}
}
