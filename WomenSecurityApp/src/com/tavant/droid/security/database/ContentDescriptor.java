package com.tavant.droid.security.database;

import java.util.HashMap;

import android.app.SearchManager;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author rakesh
 */
public class ContentDescriptor {
	// utility variables
	public static final String AUTHORITY = "com.tavant.droid.security.database.contentprovider";
	private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
	public static final UriMatcher URI_MATCHER = buildUriMatcher();
	public static final String KEY_WORD = SearchManager.SUGGEST_COLUMN_TEXT_1;
	public static final HashMap<String,String> mColumnMapFB = buildColumnMapfacebook();
	public static final HashMap<String,String> mColumnMapContact = buildColumnMapcontact();
	//The columns we'll include in the dictionary table
	public static final String KEY_DEFINITION = SearchManager.SUGGEST_COLUMN_TEXT_2;

	private ContentDescriptor(){};

	// register identifying URIs for Restaurant entity
	// the TOKEN value is associated with each URI registered
	private static  UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = AUTHORITY;

		matcher.addURI(authority, WSContact.PATH, WSContact.PATH_TOKEN);
		matcher.addURI(authority, WSContact.PATH_FOR_ID, WSContact.PATH_FOR_ID_TOKEN);
		matcher.addURI(authority, WSFacebook.PATH, WSFacebook.PATH_TOKEN);
		matcher.addURI(authority, WSFacebook.PATH_FOR_ID, WSFacebook.PATH_FOR_ID_TOKEN);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, WSFacebook.SEARCH_SUGGEST);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", WSFacebook.SEARCH_SUGGEST);
		return matcher;
	}
	private static HashMap<String,String> buildColumnMapfacebook() {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put(KEY_WORD, WSFacebook.Cols.FBNAME);
		map.put(BaseColumns._ID, "rowid AS " +
				WSFacebook.Cols.ID);
		 map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " +
	                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		return map;
	}
	private static HashMap<String,String> buildColumnMapcontact() {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put(KEY_WORD, WSContact.Cols.NAME);
		map.put(KEY_DEFINITION, WSContact.Cols.PHONE);
		map.put(BaseColumns._ID, "rowid AS " +
				WSContact.Cols.ID);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		return map;
	}



	// Define a static class that represents description of stored content entity.
	// Here we define contacts
	public static class WSContact {
		// an identifying name for entity
		public static final String NAME = "wscontact";
		public static final String PATH = "wscontacts";
		public static final int PATH_TOKEN = 100;
		public static final String PATH_FOR_ID = "wscontacts/*";
		public static final int PATH_FOR_ID_TOKEN = 200;
		public static final int SEARCH_SUGGEST = 3;

		// URI for all content stored as WSContact entity
		// BASE_URI + PATH ==> "com.tavant.droid.womensecurity.database.contentprovider";
		public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();

		// define content mime type for entity
		public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.wscontact.app";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.wscontact.app";

		// a static class to store columns in entity
		public static class Cols {
			public static final String ID = BaseColumns._ID; // convention
			public static final String NAME = "suggest_text_1";
			public static final String CONTACTS_ID="wscontact_id";
			public static final String PHONE = "suggest_text_2";
		}

	}


	public static class WSFacebook {
		// an identifying name for entity
		public static final String NAME_TABLE = "wsfb";

		public static final String PATH = "wsfb";
		public static final int PATH_TOKEN = 500;
		public static final String PATH_FOR_ID = "wsfb/*";
		public static final int PATH_FOR_ID_TOKEN = 600;
		public static final int SEARCH_SUGGEST = 2;
		public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();

		public static class Cols {
			public static final String ID = BaseColumns._ID; // convention
			public static final String FBID = "fbid";
			public static final String FBNAME  = "suggest_text_1";
			public static final String FBSTATUS  = "status";
			public static final String IMGURL  = "imgurl";
		}

	}

}
