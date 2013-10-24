package com.tavant.droid.security.twitter;

import com.tavant.droid.security.R;
import com.tavant.droid.security.prefs.CommonPreferences;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

/*
 * Steps:
 * 1. Get OAuth Request Token, store the oauth_verifier
 * 2. Using the request token get the get OAuth Access Token
 * 3. Save Access token in preferences
 */
public class TwitterAuthenticationActivity extends Activity{
	public static final String TAG = "TwitterAuthenticationActivity";
	public static final String INTENT_EXTRA_ACTIVITY = "intent_activity";
	public static final String OAUTH_VERIFIER = "oauth_verifier";
	public static final String DENIED = "?denied=";
	public static final String TWITTER_CALLBACK_URL = "oauth://t4jgladio";

	private Twitter mTwitter;
	private RequestToken mReqToken;
	private WebView webview=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.twitterwebview);
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(getString(R.string.twitter_api_key));
		builder.setOAuthConsumerSecret(getString(R.string.twitter_api_secret));
		Configuration configuration = builder.build();
		TwitterFactory factory = new TwitterFactory(configuration);
		mTwitter = factory.getInstance();
		TwitterAsyncRequestToken task = new TwitterAsyncRequestToken();
		task.execute();
	}

	void showTwitterLoginPage(){
		webview=(WebView)findViewById(R.id.webView);
		String url=mReqToken.getAuthenticationURL();
		webview.loadUrl(url);
	}

	void showFailureToast(){
		Toast.makeText(this, "Twitter Login error, please try again later", Toast.LENGTH_SHORT).show();
		setResult(0);
		finish();
	}

	//Call Back from Twitter, handle all twitter responses
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		if( uri.toString().contains( DENIED) ) {
			setResult(0);
			finish();
		}else if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) { // If the user has just logged in
			String oauthVerifier = uri.getQueryParameter(OAUTH_VERIFIER);
			TwitterAsyncAccessToken task = new TwitterAsyncAccessToken();
			task.execute(oauthVerifier);
		}
	}



	//Async Tasks
	public class TwitterAsyncRequestToken extends AsyncTask<Void, Void, String> {
		ProgressDialog progressDialog;
		@Override
		protected void onPreExecute()
		{
			progressDialog= ProgressDialog.show(TwitterAuthenticationActivity.this, "Please wait","Logging into Twitter...", true);

			//do initialization of required objects objects here                
		};      
		@Override
		protected String doInBackground(Void... params)
		{   
			try {
				mReqToken = mTwitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
			} catch (TwitterException e) {
				e.printStackTrace();
				return "ERROR";

			}

			//do loading operation here  
			return "SUCCESS";
		}       
		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);

			progressDialog.dismiss();
			if(result.equals("ERROR")){
				showFailureToast();
			}else{
				showTwitterLoginPage();
			}


		};
	}

	public class TwitterAsyncAccessToken extends AsyncTask<String, Void, String> {
		ProgressDialog progressDialog;
		@Override
		protected void onPreExecute()
		{
			progressDialog= ProgressDialog.show(TwitterAuthenticationActivity.this, "Please wait","Logging in...", true);

			//do initialization of required objects objects here                
		};      
		@Override
		protected String doInBackground(String... params)
		{   
			try {
				AccessToken accessToken = mTwitter.getOAuthAccessToken(mReqToken, params[0]);
				String token = accessToken.getToken();
				String secret = accessToken.getTokenSecret();
				CommonPreferences.getInstance().setAcessToken(token);
				CommonPreferences.getInstance().setAccessTokenSecret(secret);
			}catch(Exception e){
				e.printStackTrace();
				return "ERROR";
			}
			return "SUCCESS";
		}       
		@Override
		protected void onPostExecute(String result)
		{
					
			super.onPostExecute(result);
			progressDialog.dismiss();
			if (result.equals("ERROR")) {
				showFailureToast();
			}
			else {
				setResult(RESULT_OK);
				finish();
			}
          

		};
	}
}
