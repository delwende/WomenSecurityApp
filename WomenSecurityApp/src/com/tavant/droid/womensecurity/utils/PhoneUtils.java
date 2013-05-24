package com.tavant.droid.womensecurity.utils;

import com.tavant.droid.womensecurity.fragments.ContactsListFragment.ContactsQuery;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

public class PhoneUtils {

	private static final String TAG = "PhoneUtils";

	public static String getContactPhoneNumber(Context context, String contactId) {
		int type = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
		String phoneNumber = null;

		String[] whereArgs = new String[] { String.valueOf(contactId),
				String.valueOf(type) };

		Log.d(TAG, "Got contact id: " + contactId);

		Cursor cursor = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? and "
						+ ContactsContract.CommonDataKinds.Phone.TYPE + " = ?",
				whereArgs, null);

		int phoneNumberIndex = cursor
				.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

		if (cursor != null) {
			Log.d(TAG, "Returned contact count: " + cursor.getCount());
			try {
				if (cursor.moveToFirst()) {
					phoneNumber = cursor.getString(phoneNumberIndex);
				}
			} finally {
				cursor.close();
			}
		}

		Log.d(TAG, "Returning phone number: " + phoneNumber);
		return phoneNumber;
	}

	public static String getDisplayName(Context context, String phoneNumber) {
		String name = null;
		
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, new String[]{PhoneLookup.DISPLAY_NAME},null,null,null);

		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				name = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			}
		}
		
		return name;
	}

}
