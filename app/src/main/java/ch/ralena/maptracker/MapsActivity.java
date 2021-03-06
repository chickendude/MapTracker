package ch.ralena.maptracker;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ch.ralena.maptracker.fragments.FilterFragment;
import ch.ralena.maptracker.objects.DateRange;
import ch.ralena.maptracker.objects.Position;
import ch.ralena.maptracker.service.MapLocationService;
import ch.ralena.maptracker.sql.SqlManager;

public class MapsActivity extends Activity implements
		OnMapReadyCallback, FilterFragment.FilterDateChangeListener {

	public static final String TAG = MapsActivity.class.getSimpleName();
	private static final int PERMISSION_FINE_LOCATION = 100;
	private static final String TAG_FILTER_FRAGMENT = "tag_filter_fragment";
	private static final String BUNDLE_LOCATION_SERVICE = "bundle_location_service";

	private GoogleMap mMap;
	private PreferencesHelper mPreferencesHelper;

	private SqlManager mSqlManager;
	private ArrayList<Position> mPositions;
	private boolean mIsBound;
	private DateRange mCurDateRange;
	private MapLocationService mMapLocationService;
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
			Log.d(TAG, "Service connected");
			mIsBound = true;
			MapLocationService.LocalBinder localBinder = (MapLocationService.LocalBinder) iBinder;
			mMapLocationService = localBinder.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mIsBound = false;
		}
	};
	// message receiver
	private BroadcastReceiver mBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		mPreferencesHelper = new PreferencesHelper(this);
		mSqlManager = new SqlManager(this);
		mPositions = mSqlManager.getPositions();
		mIsBound = false;
		Intent intent = new Intent(this, MapLocationService.class);
		startService(intent);
		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Position position = intent.getParcelableExtra(MapLocationService.EXTRA_LOCATION);
				loadNewPosition(position);
			}
		};
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(MapLocationService.INTENT_LOCATION_RECEIVED));

		setUpButtons();

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
		checkLocationPermission();
		Intent intent = new Intent(this, MapLocationService.class);
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
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
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mMapLocationService != null) {
			mMapLocationService.killService();
		}
	}

	private void setUpButtons() {
		TextView settingsText = (TextView) findViewById(R.id.settingsTextView);
		TextView filterText = (TextView) findViewById(R.id.filterTextView);

		settingsText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MapsActivity.this, SettingsActivity.class);
				startActivity(intent);
			}
		});

		filterText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FilterFragment filterFragment = new FilterFragment();
				Bundle bundle = new Bundle();
				bundle.putParcelable(FilterFragment.BUNDLE_DATERANGE, mCurDateRange);
				filterFragment.setArguments(bundle);
				filterFragment.show(getFragmentManager(), TAG_FILTER_FRAGMENT);
			}
		});
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
		// allow adapter to have multi-line snippet
		mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
			@Override
			public View getInfoWindow(Marker arg0) {
				return null;
			}

			@Override
			public View getInfoContents(Marker marker) {
				LinearLayout view = new LinearLayout(MapsActivity.this);
				view.setOrientation(LinearLayout.VERTICAL);

				TextView title = new TextView(MapsActivity.this);
				title.setTextColor(Color.BLACK);
				title.setGravity(Gravity.CENTER);
				title.setTypeface(null, Typeface.BOLD);
				title.setText(marker.getTitle());

				TextView snippet = new TextView(MapsActivity.this);
				snippet.setTextColor(Color.GRAY);
				snippet.setText(marker.getSnippet());

				view.addView(title);
				view.addView(snippet);

				return view;
			}
		});
		mCurDateRange = getCurDateRange();
		loadMarkers();
	}

	public void checkLocationPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
					&& checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG, "Ask for permissions!");
				requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		Log.d(TAG, "Permission result!");

		// check if we got a result
		if (requestCode == PERMISSION_FINE_LOCATION) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				if (mIsBound) {
					mMapLocationService.getLocation();
				}
			}
		}
	}

	// run when we get a new position from LocationHelper
	public void loadNewPosition(Position position) {
		// add position to ArrayList
		mPositions.add(position);
		// add to map
		addMapMarker(position);
		// check whether we should move the map or not
		if (mPreferencesHelper.isMoveMap()) {
			LatLng latLng = new LatLng(position.getLatitude(), position.getLongitude());
			mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		}
	}

	private DateRange getCurDateRange() {
		Log.d(TAG, "getCurDateRange");
		// create daterange
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		if (mPositions.size() > 0) {
			Log.d(TAG, "Loading dates from mPosition");
			Date start = mPositions.get(0).getDate();
			Date end = mPositions.get(mPositions.size()-1).getDate();
			startDate.setTime(start);
			endDate.setTime(end);
		}
		// set to start and end of day
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		endDate.set(Calendar.HOUR_OF_DAY, 23);
		endDate.set(Calendar.MINUTE, 59);
		endDate.set(Calendar.SECOND, 59);
		return new DateRange(startDate, endDate);
	}

	public void loadMarkers() {
		String curDate = SimpleDateFormat.getDateTimeInstance().format(mCurDateRange.getStartDate().getTime()) + " - " + SimpleDateFormat.getDateTimeInstance().format(mCurDateRange.getEndDate().getTime());
		Log.d(TAG, "Current filter range is: " + curDate);
		for (Position position : mPositions) {

			if (position.getDate().after(mCurDateRange.getStartDate().getTime())
					&& position.getDate().before(mCurDateRange.getEndDate().getTime())) {
				// Add a marker
				addMapMarker(position);
			}
		}
	}

	private void addMapMarker(Position position) {
		// format date to local time format
		String date = SimpleDateFormat.getDateTimeInstance().format(position.getDate());
		// Add a marker and move the camera there
		LatLng latLng = new LatLng(position.getLatitude(), position.getLongitude());
		mMap.addMarker(new MarkerOptions()
				.position(latLng)
				.title("Here on " + date)
				.snippet("Latitude: " + position.getLatitude() + "\nLongitude: " + position.getLongitude()));
	}

	@Override
	public void onDateChanged() {
		Log.d(TAG, "Date changed!");
//		mPositions = mSqlManager.getPositions(mCurDateRange);
		mMap.clear();
		loadMarkers();
	}
}
