package com.idr.trvlr.sqlite;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TrainDbOpenHelper extends SQLiteOpenHelper implements Serializable {

	public static final String TABLE_ROUTES = "routes";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ROUTE_NAME = "routename";
	public static final String TABLE_GEOHASHES = "geohashes";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_GEOHASH = "geohash";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_SCHEDULED_TIME = "scheduletime";
	public static final String COLUMN_ACTUAL_TIME = "actualtime";
	public static final String DISTANCE_FROM_PREV_STOP="distance";
	public static final String INCLUDE_IN_TRIP="include";

	static final String GEOHASH_DATABASE_CREATE = "create table "
			+ TABLE_GEOHASHES + "( " + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_DESCRIPTION
			+ " varchar(100) DEFAULT NULL, "+ COLUMN_LATITUDE
			+ " decimal(7,7) DEFAULT NULL, "+ COLUMN_LONGITUDE
			+ " decimal(7,7) DEFAULT NULL, "+ COLUMN_GEOHASH
			+ " varchar(50) DEFAULT NULL, "+ COLUMN_SCHEDULED_TIME
			+ " numeric DEFAULT NULL, "+ COLUMN_ACTUAL_TIME
			+ " numeric DEFAULT NULL, "+ DISTANCE_FROM_PREV_STOP
			+ " numeric DEFAULT NULL, "+ INCLUDE_IN_TRIP
			+ " numeric DEFAULT 0, "+ COLUMN_ROUTE_NAME
			+ " varchar(100) not null);";

	public static String DB_PATH;
	public static String DB_NAME;
	public SQLiteDatabase database;
	public final Context context;
	private boolean newDBCreated = false;

	public SQLiteDatabase getDb() {
		return database;
	}


	public TrainDbOpenHelper(Context context, String databaseName) {
		super(context, databaseName, null, 1);
		this.context = context;
		String packageName = context.getPackageName();
		DB_PATH = String.format("//data//data//%s//databases//", packageName);
		DB_NAME = databaseName;
		database = openDataBase();
		if(newDBCreated) {
			onCreate(database);
		}
	}

	public void createDataBase() {
		boolean dbExist = checkDataBase();
		if (!dbExist) {
			this.getReadableDatabase();
			try {
				copyDataBase();
				newDBCreated = true;
			} catch (IOException e) {
				Log.e(this.getClass().toString(), "Copying error");
				throw new Error("Error copying database!");
			}
		} else {
			Log.i(this.getClass().toString(), "Database already exists");
		}
	}

	private boolean checkDataBase() {
		SQLiteDatabase checkDb = null;
		try {
			String path = DB_PATH + DB_NAME;
			checkDb = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READWRITE);
		} catch (SQLException e) {
			//Log.e(this.getClass().toString(), "Error while checking db");
		}

		if (checkDb != null) {
			checkDb.close();
		}
		return checkDb != null;
	}

	private void copyDataBase() throws IOException {
		InputStream externalDbStream = context.getAssets().open(DB_NAME);

		String outFileName = DB_PATH + DB_NAME;

		OutputStream localDbStream = new FileOutputStream(outFileName);

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = externalDbStream.read(buffer)) > 0) {
			localDbStream.write(buffer, 0, bytesRead);
		}
		localDbStream.close();
		externalDbStream.close();

	}

	public SQLiteDatabase openDataBase() throws SQLException {
		String path = DB_PATH + DB_NAME;
		if (database == null) {
			createDataBase();
		}
		if(database == null || !database.isOpen()) {
			database = getWritableDatabase();
		}
		return database;
	}

	@Override
	public synchronized void close() {
		if (database != null) {
			database.close();
		}
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TrainDbOpenHelper.TABLE_GEOHASHES);
		db.execSQL(TrainDbOpenHelper.GEOHASH_DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	
	
}
