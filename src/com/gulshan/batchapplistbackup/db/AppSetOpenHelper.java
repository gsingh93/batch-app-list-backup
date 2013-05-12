package com.gulshan.batchapplistbackup.db;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import com.gulshan.batchapplistbackup.db.DatabaseContract.AppList;
import com.gulshan.batchapplistbackup.db.DatabaseContract.AppSet;
import com.gulshan.batchapplistbackup.db.DatabaseContract.Apps;

public class AppSetOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "app_list.db";

	public AppSetOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + AppSet.TABLE_NAME + "(" + AppSet._ID
				+ " INTEGER PRIMARY KEY," + AppSet.COLUMN_NAME_TITLE + " TEXT"
				+ ");");

		db.execSQL("CREATE TABLE " + AppList.TABLE_NAME + "(" + AppList._ID
				+ " INTEGER PRIMARY KEY," + AppList.COLUMN_NAME_SET_ID
				+ " INTEGER," + AppList.COLUMN_NAME_APP_ID + " INTEGER" + ");");

		db.execSQL("CREATE TABLE " + Apps.TABLE_NAME + "(" + Apps._ID
				+ " INTEGER PRIMARY KEY," + Apps.COLUMN_NAME_NAME + " TEXT"
				+ ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public Cursor getAppSets() {
		return getReadableDatabase().query(AppSet.TABLE_NAME,
				new String[] { AppSet._ID, AppSet.COLUMN_NAME_TITLE }, null,
				null, null, null, null);
	}

	public Cursor getAppsInSet(int setId) {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(Apps.TABLE_NAME + " JOIN " + AppList.TABLE_NAME + " ON "
				+ AppList.COLUMN_NAME_APP_ID + "=" + Apps.TABLE_NAME + "."
				+ Apps._ID);
		return qb.query(db, new String[] { Apps.COLUMN_NAME_NAME },
				AppList.COLUMN_NAME_SET_ID + "=?",
				new String[] { String.valueOf(setId) }, null, null, null);
	}

	public Cursor getAppsInSet(String setName) {
		return getAppsInSet(getSetId(setName));
	}

	public void saveApplicationSet(String title, List<String> apps, String name)
			throws DuplicateSetException {
		int setId;
		if (name == null) {
			setId = createNewAppSet(title);
		} else {
			setId = setTitle(title, name);
		}
		insertApps(apps);
		linkAppsToSet(setId, apps);
	}

	private int setTitle(String title, String name) {
		int id = getSetId(title);
		if (!title.equals(name)) {
			ContentValues values = new ContentValues();
			values.put(AppSet.COLUMN_NAME_TITLE, title);
			values.put(AppSet._ID, id);
			return (int) getWritableDatabase().insertWithOnConflict(
					AppSet.TABLE_NAME, null, values,
					SQLiteDatabase.CONFLICT_REPLACE);
		}
		return id;
	}

	public int getSetId(String title) {
		Cursor c = getReadableDatabase().query(AppSet.TABLE_NAME,
				new String[] { AppSet._ID }, AppSet.COLUMN_NAME_TITLE + "=?",
				new String[] { title }, null, null, null);
		if (c.moveToFirst()) {
			return c.getInt(c.getColumnIndex(AppSet._ID));
		} else {
			return -1;
		}
	}

	private void linkAppsToSet(int setId, List<String> apps) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		for (String app : apps) {
			Cursor c = db.query(Apps.TABLE_NAME, new String[] { Apps._ID },
					Apps.COLUMN_NAME_NAME + "=?", new String[] { app }, null,
					null, null);
			c.moveToFirst();
			int appId = c.getInt(0);
			values.put(AppList.COLUMN_NAME_APP_ID, appId);
			values.put(AppList.COLUMN_NAME_SET_ID, setId);
			db.insert(AppList.TABLE_NAME, null, values);
		}
	}

	private void insertApps(List<String> apps) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		for (String app : apps) {
			values.put(Apps.COLUMN_NAME_NAME, app);
			db.insertWithOnConflict(Apps.TABLE_NAME, null, values,
					SQLiteDatabase.CONFLICT_IGNORE);
		}
	}

	private int createNewAppSet(String title) throws DuplicateSetException {
		int id = getSetId(title);
		if (id != -1) {
			throw new DuplicateSetException();
		}

		ContentValues values = new ContentValues();
		values.put(AppSet.COLUMN_NAME_TITLE, title);
		return (int) getWritableDatabase().insert(AppSet.TABLE_NAME, null,
				values);
	}
}
