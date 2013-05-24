package com.tavant.droid.womensecurity;

import java.io.IOException;

import org.apache.http.client.methods.HttpRequestBase;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.tavant.droid.womensecurity.data.BaseData;
import com.tavant.droid.womensecurity.http.HttpHandler;


/**
 * 
 * @author tavant
 *
 */
public abstract class BaseActivity extends Activity {
	
	
	protected AsyncTask<Object, Void, BaseData> task;
	private final String TAG = "BaseActivity";
	
	protected abstract void onComplete(int reqCode, BaseData data);
	protected abstract void onError(int reqCode, int errorCode, String errorMessage);
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	}
	
	
	
	
	/**
	 * @param ReqCode
	 * @param request
	 * @param isshowdialog
	 * It can be multiple requests without dismissing the loading dialog ex : followuser api needs GET_MEMBER_INFO api to call OR
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
	}



}
