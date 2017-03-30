package ch.ralena.maptracker.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import ch.ralena.maptracker.objects.Position;

/**
 * Created by crater-windoze on 3/24/2017.
 */

public class SqlManager {
	SqlHelper mSqlHelper;
	public SqlManager(Context context) {
		mSqlHelper = new SqlHelper(context);
	}

	public long insertPosition(Position position) {
		SQLiteDatabase database = mSqlHelper.getWritableDatabase();
		// create ContentValues
		ContentValues positionValues = new ContentValues();
		long seconds = position.getDate().getTime()/1000;
		positionValues.put(SqlHelper.COL_POSITION_DATE, position.getDate().getTime()/1000);
		positionValues.put(SqlHelper.COL_POSITION_LATITUDE, position.getLatitude());
		positionValues.put(SqlHelper.COL_POSITION_LONGITUDE, position.getLongitude());
		long id = database.insert(SqlHelper.TABLE_POSITION, null, positionValues);
		database.close();
		return id;
	}

	public ArrayList<Position> getPositions() {
		ArrayList<Position> positions = new ArrayList<>();
		SQLiteDatabase database = mSqlHelper.getWritableDatabase();

		Cursor itemCursor = database.rawQuery("SELECT * FROM " + SqlHelper.TABLE_POSITION, null);
		if (itemCursor.moveToFirst()) {
			do {
				long latitude = itemCursor.getLong(itemCursor.getColumnIndex(SqlHelper.COL_POSITION_LATITUDE));
				long longitude = itemCursor.getLong(itemCursor.getColumnIndex(SqlHelper.COL_POSITION_LONGITUDE));
				Date date = new Date((long)itemCursor.getInt(itemCursor.getColumnIndex(SqlHelper.COL_POSITION_DATE))*1000);
				positions.add(new Position(latitude, longitude, date));
			} while (itemCursor.moveToNext());
		}
		itemCursor.close();

		database.close();
		return positions;
	}



}
