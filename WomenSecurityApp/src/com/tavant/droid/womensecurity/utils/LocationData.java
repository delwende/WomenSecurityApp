package com.tavant.droid.womensecurity.utils;

public class LocationData {
	private static LocationData locationData;
	String currentLocation;

	private LocationData() {

	}

	public static LocationData getInstance() {
		if (locationData == null) {
			locationData = new LocationData();
		}
		return locationData;
	}

	public String getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}

}
