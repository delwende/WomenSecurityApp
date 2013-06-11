package com.tavant.droid.womensecurity.service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.tavant.droid.womensecurity.HomeActivity;
import com.tavant.droid.womensecurity.http.HttpManager;
import com.tavant.droid.womensecurity.http.HttpRequestCreater;
import com.tavant.droid.womensecurity.utils.LocationData;
import com.tavant.droid.womensecurity.utils.WSConstants;

public class LocationAlarmService extends Service implements LocationListener{


	private LocationManager locationManager ;
	private String provider;
	private LocationData location=null; 
	private String userid=null;
	private SharedPreferences pref=null;

	@Override
	public void onCreate() {
		Log.i("TAG","onCreate");
	}

	@Override

	public IBinder onBind(Intent intent) {

		return null;

	}

	@Override

	public void onDestroy() {
		super.onDestroy();
		Log.i("TAG","onDestroy" );
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {			
		Log.i("TAG","calling laram agin");
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		pref=getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
		userid=pref.getString(WSConstants.PROPERTY_FB_ID,null);
		if(userid==null)
			return START_NOT_STICKY; // no registartion of alram
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		location=LocationData.getInstance();
		if(provider != null && !provider.equals("")){
			locationManager.requestLocationUpdates(provider, 20000, 1, this);
		}else{
			Toast.makeText(getBaseContext(), "No Provider found", Toast.LENGTH_SHORT).show();
		}
		return START_NOT_STICKY;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.i("TAG","got location");
		LocationData.getInstance().setLatitude(location.getLatitude());
		LocationData.getInstance().setLongitude(location.getLongitude());
		//locationManager.removeUpdates(this);
		updateLocationtoserver();
		LocationData.getInstance().setCurrentLocation(getLocationName(location.getLatitude(),location.getLongitude()));	
	}

	@Override
	public void onProviderDisabled(String provider) {
		Location location = locationManager.getLastKnownLocation(provider);
		if(location!=null){
			Log.i("TAG","got location on starting");
			LocationData.getInstance().setLatitude(location.getLatitude());
			LocationData.getInstance().setLongitude(location.getLongitude());
			updateLocationtoserver();
			LocationData.getInstance().setCurrentLocation(getLocationName(location.getLatitude(),location.getLongitude()));
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		//locationManager.removeUpdates(this);
		//updateLocationtoserver();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Location location = locationManager.getLastKnownLocation(provider);
		if(location!=null){
			Log.i("TAG","got location on starting");
			LocationData.getInstance().setLatitude(location.getLatitude());
			LocationData.getInstance().setLongitude(location.getLongitude());
			updateLocationtoserver();
			LocationData.getInstance().setCurrentLocation(getLocationName(location.getLatitude(),location.getLongitude()));
		}
	}

	private void updateLocationtoserver(){
		// TODO Auto-generated method stub
		Log.i("TAG","sending current location to server" + "latitude : " + location.getLatitude() + " longitude : " + location.getLongitude());
		if(userid==null)
			return;
		new AsyncTask <Void,Void,String>() {
			@Override 
			protected String doInBackground(Void... params) {
				String responseString="";
				HttpRequestBase post =  HttpRequestCreater.updateLocation(userid,location.getLatitude(), location.getLongitude(),0);
				try {
					HttpResponse  response = HttpManager.execute(post);
					if(response!=null){
						Log.i("TAG","update the location succesfully"+response.getStatusLine().getStatusCode());	
						responseString = response.toString();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return responseString;

			}
			protected void onPostExecute(String msg) {  
				Log.i("TAG","clearing location listenre");
				stopSelfResult(Activity.RESULT_OK);
				locationManager.removeUpdates(LocationAlarmService.this);
				//	isrunning=false;

			}
		}.execute(null, null, null);
	}

	private String getLocationName(double _lat, double _lon) {
		StringBuilder _homeAddress = null;
		try {
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(_lat, _lon, 1);
			System.out.println("Home Address :>> " + addresses);
			_homeAddress = new StringBuilder();
			Address address = null;
			for (int index = 0; index < addresses.size(); ++index) {
				address = addresses.get(index);
				_homeAddress
				.append("Name: " + address.getAddressLine(0) + "\n");
				_homeAddress.append("Sub-Admin Ares: "
						+ address.getSubAdminArea() + "\n");
				_homeAddress.append("Admin Area: " + address.getAdminArea()
						+ "\n");
				_homeAddress.append("Country: " + address.getCountryName()
						+ "\n");
				_homeAddress.append("Country Code: " + address.getCountryCode()
						+ "\n");
				_homeAddress
				.append("Latitude: " + address.getLatitude() + "\n");
				_homeAddress.append("Longitude: " + address.getLongitude()
						+ "\n\n");
				System.out.println("Home Address :>> " + _homeAddress);
			}
		} catch (Exception e) {

		}
		return (_homeAddress==null ? "":_homeAddress.toString());
	}
}


