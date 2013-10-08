package com.tavant.droid.security.activities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import com.tavant.droid.security.adapters.FbFriendsAdapter;
import com.tavant.droid.security.database.ContentDescriptor;
import com.tavant.droid.security.prefs.CommonPreferences;


public class PickerFriendListActivity extends ActionBarActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> 
{

	private ProgressBar progress=null;
	private ListView listview=null;
	private ContentResolver resolver=null;
	private FbFriendsAdapter adapter=null;	
	private Session session=null;
	private RelativeLayout layout=null;
	private CommonPreferences prefs=null;
	private static final int LOADER_ID = 0x02;



	public static final Uri FRIEND_PICKER = Uri.parse("picker://friend");
	public static final Uri CONTACTS_PICKER = Uri.parse("picker://contacts");
	private AlphabetIndexer indexer;
	private int[] usedSectionNumbers;
	private Map<Integer, Integer> sectionToOffset;
	private Map<Integer, Integer> sectionToPosition;
	private AsyncTask<Void, Void,Integer> mTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendslist);
		progress=(ProgressBar)findViewById(R.id.loading);
		listview=(ListView)findViewById(R.id.friendslist);
		layout=(RelativeLayout)findViewById(R.id.parent);
		resolver=getContentResolver();
		prefs=CommonPreferences.getInstance();
		session=new Session(this);
		getSupportLoaderManager().initLoader(LOADER_ID, null, this);
		loadFBSession();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new android.support.v4.content.CursorLoader(this,ContentDescriptor.WSFacebook.CONTENT_URI, null, null, null, null);	
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		Log.i("TAG1","cursorLength"+arg1.getCount());
		sortUsingSectionIndexor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		Log.i("TAG1","Loader reset");
		adapter.swapCursor(null);
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
				TextView nofriendtext=null;
				if(users.size()==0){
					nofriendtext=new TextView(PickerFriendListActivity.this);
					nofriendtext.setText("No friends");
					RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.addRule(RelativeLayout.CENTER_IN_PARENT);
					layout.addView(nofriendtext,params);
				}else{
					Log.d("TAG", "size of user"+users.size());
					new Thread(new Runnable() {
						@Override
						public void run() {
							for(GraphUser user:users){
								ContentValues values=new ContentValues();
								Log.d("TAG",""+user.getId()+"::"+user.getFirstName()+user.getLastName()+"::"+user.getMiddleName()+":"+user.getName());
								values.put(ContentDescriptor.WSFacebook.Cols.FBID, user.getId());
								values.put(ContentDescriptor.WSFacebook.Cols.FBNAME, user.getName());
								values.put(ContentDescriptor.WSFacebook.Cols.IMGURL, "");           
								int numRows=resolver.update(ContentDescriptor.WSFacebook.CONTENT_URI, values, ContentDescriptor.WSFacebook.Cols.FBID+"=?", new String[]{user.getId()});
						        if(numRows==0){
						           values.put(ContentDescriptor.WSFacebook.Cols.FBSTATUS, 0);    	
								   resolver.insert(ContentDescriptor.WSFacebook.CONTENT_URI, values);
						        }
							}	
							
						}
					});
				}
			}
		});
		req.executeAsync();
	}
	
	
	private void sortUsingSectionIndexor(final Cursor mCursor) {
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
				if (adapter == null) {
					adapter = new FbFriendsAdapter(PickerFriendListActivity.this, mCursor, indexer, usedSectionNumbers,sectionToOffset, sectionToPosition);
					listview.setAdapter(adapter);
					progress.setVisibility(View.INVISIBLE);
					listview.setVisibility(View.VISIBLE);
				} 
				//else if (mAdapter != null) {
//					mAdapter.refresh(indexer, usedSectionNumbers,
//							sectionToOffset, sectionToPosition);
//				}
			}
		};
		mTask.execute();
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		getSupportLoaderManager().destroyLoader(LOADER_ID);
	}

}
