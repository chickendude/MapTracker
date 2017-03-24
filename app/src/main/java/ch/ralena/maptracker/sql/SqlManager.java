package ch.ralena.maptracker.sql;

import android.content.Context;

/**
 * Created by crater-windoze on 3/24/2017.
 */

public class SqlManager {
	SqlHelper mSqlHelper;
	public SqlManager(Context context) {
		mSqlHelper = new SqlHelper(context);
	}


}
