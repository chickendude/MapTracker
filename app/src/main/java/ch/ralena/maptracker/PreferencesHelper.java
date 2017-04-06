package ch.ralena.maptracker;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by crater-windoze on 4/6/2017.
 */

public class PreferencesHelper {
	public static final String MINUTES_BETWEEN_UPDATE = "minutesBetweenUpdate";
	public static final String MOVE_MAP = "moveMap";

	private MapsActivity mMapsActivity;
	private SharedPreferences mSharedPreferences;

	public PreferencesHelper(MapsActivity mapsActivity) {
		mMapsActivity = mapsActivity;
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mMapsActivity);
	}

	public boolean isMoveMap() {
		return mSharedPreferences.getBoolean(MOVE_MAP, true);
	}

	public float getMinutes() {
		String minutes = mSharedPreferences.getString(MINUTES_BETWEEN_UPDATE, null);
		return Float.parseFloat(minutes);
	}

	public SharedPreferences getSharedPreferences() {
		return mSharedPreferences;
	}
}
