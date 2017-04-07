package ch.ralena.maptracker.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import ch.ralena.maptracker.PreferencesHelper;

/**
 * Created by crater-windoze on 4/8/2017.
 */

public class MapLocationService extends Service {
	private static final String TAG = MapLocationService.class.getSimpleName();

	private PreferencesHelper mPreferencesHelper;

	@Override
	public void onCreate() {
		Log.d(TAG, "Service Created");
		super.onCreate();
		// initialize variables
		mPreferencesHelper = new PreferencesHelper(this);
		String minutes = mPreferencesHelper.getMinutes() + "";
		Log.d(TAG, minutes);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "Service Bound");
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "Service Unbound");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "Service Destroyed");
		super.onDestroy();
	}

	// Client Methods
}
