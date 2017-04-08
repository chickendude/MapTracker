package ch.ralena.maptracker.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by crater-windoze on 4/8/2017.
 */

public class DateRange implements Parcelable {
	private Calendar mStartDate, mEndDate;

	public DateRange(Calendar startDate, Calendar endDate) {
		mStartDate = startDate;
		mEndDate = endDate;
	}

	public Calendar getStartDate() {
		return mStartDate;
	}

	public Calendar getEndDate() {
		return mEndDate;
	}

	public void setStartDate(Calendar startDate) {
		mStartDate = startDate;
	}

	public void setEndDate(Calendar endDate) {
		mEndDate = endDate;
	}

	protected DateRange(Parcel in) {
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<DateRange> CREATOR = new Creator<DateRange>() {
		@Override
		public DateRange createFromParcel(Parcel in) {
			return new DateRange(in);
		}

		@Override
		public DateRange[] newArray(int size) {
			return new DateRange[size];
		}
	};

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DateRange dateRange = (DateRange) obj;
		return dateRange.getStartDate() == mStartDate && dateRange.getEndDate() == mEndDate;
	}
}
