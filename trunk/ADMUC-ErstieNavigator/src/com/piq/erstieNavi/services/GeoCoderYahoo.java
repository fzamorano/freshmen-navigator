package com.piq.erstieNavi.services;

import com.piq.erstieNavi.model.GeoCodeResult;
import com.piq.erstieNavi.util.XmlParser;

public class GeoCoderYahoo {
	
	// http://developer.yahoo.com/geo/placefinder/
	
	private static final String APPLICATION_ID = "QsBxE37g";
	private static final String YAHOO_API_BASE_URL = "http://where.yahooapis.com/geocode?q=%1$s,+%2$s&gflags=R&appid=" + APPLICATION_ID;
	private HttpRetriever httpRetriever = new HttpRetriever();
	private XmlParser xmlParser = new XmlParser();
	
	public GeoCodeResult reverseGeoCode(double latitude, double longitude) {
		
		String url = String.format(YAHOO_API_BASE_URL, String.valueOf(latitude), String.valueOf(longitude));
		String response = httpRetriever.retrieve(url);
		return xmlParser.parseXmlResponse(response);
	}
}
