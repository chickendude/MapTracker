package ch.ralena.maptracker;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ch.ralena.maptracker.fragments.FilterFragment;
import ch.ralena.maptracker.objects.Position;
import ch.ralena.maptracker.sql.SqlManager;

public class MapsActivity extends Activity implements
		OnMapReadyCallback {

	public static final String TAG = MapsActivity.class.getSimpleName();
	private static final int PERMISSION_FINE_LOCATION = 100;
	public static final int RESOLUTION_REQUEST_CONNECTION_FAILURE = 101;
	private static final String TAG_FILTER_FRAGMENT = "tag_filter_fragment";

	private GoogleMap mMap;
	private LocationHelper mLocationHelper;

	private SqlManager mSqlManager;
	private ArrayList<Position> mPositions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		mSqlManager = new SqlManager(this);
		mPositions = new ArrayList<>();

		TextView settingsText = (TextView) findViewById(R.id.settingsTextView);
		TextView filterText = (TextView) findViewById(R.id.filterTextView);

		settingsText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(MapsActivity.this, "Load Settings Page", Toast.LENGTH_SHORT).show();
			}
		});

		filterText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FilterFragment filterFragment = new FilterFragment();
				filterFragment.show(getFragmentManager(), TAG_FILTER_FRAGMENT);
			}
		});

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		mLocationHelper = new LocationHelper(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocationHelper.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationHelper.disconnect();
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

	public boolean hasLocationPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
			&& checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
			{
				Log.d(TAG, "Ask for permissions!");
				requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		Log.d(TAG, "Permission result!");

		// check if we got a result
		if (requestCode == PERMISSION_FINE_LOCATION) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Add a marker in Sydney and move the camera
				mLocationHelper.getLocation();
			}
		}
	}

	// run when we get a new position from LocationHelper
	public void loadNewPosition(Position position) {
		mPositions.add(position);
		// format date to local time format
		String date = SimpleDateFormat.getDateTimeInstance().format(position.getDate());
		// Add a marker and move the camera there
		LatLng latLng = new LatLng(position.getLatitude(), position.getLongitude());
		mMap.addMarker(new MarkerOptions().position(latLng).title("Here on " + date));
		mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
	}
}
