package ch.ralena.maptracker.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

import ch.ralena.maptracker.PreferencesHelper;
import ch.ralena.maptracker.objects.Position;
import ch.ralena.maptracker.sql.SqlManager;

/**
 * Created by crater-windoze on 4/8/2017.
 */

public class MapLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
	private static final String TAG = MapLocationService.class.getSimpleName();
	public static final int RESOLUTION_REQUEST_CONNECTION_FAILURE = 101;
	public static String INTENT_LOCATION_RECEIVED = "location_received";
	public static final String EXTRA_LOCATION = "location";

	private IBinder mBinder = new LocalBinder();
	private PreferencesHelper mPreferencesHelper;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private Location mPreviousLocation;
	private SqlManager mSqlManager;

	PermissionRequestActivity mPermissionRequestActivity;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Service started");
		return Service.START_STICKY;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "Service Created");
		super.onCreate();
		// initialize variables
		mSqlManager = new SqlManager(this);
		mPermissionRequestActivity = new PermissionRequestActivity();
		mPreferencesHelper = new PreferencesHelper(this);
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		long interval = (long) (mPreferencesHelper.getMinutes() * 1000 * 60);
		interval = 1000;
		mLocationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(interval)
				.setFastestInterval(interval);
		mGoogleApiClient.connect();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "Service Bound");
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "Service Unbound");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "Service Destroyed");
		mGoogleApiClient.disconnect();
		super.onDestroy();
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		if (hasLocationPermission()) {
			Log.d(TAG, "Has location permissions");
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
			getLocation();
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(TAG, "Location services suspended!");
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(mPermissionRequestActivity, RESOLUTION_REQUEST_CONNECTION_FAILURE);
			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			}
		} else {
			Log.d(TAG, "Location services failed to connect! Error code: " + connectionResult.getErrorCode());
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "Location changed");
		loadNewPosition(location);
	}

	// local methods

	private boolean hasLocationPermission() {
		// if we are below API 23, we already have permission from when the app was installed
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
					&& checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG, "No permissions!");
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}


	static int number = 0;

	private void loadNewPosition(Location location) {
		Log.d(TAG, location.toString());
		Position position = new Position(location.getLatitude(), location.getLongitude(), new Date());
		position.getDate().setTime(position.getDate().getTime() + number++ * 1000 * 60 * 60 * 12);
		mSqlManager.insertPosition(position);

		Intent intent = new Intent(INTENT_LOCATION_RECEIVED);
		intent.putExtra(EXTRA_LOCATION, position);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	// local classes

	public class LocalBinder extends Binder {
		public MapLocationService getService() {
			return MapLocationService.this;
		}
	}

	public class PermissionRequestActivity extends AppCompatActivity {

	}


	// Client Methods
	public void killService() {
		stopSelf();
	}

	public void getLocation() {
		Log.d(TAG, "Request location");
		if (hasLocationPermission()) {
			Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
			if (location != null) {
				// create position object
				loadNewPosition(location);
			}
		}
	}
}
