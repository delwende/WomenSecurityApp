package com.tavant.droid.security.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.Log;

/**
 * Twitter utility Class.
 *
 */
public class Twitterclient {

	private static final String TWITTER_CONSUMER_KEY = "QvvNkdWf0j0Ow4HOjNKntw";
	private static final String TWITTER_SECRET_KEY = "bpn07EYV3hcH3IvQXm4H0pGMfLf4FUW18t0epl0rU0";
	private static final String TWITTER_PREF = "twitterInfo";
	private static final String TWITTER_AUTH ="AUTH";
	private static final String TWITTER_SECRET ="SECRET";


	static final String TWITTER_CALLBACK_URL = "oauth://t4gladio";

	// Twitter oauth urls
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

	private static TwitterFactory factory1 =null;
	private static Twitter twitter = null;
	private static RequestToken requestToken = null;
	private static String URL = null;
	private static AccessToken accessToken;
	private static Twitterclient mGateWay=null;

	private SharedPreferences mPreferences = null;
	private Editor mEditor;



	private static boolean isLogin = false;
	private boolean isConnected = false;
	public long nextPage =  -1;
	public long previousPage =  -1;
	public String Username = "Unable to get user name";


	/**
	 * Constructor.
	 */
	private  Twitterclient(Context context){
		mPreferences=context.getSharedPreferences(TWITTER_PREF,Context.MODE_PRIVATE);
		mEditor = mPreferences.edit();
		String token = mPreferences.getString(TWITTER_AUTH, null);
		String tokenSecret = mPreferences.getString(TWITTER_SECRET, null);
		if(token==null && tokenSecret==null){
			isLogin = false;
		}else{
			accessToken=new AccessToken(token, tokenSecret);
			isLogin=true;
		}
	}

	/**
	 * Method which will return the Single object.
	 * @return TwitterGateWay.
	 */
	public static Twitterclient getInstance(Context aContext){
		if(mGateWay == null){
			mGateWay = new Twitterclient(aContext);
		}
		return mGateWay;
	}

	/**
	 * method to create the connection with server.
	 * @return true if connected.
	 * @throws TwitterException
	 */
	public boolean Connect2Twitter() throws TwitterException{
		if(isConnected){
			return isConnected;
		}else{
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(TWITTER_SECRET_KEY);
			Configuration configuration = builder.build();

			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			requestToken = twitter.getOAuthRequestToken();

			URL	=	requestToken.getAuthorizationURL();
			Log.i("TAG","URL"+URL);
			if(isLogin){
				setAccessToekn(accessToken);
			}
			isConnected = true;
			return true;
		}
	}


	/**
	 * Method will notify whether the use is login or not.
	 * if user is not login have to call getTwitterUrl() and get Authentication token.
	 * onActivityResult call method setOAuthAccessToken(setOAuthAccessToken)
	 * @return status true / false;
	 */
	public boolean isLogin(){
		return isLogin;
	}

	/**
	 * Method to set the Twitter Auth Token.Call this on Activity result.
	 * @param twitterAuthCode the string returned from the web page.
	 */
	public void setOAuthAccessToken(String twitterAuthCode){
		try{
			if(accessToken==null){
				accessToken = twitter.getOAuthAccessToken(requestToken,"23947784");	
				mEditor.putString(TWITTER_AUTH, accessToken.getToken()).commit();
				mEditor.putString(TWITTER_SECRET, accessToken.getTokenSecret()).commit();
				setAccessToekn(accessToken);
				isLogin=true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * local method to add Access tokens.
	 * @param access
	 * @throws TwitterException 
	 * @throws IllegalStateException 
	 */
	private void setAccessToekn(AccessToken access) throws IllegalStateException, TwitterException{
		twitter.setOAuthAccessToken(access);
		Username = twitter.getScreenName();
	}



	/**
	 * method to post on the twitter.
	 * @param message message to be posted.
	 * @throws TwitterException 
	 */
	public boolean doTweet(final String message) throws TwitterException{
		twitter.updateStatus(message);
		return true;

	}

	/**
	 * clean all object.
	 */
	private void Clean(){

		factory1 = null;
		twitter = null;
		requestToken = null;
		URL = null;
		accessToken=null;
		mPreferences = null;
		mEditor=null;
		isLogin = false;
		isConnected = false;
		mGateWay = null;
	}


	/**
	 * Logout the Twitter.
	 */
	public void clearTwitter(){
		if(mEditor!=null){
			mEditor.putString(TWITTER_AUTH, null).commit();
			mEditor.putString(TWITTER_SECRET, null).commit();
		}
		Clean();
	}

	/**
	 * method to start the login page of Twitter.
	 * if success then Result code will be TWITTER_RESULT;
	 * @param RequestCode
	 */
	public void startTwitterActivity(Context context,int from,Handler handler){
		mHandler = handler;
		Intent mIntent = new Intent(context,TwitterActivity.class);
		mIntent.putExtra("TwitterURL",URL);
		mIntent.putExtra(TwitterActivity.FROM, from);
		context.startActivity(mIntent);
	}
	
	
	

	/**
	 * Send direct message to the Followers.
	 * @param uid I which whom to send.
	 * @param message message to be send.
	 * @throws TwitterException
	 */
	public void sendDirectMessage(long uid,String message) throws TwitterException{
		twitter.sendDirectMessage(uid, message);
	}

	public Handler mHandler = null;
}