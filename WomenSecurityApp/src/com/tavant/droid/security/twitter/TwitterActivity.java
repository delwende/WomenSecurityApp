package com.tavant.droid.security.twitter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tavant.droid.security.BaseActivity;
import com.tavant.droid.security.R;
import com.tavant.droid.security.data.BaseData;

public class TwitterActivity extends BaseActivity{
	public WebView web;
	public String twitterURL = "";
	public String urllink = null;
	public static String twitterCode="";
	public static final String FROM = "FROM";
	private boolean isLogin = false;
	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.twitterwebview);
			final int from = getIntent().getIntExtra(FROM, 0);
			web = (WebView) findViewById(R.id.webView);
			web.setWebViewClient(new WebViewClient(){
				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					try{/*
						web.loadUrl("javascript:(function(){ var str; " +
								"str = document.getElementById('oauth_pin').firstChild.nodeValue;" +
								//"if(str != null)" +
								"alert(str);" +
						"})()");
						*/
						/*
						web.loadUrl("javascript:(function(){ var str; " +
								"str = document.getElementById('oauth_pin')" +
								//"if(str != null)" +
								"alert(str);" +
						"})()");
						*/
						web.loadUrl("javascript:alert('Hello World!')");
						
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onPageStarted(WebView view, String url,Bitmap favicon) {
					// TODO Auto-generated method stub
					super.onPageStarted(view, url, favicon);
					Log.i("TEST","onPageStarted");
				}
			});
			
			web.getSettings().setJavaScriptEnabled(true);
			web.getSettings().setPluginState(PluginState.ON);
			web.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
			web.getSettings().setSupportMultipleWindows(false);
			web.getSettings().setSupportZoom(false);
			web.canGoBackOrForward(RESULT_CANCELED);
			web.setVerticalScrollBarEnabled(true);
			web.setHorizontalScrollBarEnabled(true);
			web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			web.clearHistory();
			web.clearFormData();
			web.clearCache(true);
			web.setWebChromeClient(new WebChromeClient(){
				@Override
				public boolean onJsAlert(WebView view, final String url,final String message, JsResult result) {
					if(message!=null){
//						new Thread(new Runnable() {
//
//							@Override
//							public void run() {
								Log.i("TAG","message"+message+"url:::"+url);
								Twitterclient.getInstance(TwitterActivity.this).setOAuthAccessToken(message);
								Twitterclient.getInstance(TwitterActivity.this).mHandler.sendEmptyMessage(from);
								isLogin = true;
								
							}
						//}).start();
						
					//}
					else{
						TwitterHelper.errorMsg = "Faild to login...";
						Twitterclient.getInstance(TwitterActivity.this).mHandler.sendEmptyMessage(TwitterHelper.TWT_ON_ERROR);
					}
					//TwitterActivity.this.finish();
					return true;
				}
			});
			web.setSaveEnabled(false);
			web.setDrawingCacheEnabled(false);
			web.setDuplicateParentStateEnabled(false);
			twitterURL = getIntent().getStringExtra("TwitterURL");
			web.loadUrl(twitterURL);
			
		}catch (Exception e) {
			TwitterHelper.errorMsg = "Unable to load the Twitter";
			Twitterclient.getInstance(TwitterActivity.this).mHandler.sendEmptyMessage(TwitterHelper.TWT_ON_ERROR);
			finish();
		}
	}




	@Override
	protected void onDestroy() {
		web.clearHistory();
		web.clearCache(true);
		web.destroy();
		super.onDestroy();
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK && !isLogin){
			TwitterHelper.errorMsg = "You have cancelled the Twiiter";
			Twitterclient.getInstance(TwitterActivity.this).mHandler.sendEmptyMessage(TwitterHelper.TWT_ON_ERROR);
		} else if(keyCode==KeyEvent.KEYCODE_BACK && !isLogin){
			
		}
		return super.onKeyDown(keyCode, event);
	}




	@Override
	protected void onComplete(int reqCode, BaseData data) {
		// TODO Auto-generated method stub
		
	}




	@Override
	protected void onError(int reqCode, int errorCode, String errorMessage) {
		// TODO Auto-generated method stub
		
	}
	
}

