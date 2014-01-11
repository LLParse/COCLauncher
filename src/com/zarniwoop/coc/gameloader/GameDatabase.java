package com.zarniwoop.coc.gameloader;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDatabase extends SQLiteOpenHelper {

	public static final String TABLE_CREDS = "credentials";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_LOW = "low";
	public static final String COLUMN_HIGH = "high";
	public static final String COLUMN_PASS = "pass";
	public static final String COLUMN_THLEVEL = "thlevel";
	public static final String COLUMN_LOCALE = "locale";
	public static final String COLUMN_NOTIFY_TIME = "notify_time";

	private static final String DATABASE_NAME = "games.db";
	private static final int DATABASE_VERSION = 2;

	private static final String V2_ADD_NOTIFY_DATE = "alter table " + TABLE_CREDS
			+ " add column " + COLUMN_NOTIFY_TIME + " integer;"; 
	
	private static final String DATABASE_CREATE = "create table " + TABLE_CREDS
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_LOW + " text not null, "
			+ COLUMN_HIGH + " text not null, "
			+ COLUMN_PASS + " text not null, "
			+ COLUMN_THLEVEL + " text not null, "
			+ COLUMN_LOCALE + " text not null, "
			+ COLUMN_NOTIFY_TIME + " integer);"
			+ "CREATE UNIQUE INDEX cred_idx1 ON " + TABLE_CREDS + " (low, high, pass, thlevel);";

	public GameDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		while (oldVersion != newVersion) {
			try {
				switch (oldVersion) {
				case 1:
					db.execSQL(V2_ADD_NOTIFY_DATE);
					break;			
				}
				oldVersion++;
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(255);
			}
		}
	}

}
