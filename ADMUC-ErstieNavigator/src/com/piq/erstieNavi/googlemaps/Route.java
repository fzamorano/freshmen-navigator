package com.piq.erstieNavi.googlemaps;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;

public class Route {
	private String name;
	private final List<GeoPoint> points;
	private List<Segment> segments;
	private String copyright;
	private String warning;
	private String country;
	private int length;
	private String polyline;
	
	public Route() {
		points = new ArrayList<GeoPoint>();
		segments = new ArrayList<Segment>();
	}
	
	public void addPoint(final GeoPoint p) {
		points.add(p);
	}
	
	public void addPoints(final List<GeoPoint> points) {
		this.points.addAll(points);
	}
	
	public List<GeoPoint> getPoints() {
		return points;
	}
	
	public void addSegment(final Segment s) {
		segments.add(s);
	}
	
	public List<Segment> getSegments() {
		return segments;
	}
	
	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param copyright
	 *            the copyright to set
	 */
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	
	/**
	 * @return the copyright
	 */
	public String getCopyright() {
		return copyright;
	}
	
	/**
	 * @param warning
	 *            the warning to set
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}
	
	/**
	 * @return the warning
	 */
	public String getWarning() {
		return warning;
	}
	
	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}
	
	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * @param polyline
	 *            the polyline to set
	 */
	public void setPolyline(String polyline) {
		this.polyline = polyline;
	}
	
	/**
	 * @return the polyline
	 */
	public String getPolyline() {
		return polyline;
	}
	
}