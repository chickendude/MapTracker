package ch.ralena.maptracker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import ch.ralena.maptracker.fragments.SettingsFragment;
import ch.ralena.maptracker.service.MapLocationService;

import static ch.ralena.maptracker.PreferencesHelper.METERS_BETWEEN_LOCATION;
import static ch.ralena.maptracker.PreferencesHelper.MINUTES_BETWEEN_UPDATE;

/**
 * Created by crater-windoze on 3/31/2017.
 */

public class SettingsActivity extends PreferenceActivity
		implements SharedPreferences.OnSharedPreferenceChangeListener {
	private static final String TAG = SettingsActivity.class.getSimpleName();

	private boolean mIsBound;
	private MapLocationService mMapLocationService;
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
			mIsBound = true;
			MapLocationService.LocalBinder localBinder = (MapLocationService.LocalBinder) iBinder;
			mMapLocationService = localBinder.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mIsBound = false;
		}
	};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIsBound = false;
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		pref.registerOnSharedPreferenceChangeListener(this);
		Intent intent = new Intent(this, MapLocationService.class);
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mIsBound) {
			unbindService(mServiceConnection);
			mIsBound = false;
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d(TAG, "Settings changed");
		if (key.equals(MINUTES_BETWEEN_UPDATE)) {
			float locationRequestInterval = Float.parseFloat(sharedPreferences.getString(MINUTES_BETWEEN_UPDATE, "15"));
			Log.d(TAG, locationRequestInterval + " minutes");
			mMapLocationService.setLocationRequestInterval(locationRequestInterval);
		} else if (key.equals(METERS_BETWEEN_LOCATION)) {
			long smallestDisplacement = Long.parseLong(sharedPreferences.getString(METERS_BETWEEN_LOCATION, "15"));
			Log.d(TAG, smallestDisplacement + " meters");
			mMapLocationService.setSmallestDisplacement(smallestDisplacement);
		}
	}
}
