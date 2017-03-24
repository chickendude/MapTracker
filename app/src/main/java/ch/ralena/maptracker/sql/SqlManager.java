package ch.ralena.maptracker.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
		positionValues.put(SqlHelper.COL_POSITION_DATE, position.getDate().getTime());
		positionValues.put(SqlHelper.COL_POSITION_LATITUDE, position.getLatitude());
		positionValues.put(SqlHelper.COL_POSITION_LONGITUDE, position.getLongitude());
		long id = database.insert(SqlHelper.TABLE_POSITION, null, positionValues);
		return id;
	}

}
