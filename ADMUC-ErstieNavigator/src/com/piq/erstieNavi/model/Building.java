package com.piq.erstieNavi.model;

public class Building implements Comparable<Building> {
	
	private double longitude;
	private double latitude;
	private String name;
	private String abbrev;
	
	public Building(String name, String abbrev, double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
		this.abbrev = abbrev;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAbbrev() {
		return abbrev;
	}
	
	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}
	
	@Override
	public String toString() {
		System.out.println("Name: " + name + " Abbrev: " + abbrev + " Lat: " + latitude + " Long: " + longitude);
		return "Name: " + name + " Abbrev: " + abbrev + " Lat: " + latitude + " Long: " + longitude;
	}
	
	public int compareTo(Building b) {
		
		return this.getAbbrev().compareTo(b.getAbbrev());
	}
	
}
