package com.tavant.droid.security.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tavant.droid.security.data.BaseData;
import com.tavant.droid.security.data.CopsData;
import com.tavant.droid.security.http.HttpHandler;
import com.tavant.droid.security.http.HttpRequestCreater;
import com.tavant.droid.security.prefs.CommonPreferences;
import com.tavant.droid.security.utils.LocationData;
import com.tavant.droid.security.utils.WSConstants;

public class LocationAlarmService extends Service implements LocationListener{


	private LocationManager locationManager ;
	private String provider;
	private LocationData location=null; 
	private String userid=null;
	//private SharedPreferences pref=null;
	private Timer timer=null;
	//private Editor edit=null;
	private static final String REVRESE_LOCATION_API
	="http://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&sensor=true";
	
	private CommonPreferences prefrences;

	@Override
	public void onCreate() {
		timer=new Timer();
		Log.d("TAG","onCreate");
	}

	@Override

	public IBinder onBind(Intent intent) {

		return null;

	}

	@Override

	public void onDestroy() {
		super.onDestroy();
		Log.d("TAG","onDestroy" );
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {			
		Log.d("TAG","calling laram agin");
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		prefrences=CommonPreferences.getInstance();
		userid=prefrences.getFbId();
		if(userid==null)
			return START_NOT_STICKY; // no registartion of alram
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Log.d("TAG","best provider is"+provider);
		if(provider.equals(LocationManager.NETWORK_PROVIDER.toString())&&!locationManager.isProviderEnabled(provider)&&
				locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			provider= LocationManager.GPS_PROVIDER;
			Log.d("TAG","coming here1");
			
		}else if(provider.equals(LocationManager.GPS_PROVIDER)&&!locationManager.isProviderEnabled(provider)&&
				locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			provider= LocationManager.NETWORK_PROVIDER;
			Log.d("TAG","coming here2");
		}else{
			provider=LocationManager.NETWORK_PROVIDER;   // if both are
			Log.d("TAG","coming here3");
		}


		location=LocationData.getInstance();
		if(provider != null && !provider.equals("")){
			Log.d("TAG","final provider"+provider);
			timer.schedule(timertask, 10*1000);
			locationManager.requestLocationUpdates(provider, 20000, 1, this);
		}else{
			Toast.makeText(getBaseContext(), "No Provider found", Toast.LENGTH_SHORT).show();
		}
		return START_NOT_STICKY;
	}



	private TimerTask timertask=new TimerTask() {
		@Override
		public void run() {
			timertask.cancel();
			timer.cancel();
			Location location = locationManager.getLastKnownLocation(provider);
			if(location!=null){
				Log.d("TAG","got last known location, after removing the timer");
				LocationData.getInstance().setLatitude(location.getLatitude());
				LocationData.getInstance().setLongitude(location.getLongitude());
				//LocationData.getInstance().setCurrentLocation(getLocationName(location.getLatitude(),location.getLongitude()));
				getLocationinString(location.getLatitude(),location.getLongitude());
				handler.sendEmptyMessage(0);
			}else{
				Log.d("TAG","no location at all");
				clear();
			}
		}
	};


	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			updateLocationtoserver();
		}
	};




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
		Log.d("TAG","got location");
		timertask.cancel();
		LocationData.getInstance().setLatitude(location.getLatitude());
		LocationData.getInstance().setLongitude(location.getLongitude());
		//locationManager.removeUpdates(this);
		updateLocationtoserver();
		//LocationData.getInstance().setCurrentLocation(getLocationName(location.getLatitude(),location.getLongitude()));	
		getLocationinString(location.getLatitude(),location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d("TAG","provider disabled");
		timertask.cancel();
		Location location = locationManager.getLastKnownLocation(provider);
		if(location!=null){
			Log.d("TAG","provider disabled, then also got location");
			LocationData.getInstance().setLatitude(location.getLatitude());
			LocationData.getInstance().setLongitude(location.getLongitude());
			updateLocationtoserver();
			//LocationData.getInstance().setCurrentLocation(getLocationName(location.getLatitude(),location.getLongitude()));
			getLocationinString(location.getLatitude(),location.getLongitude());
		}else{
			timer.cancel();
			clear();
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d("TAG","coming here5, can fetch location in next attemp");
		timertask.cancel();
		timer.cancel();
		clear();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("TAG","coming here6");
		timertask.cancel();
		Location location = locationManager.getLastKnownLocation(provider);
		if(location!=null){
			Log.d("TAG","got location on starting");
			LocationData.getInstance().setLatitude(location.getLatitude());
			LocationData.getInstance().setLongitude(location.getLongitude());
			updateLocationtoserver();
			//LocationData.getInstance().setCurrentLocation(getLocationName(location.getLatitude(),location.getLongitude()));
			getLocationinString(location.getLatitude(),location.getLongitude());
		}else{
			timer.cancel();
			clear();
		}
	}

	private void clear(){
		locationManager.removeUpdates(this);
		stopSelfResult(Activity.RESULT_OK);
	}

	private void updateLocationtoserver(){
		// TODO Auto-generated method stub
		prefrences.setLatitude(""+location.getLatitude());
		prefrences.setLongtitude(""+location.getLongitude());
		Log.d("TAG","sending current location to server" + "latitude : " + location.getLatitude() + " longitude : " + location.getLongitude());
		if(userid==null)
			return;
		new AsyncTask <Void,Void,BaseData>() {
			@Override 
			protected BaseData doInBackground(Void... params) {
				BaseData data=null;
				HttpRequestBase post =  HttpRequestCreater.updateLocation(userid,location.getLatitude(), location.getLongitude(),0);
				try {
					data  = HttpHandler.getInstance().makeHttpRequest(WSConstants.CODE_LOCATION_API, post);
					return data;
				} catch (IOException e) {
					e.printStackTrace();
				}
				return data;

			}
			@Override 
			protected void onPostExecute(BaseData base) {  
				Log.d("TAG","clearing location listenre");
				CopsData data=(CopsData)base;
				if(data!=null&&data.phoneNumber!=null) {
					Log.d("TAG","police number"+data.phoneNumber);
					prefrences.setVolunteerNumber(data.phoneNumber);
				}	
				stopSelfResult(Activity.RESULT_OK);
				locationManager.removeUpdates(LocationAlarmService.this);
				timertask.cancel();
				timer.cancel();
				//	isrunning=false;

			}
		}.execute(null, null, null);
	}

	@SuppressWarnings("unused")
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
			Log.d("TAG","erroe in getting the locations.....for sms....");
		}
		return (_homeAddress==null ? "":_homeAddress.toString());
	}


	private void getLocationinString(final double _lat, final double _lon){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					String url=String.format(REVRESE_LOCATION_API,""+_lat,""+_lon); 
					DefaultHttpClient  client=new DefaultHttpClient();
					HttpRequestBase get=new HttpGet(url);
					HttpResponse res=client.execute(get);
					HttpEntity entity=res.getEntity();
					JSONObject resJson=new JSONObject(read(entity.getContent()));
					JSONArray jsonarray=  resJson.getJSONArray("results");
					JSONObject firstobject=(JSONObject) jsonarray.get(0);	
					Log.d("TAG","userlocationforsms"+firstobject.get("formatted_address").toString());
					prefrences.setUserlocation(firstobject.get("formatted_address").toString());
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}



	private static String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		return sb.toString();
	}
}


