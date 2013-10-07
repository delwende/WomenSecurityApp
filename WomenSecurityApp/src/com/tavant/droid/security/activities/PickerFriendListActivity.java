package com.tavant.droid.security.activities;

import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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
import com.tavant.droid.security.BaseActivity;
import com.tavant.droid.security.R;
import com.tavant.droid.security.adapters.FbFriendsAdapter;
import com.tavant.droid.security.data.BaseData;
import com.tavant.droid.security.database.ContentDescriptor;
import com.tavant.droid.security.prefs.CommonPreferences;
import com.tavant.droid.security.utils.WSConstants;


public class PickerFriendListActivity extends BaseActivity
{

private ProgressBar progress=null;
private ListView listview=null;
private ContentResolver resolver=null;
private FbFriendsAdapter adapter=null;	
private Session session=null;
private RelativeLayout layout=null;
private CommonPreferences prefs=null;


public static final Uri FRIEND_PICKER = Uri.parse("picker://friend");
public static final Uri CONTACTS_PICKER = Uri.parse("picker://contacts");

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
	AccessToken token=AccessToken.createFromExistingAccessToken(prefs.getFbAcessToken(),null,null,
			AccessTokenSource.FACEBOOK_APPLICATION_SERVICE,null);
	session.open(token, new StatusCallback() {
		@Override
		public void call(Session msession, SessionState state, Exception exception) {
			  checkintialiLoading();
		}
	});
}

private void checkintialiLoading() {
	Cursor curosr=resolver.query(ContentDescriptor.WSFacebook.CONTENT_URI, null, null, null, null);
	if(curosr!=null && curosr.getCount()>0){
		adapter=new FbFriendsAdapter(this,curosr);
		listview.setAdapter(adapter);
		progress.setVisibility(View.INVISIBLE);
		listview.setVisibility(View.VISIBLE);
		
	}else{
		
	   //FBFriendListActivity OpenRe	
		
		Request req= Request.newMyFriendsRequest(session,  new Request.GraphUserListCallback() {
			@Override
			public void onCompleted(List<GraphUser> users, Response response) {
				
				TextView nofriendtext=null;
				if(users.size()==0){
					 nofriendtext=new TextView(PickerFriendListActivity.this);
					 nofriendtext.setText("No friends");
					 RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					 params.addRule(RelativeLayout.CENTER_IN_PARENT);
					 layout.addView(nofriendtext,params);
				}else{
					Log.d("TAG", "size of user"+users.size());
					for(GraphUser user:users){
						ContentValues values=new ContentValues();
						Log.d("TAG",""+user.getId()+"::"+user.getFirstName()+user.getLastName()+"::"+user.getMiddleName()+":"+user.getName());
						values.put(ContentDescriptor.WSFacebook.Cols.FBID, user.getId());
						values.put(ContentDescriptor.WSFacebook.Cols.FBNAME, user.getName());
						values.put(ContentDescriptor.WSFacebook.Cols.IMGURL, "");
						values.put(ContentDescriptor.WSFacebook.Cols.FBSTATUS, 0);
						resolver.insert(ContentDescriptor.WSFacebook.CONTENT_URI, values);
					}
					Cursor curosr=resolver.query(ContentDescriptor.WSFacebook.CONTENT_URI, null, null, null, null);
					adapter=new FbFriendsAdapter(PickerFriendListActivity.this,curosr);
					listview.setAdapter(adapter);
					progress.setVisibility(View.INVISIBLE);
					listview.setVisibility(View.VISIBLE);
					
				}
				
			}
	    });
		req.executeAsync();
		
	}
}

@Override
protected void onComplete(int reqCode, BaseData data) {
	// TODO Auto-generated method stub
	
}

@Override
protected void onError(int reqCode, int errorCode, String errorMessage) {
	// TODO Auto-generated method stub
	
}
@Override
protected void onDestroy() {
		super.onDestroy();
}
	

}
