package ch.ralena.maptracker.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import ch.ralena.maptracker.objects.DateRange;
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
		return getPositionsSql("SELECT * FROM " + SqlHelper.TABLE_POSITION);
	}


	public ArrayList<Position> getPositions(DateRange curDateRange) {
		int startDate = (int) (curDateRange.getStartDate().getTimeInMillis()/1000);
		int endDate = (int) (curDateRange.getEndDate().getTimeInMillis()/1000);
		return getPositionsSql("SELECT * FROM " + SqlHelper.TABLE_POSITION +
				String.format(" WHERE %s >= %d AND %s <= %d", SqlHelper.COL_POSITION_DATE, startDate,
						SqlHelper.COL_POSITION_DATE, endDate));
	}

	public ArrayList<Position> getPositionsSql(String query) {
		ArrayList<Position> positions = new ArrayList<>();
		SQLiteDatabase database = mSqlHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				// get column indices
				int latitudeIndex = cursor.getColumnIndex(SqlHelper.COL_POSITION_LATITUDE);
				int longitudeIndex = cursor.getColumnIndex(SqlHelper.COL_POSITION_LONGITUDE);
				int dateIndex = cursor.getColumnIndex(SqlHelper.COL_POSITION_DATE);
				// get cursor values
				double latitude = cursor.getDouble(latitudeIndex);
				double longitude = cursor.getDouble(longitudeIndex);
				Date date = new Date((long)cursor.getInt(dateIndex)*1000);
				// add to ArrayList
				positions.add(new Position(latitude, longitude, date));
			} while (cursor.moveToNext());
		}
		cursor.close();
		database.close();
		return positions;
	}
}
