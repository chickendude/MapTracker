package ch.ralena.maptracker.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by crater-windoze on 3/18/2017.
 */

public class Position implements Parcelable {
	private double mLatitude;
	private double mLongitude;
	private Date mDate;
	private boolean mIsVisible;

	public Position(double latitude, double longitude, Date date) {
		mLatitude = latitude;
		mLongitude = longitude;
		mDate = date;
	}

	protected Position(Parcel in) {
		mLatitude = in.readDouble();
		mLongitude = in.readDouble();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(mLatitude);
		dest.writeDouble(mLongitude);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<Position> CREATOR = new Creator<Position>() {
		@Override
		public Position createFromParcel(Parcel in) {
			return new Position(in);
		}

		@Override
		public Position[] newArray(int size) {
			return new Position[size];
		}
	};

	public boolean isVisible() {
		return mIsVisible;
	}

	public void setVisible(boolean visible) {
		mIsVisible = visible;
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

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}
}
