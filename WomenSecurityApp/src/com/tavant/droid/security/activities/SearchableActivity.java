package com.tavant.droid.security.activities;


import com.tavant.droid.security.R;
import com.tavant.droid.security.database.ContentDescriptor;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;

public class SearchableActivity extends ActionBarActivity {
	private ProgressBar progress=null;
	private ListView listview=null;
	private String searchquery="";
	private Cursor mcursor=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendslist);
		progress=(ProgressBar)findViewById(R.id.loading);
		listview=(ListView)findViewById(R.id.friendslist);
		handleintent(getIntent());
	}
	@Override
	protected void onNewIntent(Intent intent) {
	    setIntent(intent);
	    handleintent(intent);
	}

	private void handleintent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			searchquery  = intent.getStringExtra(SearchManager.QUERY);
			doMySearch();
		 }	
	}
	
	private void doMySearch(){
		mcursor=getContentResolver().query(ContentDescriptor.WSFacebook.CONTENT_URI, null, ContentDescriptor.WSFacebook.Cols.FBNAME+" like ? ", new String[]{"%"+searchquery+"%"}, null);
	    if(mcursor!=null){
	    	Log.i("TAG","mcursor"+mcursor.getCount());
	    }
	}

	

}
