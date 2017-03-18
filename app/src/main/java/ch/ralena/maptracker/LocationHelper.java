package ch.ralena.maptracker;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

import ch.ralena.maptracker.objects.Position;

/**
 * Created by crater-windoze on 3/18/2017.
 */

public class LocationHelper implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		LocationListener {

	// constants
	public static final String TAG = LocationHelper.class.getSimpleName();

	private MapsActivity mMapsActivity;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;


	public LocationHelper(MapsActivity mapsActivity) {
		mMapsActivity = mapsActivity;
		// set up API client
		mGoogleApiClient = new GoogleApiClient.Builder(mMapsActivity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();

		// set up location request stuff
		// todo: pull from preferences
		mLocationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(10 * 1000)
				.setFastestInterval(1 * 1000);
	}


	public void getLocation() {
		if(mMapsActivity.hasLocationPermission()) {
			// check if we can already pull a location from the service
			Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
			if (location != null) {
				// create position object
				loadNewPosition(location);
			}
		}
	}

	private void loadNewPosition(Location location) {
		mMapsActivity.loadNewPosition(new Position(location.getLatitude(), location.getLongitude(), new Date()));
	}


	@Override
	public void onConnected(@Nullable Bundle bundle) {
		// set up location update request
		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
		getLocation();
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(TAG, "Location services suspended!");
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(mMapsActivity, MapsActivity.RESOLUTION_REQUEST_CONNECTION_FAILURE);
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

	public void connect() {
		mGoogleApiClient.connect();
	}

	public void disconnect() {
		mGoogleApiClient.disconnect();
	}
}
