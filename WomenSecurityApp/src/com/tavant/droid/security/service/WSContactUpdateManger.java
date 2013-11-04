package com.tavant.droid.security.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

import com.tavant.droid.security.database.ContentDescriptor;
import com.tavant.droid.security.utils.PhoneUtils;

public class WSContactUpdateManger extends Service {

	private ContentResolver resolver=null;
	
    private static boolean insertcompleted=false;

	@Override
	public void onCreate() {
		super.onCreate();
		resolver=getContentResolver();
		insertcompleted=false;
		updateContactTable();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		resolver.registerContentObserver(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, true, mObserver);
		return START_STICKY;
	}

	private synchronized void  updateContactTable() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try{
				Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,
						Contacts.HAS_PHONE_NUMBER +" = 1", null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
				if(cur!=null&&cur.getCount()>0){
					while(cur.moveToNext()){
						try{
					String id=""+cur.getString(cur.getColumnIndex(Contacts._ID));
					if(checkMobileNumberExistforId(id)){
						Cursor temp=resolver.query(ContentDescriptor.WSContact.CONTENT_URI, null, ContentDescriptor.WSContact.Cols.CONTACTS_ID+" =?", new String[]{id}, null);
						if(temp!=null&temp.getCount()==0){
						ContentValues values=new ContentValues();
						values.put(ContentDescriptor.WSContact.Cols.NAME, cur.getString(cur.getColumnIndex(Contacts.DISPLAY_NAME)));
						values.put(ContentDescriptor.WSContact.Cols.CONTACTS_ID, id);
						values.put(ContentDescriptor.WSContact.Cols.PHONE,  PhoneUtils.getContactPhoneNumber(getApplicationContext(),id));
						values.put(ContentDescriptor.WSContact.Cols.IMGURL, ""+cur.getLong(cur.getColumnIndex(Contacts._ID)));
						values.put(ContentDescriptor.WSContact.Cols.STATUS, 0);
						Log.i("TAG","inserting");
						resolver.insert(ContentDescriptor.WSContact.CONTENT_URI, values);
						}else if(temp!=null&&temp.getCount()>0){
							
							while(temp.moveToNext()){
								ContentValues values=new ContentValues();
								values.put(ContentDescriptor.WSContact.Cols.NAME, cur.getString(cur.getColumnIndex(Contacts.DISPLAY_NAME)));
								values.put(ContentDescriptor.WSContact.Cols.PHONE,  PhoneUtils.getContactPhoneNumber(getApplicationContext(),id));
								values.put(ContentDescriptor.WSContact.Cols.IMGURL, ""+cur.getLong(cur.getColumnIndex(Contacts._ID)));
								Log.i("TAG","updating");
								resolver.update(ContentDescriptor.WSContact.CONTENT_URI, values, ContentDescriptor.WSContact.Cols.CONTACTS_ID+" =?", new String[]{id});
							}
						}
					}
						}catch(Exception e){e.printStackTrace();}
					}

				}
			}catch(Exception e3){
				e3.printStackTrace();
			}finally{
				insertcompleted=true;
			}
			}
		
		}).start();

	}

	private boolean checkMobileNumberExistforId(String id){
		int type = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
		String[] whereArgs = new String[] { String.valueOf(id),
				String.valueOf(type) };
		Cursor cursor = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? and "
						+ ContactsContract.CommonDataKinds.Phone.TYPE + " = ?",
						whereArgs, null);
		return ((cursor!=null&&cursor.getCount()>0) ? true: false);
	}
	
	private  ContentObserver mObserver = new ContentObserver(new Handler()) {

	    @Override
	    public void onChange(boolean selfChange) {
	        super.onChange(selfChange);
            Log.i("TAG","mobile contact changed"); 
            if(insertcompleted)
            	updateContactTable();
	    }

	};

	@Override
	public void onDestroy() {
		resolver.unregisterContentObserver(mObserver);
	};

}
