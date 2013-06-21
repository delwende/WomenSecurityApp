package com.tavant.droid.womensecurity.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class to manage the creation and modification of database structure.
 * It is also used to manage connection to the SQLite database (hence the OpenHelper in the name)
 * Note that Android SDK will create DB once. Once created it's structure won't change until 
 * version number is changed.
 * 
 *
 */
public class WSContactsDatabase extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "ws_contacts.db";
	private static final int DATABASE_VERSION = 2;
	
	public WSContactsDatabase(Context ctx){
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);    		
	}
	
	/**
	 * What to do when the database is created the first time
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + ContentDescriptor.WSContact.NAME+ " ( " +
				ContentDescriptor.WSContact.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				ContentDescriptor.WSContact.Cols.NAME + " TEXT NOT NULL, " +
				ContentDescriptor.WSContact.Cols.ADDRESS 	+ " TEXT , " +
				ContentDescriptor.WSContact.Cols.CITY + " TEXT, " +
				ContentDescriptor.WSContact.Cols.STATE + " TEXT, " +
				ContentDescriptor.WSContact.Cols.PHONE + " TEXT NOT NULL, " +
				"UNIQUE (" + 
					ContentDescriptor.WSContact.Cols.ID + 
				") ON CONFLICT REPLACE)"
			);
		
		db.execSQL("CREATE TABLE " + ContentDescriptor.WSFacebook.NAME_TABLE+ " ( " +
				ContentDescriptor.WSContact.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				ContentDescriptor.WSFacebook.Cols.FBID + " TEXT NOT NULL, " +
				ContentDescriptor.WSFacebook.Cols.FBNAME 	+ " TEXT NOT NULL , " +
				ContentDescriptor.WSFacebook.Cols.FBSTATUS 	+ " INTEGER NOT NULL, " +
				ContentDescriptor.WSFacebook.Cols.IMGURL 	+ " TEXT NOT NULL, " +
				"UNIQUE (" + 
					ContentDescriptor.WSContact.Cols.ID + 
				") ON CONFLICT REPLACE)"
			);	
		
	}

	/**
	 * What to do when the database version changes: drop table and recreate
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < newVersion){
        	db.execSQL("DROP TABLE IF EXISTS " + ContentDescriptor.WSContact.NAME);
        	onCreate(db);
        }
	}

}
