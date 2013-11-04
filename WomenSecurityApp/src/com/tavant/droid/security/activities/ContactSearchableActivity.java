package com.tavant.droid.security.activities;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.tavant.droid.security.R;
import com.tavant.droid.security.adapters.ContactListAdapter;
import com.tavant.droid.security.adapters.FacebookFriendsAdapter;
import com.tavant.droid.security.database.ContentDescriptor;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AlphabetIndexer;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ContactSearchableActivity extends ActionBarActivity implements  LoaderCallbacks<Cursor> {
	
	private ProgressBar progress=null;
	private ListView listview=null;
	private String searchquery="";
	private Cursor mcursor=null;
	private Uri muri=null;
	private static final int LOADER_ID = 0x05;
	private ContactListAdapter adapter=null;
	private TextView mtext=null;
	private AsyncTask<Void, Void, Integer>mTask=null;
	private AlphabetIndexer indexer;
	private int[] usedSectionNumbers;
	private Map<Integer, Integer> sectionToOffset;
	private Map<Integer, Integer> sectionToPosition;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendslist);
		progress=(ProgressBar)findViewById(R.id.loading);
		listview=(ListView)findViewById(R.id.friendslist);
		mtext=(TextView)findViewById(R.id.load_status);
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
			//doMySearch();
		}else if(Intent.ACTION_VIEW.equals(intent.getAction())){
			muri=intent.getData();
		}
		getSupportLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
			 return new CursorLoader(this, ContentDescriptor.WSContact.CONTENT_URI, null, ContentDescriptor.WSContact.Cols.NAME+" like ? ", new String[]{searchquery+"%"}, null);
		}else if(Intent.ACTION_VIEW.equals(getIntent().getAction())){
           return new CursorLoader(this, muri, null, null, null, null);
		}else
			return null;
	}
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		if(arg1!=null)
		sortUsingSectionIndexor(arg1);
	}
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		if(adapter!=null)
		adapter.swapCursor(null);
	}
	private void sortUsingSectionIndexor(final Cursor mCursor ) {
		if (mTask != null && (mTask.getStatus() == Status.RUNNING))
			return;
		mTask = new AsyncTask<Void, Void, Integer>() {
			
			@Override
			protected void onPreExecute() {
				if(mCursor.getCount()<=0){
					progress.setVisibility(View.INVISIBLE);
				    mtext.setVisibility(View.VISIBLE);
				    mTask.cancel(true);
				}
			};
			
			@Override
			protected Integer doInBackground(Void... params) {
				try {
					indexer = new AlphabetIndexer(mCursor,
							mCursor.getColumnIndexOrThrow(ContentDescriptor.WSContact.Cols.NAME),
							"#ABCDEFGHIJKLMNOPQRSTUVWXYZ");
					sectionToOffset = new HashMap<Integer, Integer>();
					sectionToPosition = new TreeMap<Integer, Integer>();
					final int count = mCursor.getCount();
					int i;

					boolean is = false;
					for (i = count - 1; i >= 0; i--) {
						int sec = indexer.getSectionForPosition(i);
						if (sec == 0) {
							is = true;
						}
						sectionToPosition.put(sec, i);
					}

					Integer value = sectionToPosition.get(0);
					if ((value != null) && (is) && (value.intValue() != 0)) {
						sectionToPosition.remove(0);
						indexer.getSections()[0] = "A";
					}

					i = 0;
					usedSectionNumbers = new int[sectionToPosition.keySet().size()];
					for (Integer section : sectionToPosition.keySet()) {
						sectionToOffset.put(section, i);
						usedSectionNumbers[i] = section;
						i++;
					}
					for (Integer section : sectionToPosition.keySet()) {
						sectionToPosition.put(section,
								sectionToPosition.get(section)
								+ sectionToOffset.get(section));
					}
				} catch (Exception e) {
					e.printStackTrace();
					return -1;
				}
				return 0;
			}

			@Override
			protected void onPostExecute(Integer params) {

				if(params ==-1){
					progress.setVisibility(View.INVISIBLE);
					mtext.setVisibility(View.VISIBLE);
					return;
				}
				Log.i("TAG","calling onPost execute"+mCursor.getCount());
				if (adapter == null) {
					adapter = new ContactListAdapter(ContactSearchableActivity.this, mCursor, indexer, usedSectionNumbers,sectionToOffset, sectionToPosition);
					listview.setAdapter(adapter);
					progress.setVisibility(View.INVISIBLE);
					listview.setVisibility(View.VISIBLE);
				}else{
					adapter.refresh(indexer, usedSectionNumbers,
							sectionToOffset, sectionToPosition);	
					adapter.swapCursor(mCursor);
				}
			}
		};
		mTask.execute();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("TAG","calling on stop");
		getSupportLoaderManager().destroyLoader(LOADER_ID);
	}

}
