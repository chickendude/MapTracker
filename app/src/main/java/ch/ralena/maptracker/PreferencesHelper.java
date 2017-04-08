package ch.ralena.maptracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by crater-windoze on 4/6/2017.
 */

public class PreferencesHelper {
	public static final String MINUTES_BETWEEN_UPDATE = "minutesBetweenUpdate";
	public static final String MOVE_MAP = "moveMap";
	public static final String METERS_BETWEEN_LOCATION = "minMetersForNewLocation";

	private Context mContext;
	private SharedPreferences mSharedPreferences;

	public PreferencesHelper(Context context) {
		mContext = context;
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	public boolean isMoveMap() {
		return mSharedPreferences.getBoolean(MOVE_MAP, true);
	}

	public float getMinutes() {
		String minutes = mSharedPreferences.getString(MINUTES_BETWEEN_UPDATE, "15");
		return Float.parseFloat(minutes);
	}

	public static String getMetersBetweenLocation() {
		return METERS_BETWEEN_LOCATION;
	}

	public SharedPreferences getSharedPreferences() {
		return mSharedPreferences;
	}
}
