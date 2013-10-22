package com.tavant.droid.security.twitter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class TwitterHelper {

	public static final int TWT_LOGIN_SUCCESS = 5;
	public static final int TWT_START_FOLLOWERS=1;
	public static final int TWT_START_UPDATE_STS=2;
	public static final int TWT_SETTINGS = 7;
	public static final int TWT_ON_ERROR=4;
	public static final int TWT_TWEET_SUCCESS = 6;
	public static final int TWT_TWEET_FAIL = 7;

	private Context mContext = null;
	private TwitterCallBack mCallBack = null;
	public static String errorMsg = null;

	public TwitterHelper(Context context,TwitterCallBack callBack){
		mContext= context;
		mCallBack = callBack;
	}

	public void updateStatus(final String message){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					Twitterclient.getInstance(mContext).doTweet(message);
					mTwitterHandler.sendEmptyMessage(TWT_TWEET_SUCCESS);
				}catch (Exception e) {
					e.printStackTrace();
					errorMsg = "Unable to Update the twitter status";
					mTwitterHandler.sendEmptyMessage(TWT_ON_ERROR);
				}
			}
		}).start();
	}

	public void startTwitter(final int from){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					if(Twitterclient.getInstance(mContext).Connect2Twitter()){
						if(!Twitterclient.getInstance(mContext).isLogin()){
							Twitterclient.getInstance(mContext).startTwitterActivity(mContext,from, mTwitterHandler);
						}else{
							mTwitterHandler.sendEmptyMessage(from);
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
					errorMsg = "Unable to connect Twitter...";
					mTwitterHandler.sendEmptyMessage(TWT_ON_ERROR);
				}
			}
		}).start();
	}

	public Handler mTwitterHandler = new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what) {
			case TWT_START_UPDATE_STS:
				mCallBack.onSuccess(TWT_LOGIN_SUCCESS);
				break;
			case TWT_START_FOLLOWERS:
				mCallBack.onSuccess(TWT_LOGIN_SUCCESS);
				closeProgress();
				break;
			case TWT_TWEET_SUCCESS:
				mCallBack.onSuccess(TWT_TWEET_SUCCESS);
				closeProgress();
				break;
			case TWT_ON_ERROR:
				closeProgress();
				mCallBack.onError(errorMsg);
				break;
			case TWT_SETTINGS:
				closeProgress();
				mCallBack.onSuccess(TWT_SETTINGS);
				break;
			default:
				closeProgress();
				break;
			}
		}
	};

	public interface TwitterCallBack{
		public void onError(String message);
		public void onSuccess(int flag);
	}

	

	private void closeProgress(){
		/*
		if(mLoadingDialog.isShowing())
			mLoadingDialog.dismiss();

		mLoadingDialog = null;
		*/
	}

	public void removeCallbacks(){
		if(mTwitterHandler!=null){
			mTwitterHandler.removeMessages(TWT_LOGIN_SUCCESS);
			mTwitterHandler.removeMessages(TWT_ON_ERROR);
			mTwitterHandler.removeMessages(TWT_START_FOLLOWERS);
			mTwitterHandler.removeMessages(TWT_START_UPDATE_STS);
			mTwitterHandler.removeMessages(TWT_TWEET_SUCCESS);
			mTwitterHandler.removeMessages(TWT_TWEET_FAIL);
		}
	}
}
