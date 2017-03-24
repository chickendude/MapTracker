package ch.ralena.maptracker.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by crater-windoze on 3/24/2017.
 */

public class SqlHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "MapTracker.db";
	public static final int DB_VERSION = 1;

	// table: Position
	public static final String TABLE_POSITION = "POSITION";
	public static final String COL_POSITION_LATITUDE = "LATITUDE";
	public static final String COL_POSITION_LONGITUDE = "LONGITUDE";
	public static final String COL_POSITION_DATE = "DATE";

	// create tables
	private static final String CREATE_POSITION =
			"CREATE TABLE " + TABLE_POSITION +
					"( " + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COL_POSITION_DATE + " INTEGER, " +
					COL_POSITION_LATITUDE + " REAL, " +
					COL_POSITION_LONGITUDE + " REAL " +
					" )";

	public SqlHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL(CREATE_POSITION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		switch(oldVersion) {
			default:
				break;
		}
	}
}
