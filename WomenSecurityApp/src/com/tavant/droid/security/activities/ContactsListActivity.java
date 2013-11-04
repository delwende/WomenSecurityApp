package com.tavant.droid.security.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AlphabetIndexer;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.tavant.droid.security.R;
import com.tavant.droid.security.adapters.FacebookFriendsAdapter;
import com.tavant.droid.security.adapters.ContactListAdapter;
import com.tavant.droid.security.database.ContentDescriptor;
import com.tavant.droid.security.database.ContentDescriptor.WSContact;
import com.tavant.droid.security.prefs.CommonPreferences;
import com.tavant.droid.security.utils.Utils;




public class ContactsListActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> 
{

	private ProgressBar progress=null;
	private ListView listview=null;
	private ContentResolver resolver=null;
	private ContactListAdapter adapter=null;


	private RelativeLayout layout=null;
	private CommonPreferences prefs=null;
	private static final int LOADER_ID = 0x03;
	private Cursor mCursor=null;
	private TextView mtext=null;


	private AlphabetIndexer indexer;
	private int[] usedSectionNumbers;
	private Map<Integer, Integer> sectionToOffset;
	private Map<Integer, Integer> sectionToPosition;
	private AsyncTask<Void, Void,Integer> mTask = null;
	private boolean showMenu=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendslist);
		progress=(ProgressBar)findViewById(R.id.loading);
		listview=(ListView)findViewById(R.id.friendslist);
		mtext=(TextView)findViewById(R.id.load_status);
		layout=(RelativeLayout)findViewById(R.id.parent);
		resolver=getContentResolver();
		prefs=CommonPreferences.getInstance();
		getSupportLoaderManager().initLoader(LOADER_ID, null, this);

	}
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return new CursorLoader(this, ContentDescriptor.WSContact.CONTENT_URI, null, null, null, ContentDescriptor.WSContact.Cols.NAME + " ASC");
	}




	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
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
							getString(R.string.alphabet));
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
					return;
				}
				Log.i("TAG","calling onPost execute"+mCursor.getCount());
				if (adapter == null) {
					adapter = new ContactListAdapter(ContactsListActivity.this, mCursor, indexer, usedSectionNumbers,sectionToOffset, sectionToPosition);
					listview.setAdapter(adapter);
					progress.setVisibility(View.INVISIBLE);
					listview.setVisibility(View.VISIBLE);
					if(mCursor.getCount()>0){
						showMenu=true;
						if(Utils.hasHoneycomb())
							invalidateOptionsMenu();
						else
							ActivityCompat.invalidateOptionsMenu(ContactsListActivity.this);	
					}	
				}
				else{
					
					adapter.refresh(indexer, usedSectionNumbers,
							sectionToOffset, sectionToPosition);
							
					adapter.swapCursor(mCursor);
				}
			}
		};
		mTask.execute();
	}





	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.fb_friends, menu);
		MenuItem item=menu.findItem(R.id.action_done);
		if(showMenu){
			item.setVisible(showMenu);
		}
		if(Utils.hasHoneycomb()){
		    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		    // Assumes current activity is the searchable activity
		    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

		}
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.action_done)
			finish();
		else if(item.getItemId()==R.id.action_search){
			onSearchRequested();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		getSupportLoaderManager().destroyLoader(LOADER_ID);
	}






}
