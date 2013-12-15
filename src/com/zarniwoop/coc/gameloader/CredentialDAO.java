package com.zarniwoop.coc.gameloader;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class CredentialDAO {

	private SQLiteDatabase database;
	private GameDatabase db;
	private String[] allColumns = { GameDatabase.COLUMN_ID,
			GameDatabase.COLUMN_NAME, GameDatabase.COLUMN_LOW,
			GameDatabase.COLUMN_HIGH, GameDatabase.COLUMN_PASS,
			GameDatabase.COLUMN_THLEVEL, GameDatabase.COLUMN_LOCALE };
	private Context context;

	public CredentialDAO(Context context) {
		this.context = context;
		db = new GameDatabase(context);
		open();
	}
	
	private void open() throws SQLException {
		database = db.getWritableDatabase();
	}

	public void close() {
		db.close();
	}
	
	public Credential create(String name, String low, String high, String pass, String thLevel, String locale) {
		ContentValues values = new ContentValues();
		values.put(GameDatabase.COLUMN_NAME, name);
		values.put(GameDatabase.COLUMN_LOW, low);
		values.put(GameDatabase.COLUMN_HIGH, high);
		values.put(GameDatabase.COLUMN_PASS, pass);
		values.put(GameDatabase.COLUMN_THLEVEL, thLevel == null ? "1" : thLevel);
		values.put(GameDatabase.COLUMN_LOCALE, locale);

		Credential cred = null;
		try {
			long insertId = database.insert(GameDatabase.TABLE_CREDS, null, values);
			Cursor cursor = database.query(GameDatabase.TABLE_CREDS, allColumns, GameDatabase.COLUMN_ID + " = " + insertId, null, null, null, null);
			cursor.moveToFirst();
			cred = cursorToCredential(cursor);
			cursor.close();
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		return cred;
	}
	
	public Credential get(String name) {
		Cursor cursor = database.query(GameDatabase.TABLE_CREDS, allColumns, GameDatabase.COLUMN_NAME + " = ?", new String[]{name}, null, null, null);
		cursor.moveToFirst();
		Credential cred = cursorToCredential(cursor);
		return cred;
	}
	
	public List<Credential> getAll() {
		List<Credential> creds = new ArrayList<Credential>();
		Cursor cursor = database.query(GameDatabase.TABLE_CREDS, allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Credential cred = cursorToCredential(cursor);
			creds.add(cred);
			cursor.moveToNext();
		}
		return creds;
	}

	protected Credential cursorToCredential(Cursor cursor) {
		Credential cred = new Credential();
		cred.setId(cursor.getLong(0));
		cred.setName(cursor.getString(1));
		cred.setLow(cursor.getString(2));
		cred.setHigh(cursor.getString(3));
		cred.setPass(cursor.getString(4));
		cred.setThLevel(cursor.getString(5));
		cred.setLocale(cursor.getString(6));
		return cred;
	}
	
}
