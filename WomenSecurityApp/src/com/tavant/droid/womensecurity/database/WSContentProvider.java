package com.tavant.droid.womensecurity.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * This class defines the WomenSecurityContentProvider.
 * When registered with in the Android manifest file, the Android runtime
 * will manage the instantiation and shutdown of the provider.
 * @author rakesh
 *
 */
public class WSContentProvider extends ContentProvider {
	private WSContactsDatabase wsContactsDb;

	@Override
	public boolean onCreate() {
		Context ctx = getContext();
		wsContactsDb = new WSContactsDatabase(ctx);
		return true;
	}
	
	/**
	 * Utility function to return the mime type based on a given URI
	 */
	@Override
	public String getType(Uri uri) {
		final int match = ContentDescriptor.URI_MATCHER.match(uri);
		switch(match){
		case ContentDescriptor.WSContact.PATH_TOKEN:
			return ContentDescriptor.WSContact.CONTENT_TYPE_DIR;
		case ContentDescriptor.WSContact.PATH_FOR_ID_TOKEN:
			return ContentDescriptor.WSContact.CONTENT_ITEM_TYPE;
        default:
            throw new UnsupportedOperationException ("URI " + uri + " is not supported.");
		}	
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = wsContactsDb.getWritableDatabase();
		int token = ContentDescriptor.URI_MATCHER.match(uri);
		switch(token){
			case ContentDescriptor.WSContact.PATH_TOKEN:{
				long id = db.insert(ContentDescriptor.WSContact.NAME, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return ContentDescriptor.WSContact.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
			}
			case ContentDescriptor.WSFacebook.PATH_TOKEN:{
				long id = db.insert(ContentDescriptor.WSFacebook.NAME_TABLE, null, values);
				//getContext().getContentResolver().notifyChange(uri, null);
				return ContentDescriptor.WSFacebook.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
			}
            default: {
                throw new UnsupportedOperationException("URI: " + uri + " not supported.");
            }
		}
	}

	/**
	 * Function to query the content provider.  This example queries the backing database.
	 * It uses the SQLite API to retrieve wscontacts data based on the URI specified.
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = wsContactsDb.getReadableDatabase();
		final int match = ContentDescriptor.URI_MATCHER.match(uri);
		switch(match){
			// retrieve wscontacts list
			case ContentDescriptor.WSContact.PATH_TOKEN:{
				SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
				builder.setTables(ContentDescriptor.WSContact.NAME);
				return builder.query(db, null, null, null, null, null, null);
			}
			case ContentDescriptor.WSFacebook.PATH_TOKEN:{
				SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
				builder.setTables(ContentDescriptor.WSFacebook.NAME_TABLE);
				return builder.query(db, null, null, null, null, null, null);
			}
			default: return null;
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = wsContactsDb.getReadableDatabase();
		final int match = ContentDescriptor.URI_MATCHER.match(uri);
		switch (match) {
		case ContentDescriptor.WSFacebook.PATH_TOKEN:{
			return db.update(ContentDescriptor.WSFacebook.NAME_TABLE, values, selection, selectionArgs);
		 }
		default:
			break;
		}
		return 0;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

}
