package ch.ralena.maptracker.objects;

import java.util.Date;

/**
 * Created by crater-windoze on 3/18/2017.
 */

public class Position {
	private double mLatitude;
	private double mLongitude;
	private Date mDate;

	public Position(double latitude, double longitude, Date date) {
		mLatitude = latitude;
		mLongitude = longitude;
		mDate = date;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double latitude) {
		mLatitude = latitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double longitude) {
		mLongitude = longitude;
	}
}
