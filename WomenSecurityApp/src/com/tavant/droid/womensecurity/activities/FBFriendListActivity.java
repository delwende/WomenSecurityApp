package com.tavant.droid.womensecurity.activities;

import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
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
import com.tavant.droid.womensecurity.R;
import com.tavant.droid.womensecurity.adapters.FbFriendsAdapter;
import com.tavant.droid.womensecurity.database.ContentDescriptor;
import com.tavant.droid.womensecurity.utils.WSConstants;


public class FBFriendListActivity extends Activity
{

private ProgressBar progress=null;
private ListView listview=null;
private ContentResolver resolver=null;
private FbFriendsAdapter adapter=null;	
private Session session=null;
private RelativeLayout layout=null;
private SharedPreferences prefs=null;


@Override
protected void onCreate(Bundle savedInstanceState) {	
	super.onCreate(savedInstanceState);
	setContentView(R.layout.friendslist);
	progress=(ProgressBar)findViewById(R.id.loading);
	listview=(ListView)findViewById(R.id.friendslist);
	layout=(RelativeLayout)findViewById(R.id.parent);
	resolver=getContentResolver();
	prefs=getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
	session=new Session(this);
	AccessToken token=AccessToken.createFromExistingAccessToken(prefs.getString(WSConstants.PROPERTY_FB_ACCESSTOKEN, null),null,null,
			AccessTokenSource.FACEBOOK_APPLICATION_WEB,null);
	session.open(token, new StatusCallback() {
		@Override
		public void call(Session msession, SessionState state, Exception exception) {
			  session=msession;
			  checkintialiLoading();
		}
	});
	
}

private void checkintialiLoading() {
	Cursor curosr=resolver.query(ContentDescriptor.WSFacebook.CONTENT_URI, null, null, null, null);
	if(curosr!=null && curosr.getCount()==0){
		adapter=new FbFriendsAdapter(this,curosr);
	}else{
		
	   //FBFriendListActivity OpenRe	
		
		Request req= Request.newMyFriendsRequest(session,  new Request.GraphUserListCallback() {
			@Override
			public void onCompleted(List<GraphUser> users, Response response) {
				
				TextView nofriendtext=null;
				if(users.size()==0){
					 nofriendtext=new TextView(FBFriendListActivity.this);
					 nofriendtext.setText("No friends");
					 RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					 params.addRule(RelativeLayout.CENTER_IN_PARENT);
					 layout.addView(nofriendtext,params);
				}else{
					Log.i("TAG", "size of user"+users.size());
					for(GraphUser user:users){
						ContentValues values=new ContentValues();
						values.put(ContentDescriptor.WSFacebook.Cols.FBID, user.getId());
						values.put(ContentDescriptor.WSFacebook.Cols.FBNAME, user.getFirstName());
						values.put(ContentDescriptor.WSFacebook.Cols.IMGURL, "");
						values.put(ContentDescriptor.WSFacebook.Cols.FBSTATUS, 0);
						resolver.insert(ContentDescriptor.WSFacebook.CONTENT_URI, values);
					}
					Cursor curosr=resolver.query(ContentDescriptor.WSFacebook.CONTENT_URI, null, null, null, null);
					adapter=new FbFriendsAdapter(FBFriendListActivity.this,curosr);
					progress.setVisibility(View.INVISIBLE);
					
				}
				
			}
	    });
		req.executeAsync();
		
	}
}	
	

}
