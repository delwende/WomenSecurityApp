package com.tavant.droid.security.activities;

import java.util.ArrayList;
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
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
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
import com.tavant.droid.security.database.ContentDescriptor.WSContact;
import com.tavant.droid.security.prefs.CommonPreferences;




public class PickerFriendListActivity extends ActionBarActivity
{

	private ProgressBar progress=null;
	private ListView listview=null;
	private ContentResolver resolver=null;
	private FbFriendsAdapter adapter=null;
	//private Cursor mPhContactCursor=null;
	private Session session=null;
	private RelativeLayout layout=null;
	private CommonPreferences prefs=null;
	private static final int LOADER_ID = 0x02;
	private Cursor mCursor=null;



	public static final Uri FRIEND_PICKER = Uri.parse("picker://friend");
	public static final Uri CONTACTS_PICKER = Uri.parse("picker://contacts");
	private AlphabetIndexer indexer;
	private int[] usedSectionNumbers;
	private Map<Integer, Integer> sectionToOffset;
	private Map<Integer, Integer> sectionToPosition;
	private AsyncTask<Void, Void,Integer> mTask = null;
	private ArrayList<String>mIDs=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendslist);
		progress=(ProgressBar)findViewById(R.id.loading);
		listview=(ListView)findViewById(R.id.friendslist);
		layout=(RelativeLayout)findViewById(R.id.parent);
		resolver=getContentResolver();
		prefs=CommonPreferences.getInstance();
		if(getIntent().getData().equals(FRIEND_PICKER)){
		session=new Session(this);
		getNewFBCursor();
		}else if(getIntent().getData().equals(CONTACTS_PICKER)){
			getPhoneContactCursor();
		}
	}

	private void getNewFBCursor() {
		mCursor=resolver.query(ContentDescriptor.WSFacebook.CONTENT_URI, null, null, null, ContentDescriptor.WSFacebook.Cols.FBNAME + " ASC");
		if(mCursor.getCount()>0){
			sortUsingSectionIndexor();
		}else{
			loadFBSession();
		}
	}
	
	
	private void getPhoneContactCursor(){
		
		mCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,
				Contacts.HAS_PHONE_NUMBER +" = 1", null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
		getCurrentlySelectedCursor();
		sortUsingSectionIndexor();
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
								Log.i("TAG",""+user.getInnerJSONObject().toString());
								ContentValues values=new ContentValues();
								Log.d("TAG",""+user.getId()+"::"+user.getFirstName()+user.getLastName()+"::"+user.getMiddleName()+":"+user.getUsername());
								values.put(ContentDescriptor.WSFacebook.Cols.FBID, user.getId());
								values.put(ContentDescriptor.WSFacebook.Cols.FBNAME, user.getName());
								Log.i("TAG","dd"+user.getLink());
								
								values.put(ContentDescriptor.WSFacebook.Cols.IMGURL, "http://graph.facebook.com/"+user.getId()+"/picture?type=square");           
								int numRows=resolver.update(ContentDescriptor.WSFacebook.CONTENT_URI, values, ContentDescriptor.WSFacebook.Cols.FBID+"=?", new String[]{user.getId()});
								if(numRows==0){
									values.put(ContentDescriptor.WSFacebook.Cols.FBSTATUS, 0);    	
									resolver.insert(ContentDescriptor.WSFacebook.CONTENT_URI, values);
								}
							}
							getNewFBCursor();
						}
					}).start();
				}
			}
		});
		req.executeAsync();
	}


	private void sortUsingSectionIndexor() {
		if (mTask != null && (mTask.getStatus() == Status.RUNNING))
			return;
		mTask = new AsyncTask<Void, Void, Integer>() {
			@Override
			protected Integer doInBackground(Void... params) {
				try {
					if(getIntent().getData().equals(FRIEND_PICKER)){
					indexer = new AlphabetIndexer(mCursor,
							mCursor.getColumnIndexOrThrow(ContentDescriptor.WSFacebook.Cols.FBNAME),
							"#ABCDEFGHIJKLMNOPQRSTUVWXYZ");
					}else{
						indexer = new AlphabetIndexer(mCursor,
								mCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME),getString(R.string.alphabet)
								);
					}

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
					if(getIntent().getData().equals(FRIEND_PICKER)){
					adapter = new FbFriendsAdapter(PickerFriendListActivity.this, mCursor, indexer, usedSectionNumbers,sectionToOffset, sectionToPosition,FRIEND_PICKER,null);
					}else{
						adapter = new FbFriendsAdapter(PickerFriendListActivity.this, mCursor, indexer, usedSectionNumbers,sectionToOffset, sectionToPosition,CONTACTS_PICKER,mIDs);
					}
					listview.setAdapter(adapter);
					progress.setVisibility(View.INVISIBLE);
					listview.setVisibility(View.VISIBLE);}
				else{
					adapter.refresh(indexer, usedSectionNumbers,
							sectionToOffset, sectionToPosition);
				}
			}
		};
		mTask.execute();
	}





	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.fb_friends, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
