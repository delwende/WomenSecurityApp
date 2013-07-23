package com.glaesis.droid.security.database;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author rakesh
 */
public class ContentDescriptor {
	// utility variables
	public static final String AUTHORITY = "com.tavant.droid.womensecurity.database.contentprovider";
	private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
	public static final UriMatcher URI_MATCHER = buildUriMatcher();
	
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
		
        return matcher;
	}
	
	// Define a static class that represents description of stored content entity.
	// Here we define contacts
	public static class WSContact {
		// an identifying name for entity
		public static final String NAME = "wscontact";
		
		// define a URI paths to access entity
		// BASE_URI/wscontacts - for list of wscontacts
		// BASE_URI/wscontacts/* - retreive specific wscontacts by id
		// the toke value are used to register path in matcher (see above)
		public static final String PATH = "wscontacts";
		public static final int PATH_TOKEN = 100;
		public static final String PATH_FOR_ID = "wscontacts/*";
		public static final int PATH_FOR_ID_TOKEN = 200;
		
		// URI for all content stored as WSContact entity
		// BASE_URI + PATH ==> "com.tavant.droid.womensecurity.database.contentprovider";
		public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
		
		// define content mime type for entity
		public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.wscontact.app";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.wscontact.app";
		
		// a static class to store columns in entity
		public static class Cols {
			public static final String ID = BaseColumns._ID; // convention
			public static final String NAME = "wscontact_name";
			public static final String ADDRESS  = "wscontact_addr";
			public static final String CITY = "wscontact_city";
			public static final String STATE = "wscontact_state";
			public static final String PHONE = "wscontact_phone";
		}
		
	}
	
	
	public static class WSFacebook {
		// an identifying name for entity
		public static final String NAME_TABLE = "wsfb";
		
		// define a URI paths to access entity
		// BASE_URI/wscontacts - for list of wscontacts
		// BASE_URI/wscontacts/* - retreive specific wscontacts by id
		// the toke value are used to register path in matcher (see above)
		public static final String PATH = "wsfb";
		public static final int PATH_TOKEN = 500;
		public static final String PATH_FOR_ID = "wsfb/*";
		public static final int PATH_FOR_ID_TOKEN = 600;
		
		// URI for all content stored as WSContact entity
		// BASE_URI + PATH ==> "com.tavant.droid.womensecurity.database.contentprovider";
		public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
		
	
		
		// a static class to store columns in entity
		public static class Cols {
			public static final String ID = BaseColumns._ID; // convention
			public static final String FBID = "fbid";
			public static final String FBNAME  = "fbname";
			public static final String FBSTATUS  = "status";
			public static final String IMGURL  = "imgurl";
		}
		
	}
	
}
