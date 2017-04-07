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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import ch.ralena.maptracker.MapsActivity;
import ch.ralena.maptracker.PreferencesHelper;

/**
 * Created by crater-windoze on 4/8/2017.
 */

public class MapLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
	private static final String TAG = MapLocationService.class.getSimpleName();
	private static final int PERMISSION_FINE_LOCATION = 100;
	public static final int RESOLUTION_REQUEST_CONNECTION_FAILURE = 101;

	private IBinder mBinder = new LocalBinder();
	private PreferencesHelper mPreferencesHelper;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;

	PermissionRequestActivity mPermissionRequestActivity;


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "Service Created");
		super.onCreate();
		// initialize variables
		mPermissionRequestActivity = new PermissionRequestActivity();
		mPreferencesHelper = new PreferencesHelper(this);
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		long interval = (long) (mPreferencesHelper.getMinutes() * 1000 * 60);
		mLocationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(interval)
				.setFastestInterval(interval);

	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "Service Bound");
		mGoogleApiClient.connect();
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "Service Unbound");
		mGoogleApiClient.disconnect();
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "Service Destroyed");
		super.onDestroy();
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		if (hasLocationPermission()) {
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
				connectionResult.startResolutionForResult(mPermissionRequestActivity, MapsActivity.RESOLUTION_REQUEST_CONNECTION_FAILURE);
			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			}
		} else {
			Log.d(TAG, "Location services failed to connect! Error code: " + connectionResult.getErrorCode());
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		loadNewPosition(location);
	}

	// local methods

	private boolean hasLocationPermission() {
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

	private void loadNewPosition(Location location) {
		Log.d(TAG, location.toString());
//		mMapsActivity.loadNewPosition(new Position(location.getLatitude(), location.getLongitude(), new Date()));
	}

	// local classes

	public class LocalBinder extends Binder {
		public MapLocationService getService() {
			return MapLocationService.this;
		}
	}

	public class PermissionRequestActivity extends AppCompatActivity {
		@Override
		public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
			// check if we got a result
			if (requestCode == PERMISSION_FINE_LOCATION) {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Add a marker in Sydney and move the camera
					getLocation();
				}
			}
		}
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
