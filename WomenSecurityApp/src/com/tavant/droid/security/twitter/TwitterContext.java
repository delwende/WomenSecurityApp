//package com.tavant.droid.security.twitter;
//
//import twitter4j.auth.AccessToken;
//import twitter4j.conf.Configuration;
//import twitter4j.conf.ConfigurationBuilder;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//
///*
// * Singleton Twitter Context
// */
//public enum TwitterContext {
//
//	INSTANCE;
//	
//	
//	
//	static final String CALLBACK_URL = "twitter_callback";
//	private static final String ACCESS_TOKEN = "TWITTER_ACCESS_TOKEN";
//	private static final String ACCESS_TOKEN_SECRET = "TWITTER_ACCESS_TOKEN_SECRET";
//	public static final String OAUTH_VERIFIER = "oauth_verifier";
//	public static final String DENIED = "?denied=";
//	
//	
//	private SharedPreferences preferences = mApplicationContext.getSharedPreferences("twitterPrefs", Context.MODE_PRIVATE);
//	
//	public void saveAccessToken(AccessToken accessToken) {
//		String token = accessToken.getToken();
//		String secret = accessToken.getTokenSecret();
//		Editor editor = preferences.edit();
//		editor.putString(ACCESS_TOKEN, token);
//		editor.putString(ACCESS_TOKEN_SECRET, secret);
//		editor.commit();
//	}
//	
//	public boolean isTwitterAuthenticated(){
//		String token = preferences.getString(ACCESS_TOKEN, null);
//		String secret = preferences.getString(ACCESS_TOKEN_SECRET, null);
//		return (!StringUtil.isEmpty(token)) && (!StringUtil.isEmpty(secret));
//	}
//	
//	public void clear(){
//		Editor editor = preferences.edit();
//		editor.putString(ACCESS_TOKEN, null);
//		editor.putString(ACCESS_TOKEN_SECRET, null);
//		editor.commit();
//	}
//	
//	public Configuration getConfiguration(){
//		if(!isTwitterAuthenticated())
//			return null;
//		
//		String token = preferences.getString(ACCESS_TOKEN, null);
//		String secret = preferences.getString(ACCESS_TOKEN_SECRET, null);
//
//		ConfigurationBuilder confbuilder = new ConfigurationBuilder();
//		Configuration conf = confbuilder
//							.setOAuthConsumerKey(CONSUMER_KEY)
//							.setOAuthConsumerSecret(CONSUMER_SECRET)
//							.setOAuthAccessToken(token)
//							.setOAuthAccessTokenSecret(secret)
//							.build();
//		
//		return conf;
//	}
//}
