package com.tavant.droid.womensecurity.service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

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

import com.tavant.droid.womensecurity.data.BaseData;
import com.tavant.droid.womensecurity.data.CopsData;
import com.tavant.droid.womensecurity.http.HttpHandler;
import com.tavant.droid.womensecurity.http.HttpManager;
import com.tavant.droid.womensecurity.http.HttpRequestCreater;
import com.tavant.droid.womensecurity.utils.LocationData;
import com.tavant.droid.womensecurity.utils.WSConstants;

public class LocationAlarmService extends Service implements LocationListener {

	String TAG = "LocationAlarmService : ";
	LocationManager locationManager;
	String provider;
	double latitude, longitude;
	private SharedPreferences copPhonePreferences;
	private SharedPreferences.Editor copPrefsEditor;

	@Override
	public void onCreate() {

		Log.i(TAG, "onCreate");

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();

		provider = locationManager.getBestProvider(criteria, false);

		if (provider != null && !provider.equals("")) {
			Location location = locationManager.getLastKnownLocation(provider);
			locationManager.requestLocationUpdates(provider, 20000, 1, this);
			locationManager.removeUpdates(this);
			if (location != null)
				onLocationChanged(location);
			else
				Toast.makeText(getBaseContext(), "Location can't be retrieved",
						Toast.LENGTH_SHORT).show();

		} else {
			Toast.makeText(getBaseContext(), "No Provider found",
					Toast.LENGTH_SHORT).show();
		}

		copPhonePreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
		copPrefsEditor = copPhonePreferences.edit();

	}

	@Override
	public IBinder onBind(Intent intent) {

		Log.i(TAG, "onBind");

		return null;

	}

	@Override
	public void onDestroy() {

		super.onDestroy();

		Log.i(TAG, "onDestroy");

	}

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		Log.i(TAG, "sending current location to server" + "latitude : "
				+ latitude + " longitude : " + longitude);

		new AsyncTask<Void, Void, BaseData>(){

			@Override
			protected BaseData doInBackground(Void... params) {
				HttpRequestBase post = HttpRequestCreater.updateLocation(
						"100002058741716", latitude, longitude);
				try {
					HttpResponse response = HttpManager.execute(post);
					return HttpHandler.getInstance().makeHttpRequest(
							WSConstants.CODE_LOCATION_API, post);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}
			
			@Override
			protected void onPostExecute(BaseData msg) {
				// TODO Auto-generated method stub
				super.onPostExecute(msg);
				System.out.println("Response Message >> " + msg);
				if (msg != null && msg.isSuccess) {
					CopsData copNumber = (CopsData)msg;
					/*System.out.println(" Cops phone number :>"
							+ (CopsData)msg.phoneNumber);*/
					copPrefsEditor.putString("COP_NUMBER", copNumber.phoneNumber);
					copPrefsEditor.commit();
				}
			}
			
		}.execute(null,null,null);
		/*new AsyncTask<Void, Void, BaseData>() {
			@Override
			protected BaseData doInBackground(Void... params) {
				String responseString = "";
				

			}

			protected void onPostExecute(CopsData msg) {
				
			}
		}.execute(null, null, null);
*/
	}

	@Override
	public boolean onUnbind(Intent intent) {

		Log.i(TAG, " onUnbind");

		return super.onUnbind(intent);

	}

	@Override
	public void onLocationChanged(Location location) {
		// get current location and send to server.
		longitude = location.getLongitude();
		latitude = location.getLatitude();
		LocationData.getInstance().setCurrentLocation(
				getLocationName(longitude, latitude));
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

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
		return _homeAddress.toString();
	}
}