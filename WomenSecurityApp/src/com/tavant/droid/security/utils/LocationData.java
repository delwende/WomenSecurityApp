package com.tavant.droid.security.utils;

public class LocationData {
	private static LocationData locationData;
	//private String currentLocation;
	private double latitude=0.0; 
	private double longitude=0.0;
	

	private LocationData() {

	}

	public static LocationData getInstance() {
		if (locationData == null) {
			locationData = new LocationData();
		}
		return locationData;
	}

	//	public String getCurrentLocation() {
	//		return currentLocation;
	//	}
	//
	//	public void setCurrentLocation(String currentLocation) {
	//		this.currentLocation = currentLocation;
	//	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}	
}
