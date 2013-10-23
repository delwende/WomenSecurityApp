package com.tavant.droid.security;

import java.io.IOException;

import org.apache.http.client.methods.HttpRequestBase;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.ads.AdRequest.Gender;
import com.google.ads.m;
import com.tavant.droid.security.data.BaseData;
import com.tavant.droid.security.http.HttpHandler;


/**
 * 
 * @author tavant
 *
 */
public abstract class BaseActivity extends ActionBarActivity {


	protected AsyncTask<Object, Void, BaseData> task;
	private final String TAG = "TAG";
	private AdView mAdView=null;

	protected abstract void onComplete(int reqCode, BaseData data);
	protected abstract void onError(int reqCode, int errorCode, String errorMessage);


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	
	protected void initAdd() {
		mAdView = (AdView)findViewById(R.id.ad);
		AdRequest adRequest = new AdRequest();
		adRequest.setGender(Gender.FEMALE);
	    adRequest.addKeyword("beauty items");
	    mAdView.loadAd(adRequest);
	}

	



	/**
	 * @param ReqCode
	 * @param request
	 * @param isshowdialog
	 * It can be multiple requests without dismissing the loading dialog
	 * It can be called for non loading screen api's like setvideostats
	 */
	public void onExecute(int ReqCode, HttpRequestBase request,final boolean isshowdialog) {
		//		if(task != null && !task.isCancelled()){
		//			task.cancel(true);
		//		}		

		task = new AsyncTask<Object, Void, BaseData>() {
			int code;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				//				if(isshowdialog)
				//					ShowProgress();
			}

			@Override
			protected BaseData doInBackground(Object... params) {
				code = (Integer) params[0];
				//if (NetworkConnection.getInstance(BaseActivity.this).isAlive()) {
				HttpRequestBase reqMethod = (HttpRequestBase) params[1];
				try {
					BaseData response = HttpHandler.getInstance().makeHttpRequest(code, reqMethod);
					Log.d(TAG, "Response : " + response);
					if (response != null) {
						response.reqCode = code;
						Log.d(TAG, "Response Code : " + response.reqCode);
						return response;
					}

				} catch (IOException e) {
					e.printStackTrace();
				} finally {

				}
				//}
				return null;
			}

			@Override
			protected void onPostExecute(BaseData data) {
				super.onPostExecute(data);
				postExecute(data,isshowdialog,code);
			}
		};
		task.execute(ReqCode, request);
	}

	private void postExecute(BaseData data,boolean isshowdialog,int code){
		if (data != null) {
			if (data.isSuccess)
				onComplete(code, data);
			else
				onError(code, data.error, data.serverMessages);
		}else{
			onError(code, 0, "Unknown error from the server");
		}
	}

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if(mAdView!=null)
    		mAdView.destroy();
    }


}
