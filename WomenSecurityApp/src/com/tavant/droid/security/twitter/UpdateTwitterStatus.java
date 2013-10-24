
package com.tavant.droid.security.twitter;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.tavant.droid.security.R;
import com.tavant.droid.security.prefs.CommonPreferences;

/**
 * Function to update status
 * */
public class UpdateTwitterStatus extends AsyncTask<String, String, String> {

	
	 private ProgressDialog pDialog=null;
	 private Context mctx=null;
	
	 
	 public UpdateTwitterStatus(Context ctx){
		 mctx=ctx;
	 }
	 
	 
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(mctx);
		pDialog.setMessage("Sharing  to Twitter...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		if(!((Activity)mctx).isFinishing()){
			pDialog.show();
		}
	}

	/**
	 * getting Places JSON
	 * */
	protected String doInBackground(String... args) {
		String status = args[0];
		try {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(mctx.getString(R.string.twitter_api_key));
			builder.setOAuthConsumerSecret(mctx.getString(R.string.twitter_api_secret));
			String access_token = CommonPreferences.getInstance().getAcessToken();
			String access_token_secret =CommonPreferences.getInstance().getAccessTokenSecret();
			AccessToken accessToken = new AccessToken(access_token, access_token_secret);
			Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

			// Update status
			twitter4j.Status response = twitter.updateStatus(status);

			Log.d("Status", "> " + response.getText());
		} catch (TwitterException e) {
			// Error in updating status
			Log.d("Twitter Update Error", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	protected void onPostExecute(String file_url) {
		if(!((Activity)mctx).isFinishing()&&pDialog.isShowing()){
			pDialog.dismiss();
		}
	}

}