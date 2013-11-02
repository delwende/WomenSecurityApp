package com.tavant.droid.security.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.ContentValues;
import android.database.Cursor;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AlphabetIndexer;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.tavant.droid.security.adapters.FbFriendsAdapter;
import com.tavant.droid.security.database.ContentDescriptor;
import com.tavant.droid.security.database.ContentDescriptor.WSContact;
import com.tavant.droid.security.prefs.CommonPreferences;
import com.tavant.droid.security.utils.Utils;

public class FacebookFriendPicker extends ActionBarActivity implements LoaderCallbacks<Cursor>  {

	private ProgressBar progress=null;
	private ListView listview=null;
	//private Cursor mPhContactCursor=null;
	private Session session=null;
	private CommonPreferences prefs=null;
	private static final int LOADER_ID = 0x02;
	private TextView mtext=null;
	private FacebookFriendsAdapter adapter=null;
	private AlphabetIndexer indexer;
	private int[] usedSectionNumbers;
	private Map<Integer, Integer> sectionToOffset;
	private Map<Integer, Integer> sectionToPosition;
	private AsyncTask<Void, Void,Integer> mTask = null;
	private ArrayList<String>mIDs=null;
	private boolean showMenu=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendslist);
		progress=(ProgressBar)findViewById(R.id.loading);
		listview=(ListView)findViewById(R.id.friendslist);
		//adaptor=new FacebookFreindsAdapter(this, null);
		//listview.setAdapter(adapter);
		mtext=(TextView)findViewById(R.id.load_status);
		prefs=CommonPreferences.getInstance();
		session=new Session(this);
		Cursor mCursor=getContentResolver().query(ContentDescriptor.WSFacebook.CONTENT_URI, null, null, null, ContentDescriptor.WSFacebook.Cols.FBNAME + " ASC");
		if(mCursor.getCount()>0){
			mCursor.close();
			getSupportLoaderManager().initLoader(LOADER_ID, null, this);
		}
		else{
			loadFBSession();
		}
	}

	private void getCurrentlySelectedCursor(){
		Cursor temp=getContentResolver().query(ContentDescriptor.WSContact.CONTENT_URI,null,null,null,null);
		Log.i("TAG","length of cursor"+temp.getCount());
		mIDs=new ArrayList<String>();
		if(temp!=null&&temp.getCount()>0){
			while(temp.moveToNext()){
				mIDs.add(temp.getString(temp.getColumnIndex(WSContact.Cols.CONTACTS_ID)));  
			}
		}		
	}

	private void loadFBSession() {
		AccessToken token=AccessToken.createFromExistingAccessToken(prefs.getFbAcessToken(),null,null,
				AccessTokenSource.FACEBOOK_APPLICATION_SERVICE,null);
		session.open(token, new StatusCallback() {
			@Override
			public void call(Session msession, SessionState state, Exception exception) {
				if(state.isOpened()){
					getFBFriends();
				}
			}
		});
	}

	private void getFBFriends(){
		Request req= Request.newMyFriendsRequest(session,  new Request.GraphUserListCallback() {
			@Override
			public void onCompleted(final List<GraphUser> users, Response response) {
				if(users.size()==0){
					mtext.setText(getString(R.string.cant_load));
					mtext.setVisibility(View.VISIBLE);
					progress.setVisibility(View.GONE);
				}else{
					Log.d("TAG", "size of user"+users.size());
					AsyncTask<List<GraphUser>, Void, Boolean>task=new AsyncTask<List<GraphUser>, Void, Boolean>(){
						@Override
						protected Boolean doInBackground(List<GraphUser>... params) {
							try{
								for(GraphUser user:users){
									Log.i("TAG",""+user.getInnerJSONObject().toString());
									ContentValues values=new ContentValues();
									Log.d("TAG",""+user.getId()+"::"+user.getFirstName()+user.getLastName()+"::"+user.getMiddleName()+":"+user.getUsername());
									values.put(ContentDescriptor.WSFacebook.Cols.FBID, user.getId());
									values.put(ContentDescriptor.WSFacebook.Cols.FBNAME, user.getName());
									Log.i("TAG","dd"+user.getLink());

									values.put(ContentDescriptor.WSFacebook.Cols.IMGURL, "http://graph.facebook.com/"+user.getId()+"/picture?type=square");           
									int numRows=getContentResolver().update(ContentDescriptor.WSFacebook.CONTENT_URI, values, ContentDescriptor.WSFacebook.Cols.FBID+"=?", new String[]{user.getId()});
									if(numRows==0){
										values.put(ContentDescriptor.WSFacebook.Cols.FBSTATUS, 0);    	
										getContentResolver().insert(ContentDescriptor.WSFacebook.CONTENT_URI, values);
									}
								}
								return true;
							}catch(Exception e){e.printStackTrace();
							return false;
							}
						}
						@Override
						protected void onPostExecute(Boolean result) {
							if(result){
								getSupportLoaderManager().initLoader(LOADER_ID, null, FacebookFriendPicker.this);
							}else{
								mtext.setText(getString(R.string.cant_load));
								mtext.setVisibility(View.VISIBLE);
								progress.setVisibility(View.GONE);
							}
						}

					};
					task.execute(users);
				}
			}
		});
		req.executeAsync();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(FacebookFriendPicker.this,
				ContentDescriptor.WSFacebook.CONTENT_URI, null, null, null, ContentDescriptor.WSFacebook.Cols.FBNAME + " ASC"
				);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		sortUsingSectionIndexor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}


	private void sortUsingSectionIndexor(final Cursor mCursor ) {
		if (mTask != null && (mTask.getStatus() == Status.RUNNING))
			return;
		mTask = new AsyncTask<Void, Void, Integer>() {
			@Override
			protected Integer doInBackground(Void... params) {
				try {
					indexer = new AlphabetIndexer(mCursor,
							mCursor.getColumnIndexOrThrow(ContentDescriptor.WSFacebook.Cols.FBNAME),
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
					return;
				}
				Log.i("TAG","calling onPost execute"+mCursor.getCount());
				if (adapter == null) {
					adapter = new FacebookFriendsAdapter(FacebookFriendPicker.this, mCursor, indexer, usedSectionNumbers,sectionToOffset, sectionToPosition);
					listview.setAdapter(adapter);
					progress.setVisibility(View.INVISIBLE);
					listview.setVisibility(View.VISIBLE);
					if(mCursor.getCount()>0){
						showMenu=true;
						if(Utils.hasHoneycomb())
							invalidateOptionsMenu();
						else
							ActivityCompat.invalidateOptionsMenu(FacebookFriendPicker.this);	
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
