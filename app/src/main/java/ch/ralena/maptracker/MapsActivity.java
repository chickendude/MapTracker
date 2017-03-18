package ch.ralena.maptracker;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

import ch.ralena.maptracker.objects.Position;

public class MapsActivity extends FragmentActivity implements
		OnMapReadyCallback,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		LocationListener {

	public static final String TAG = MapsActivity.class.getSimpleName();

	private static final int PERMISSION_FINE_LOCATION = 100;
	public static final int RESOLUTION_REQUEST_CONNECTION_FAILURE = 101;

	private GoogleMap mMap;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();

		mLocationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(10 * 1000)
				.setFastestInterval(1 * 1000);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGoogleApiClient.disconnect();
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		Log.d(TAG, "onMapReady");
		mMap = googleMap;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		Log.d(TAG, "Permission result!");

		// check if we got a result
		if (requestCode == PERMISSION_FINE_LOCATION) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Add a marker in Sydney and move the camera
				getLocation();
			}
		}
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Log.d(TAG, "Location services connected!");
		getLocation();
	}

	private void getLocation() {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			Log.d(TAG, "Ask for permissions!");
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
			return;
		}
		// set up location update request
		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
		// check if we can already pull a location from the service
		Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (location != null) {
			loadNewLocation(location);
		}
	}

	private void loadNewLocation(Location location) {
		Position position = new Position(location.getLatitude(), location.getLongitude(), new Date());
		String date = new SimpleDateFormat("MM-dd-yyyy").format(position.getDate());
		Log.d(TAG, date);
		// Add a marker in Sydney and move the camera
		LatLng latLng = new LatLng(position.getLatitude(), position.getLongitude());
		mMap.addMarker(new MarkerOptions().position(latLng).title("Here on " + date));
		mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(TAG, "Location services suspended!");
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this, RESOLUTION_REQUEST_CONNECTION_FAILURE);
			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			}
		} else {
			Log.d(TAG, "Location services failed to connect! Error code: " + connectionResult.getErrorCode());
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		loadNewLocation(location);
	}
}
