package ch.ralena.maptracker.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import ch.ralena.maptracker.R;
import ch.ralena.maptracker.objects.DateRange;

/**
 * Created by crater-windoze on 3/18/2017.
 */

public class FilterFragment extends DialogFragment {
	public static final String BUNDLE_DATERANGE = "bundle";
	private static final String TAG = FilterFragment.class.getSimpleName();

	public interface FilterDateChangeListener {
		void onDateChanged();
	}

	private TextView mStartDateLabel,mEndDateLabel;
	private DateRange mDateRange;
	private Calendar mStartDate,mEndDate;
	private FilterDateChangeListener mDateChangeListener;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mDateChangeListener = (FilterDateChangeListener) context;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mDateChangeListener = (FilterDateChangeListener) activity;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		getDialog().setCanceledOnTouchOutside(true);
		View view = inflater.inflate(R.layout.fragment_filter, container);

		Bundle bundle = getArguments();

		mDateRange = bundle.getParcelable(BUNDLE_DATERANGE);

		mStartDateLabel = (TextView) view.findViewById(R.id.startDateLabel);
		mEndDateLabel = (TextView) view.findViewById(R.id.endDateLabel);

		mStartDate = mDateRange.getStartDate();
		mEndDate = mDateRange.getEndDate();
		updateDateTexts();
		mStartDateLabel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker datePicker, int year, int month, int day) {
						mDateChangeListener.onDateChanged();
						mStartDate.set(year, month, day);
						if (mStartDate.compareTo(mEndDate) == 1) {
							mEndDate.set(year, month, day);
						}
						updateDateTexts();
					}
				}, mStartDate.get(Calendar.YEAR), mStartDate.get(Calendar.MONTH), mStartDate.get(Calendar.DAY_OF_MONTH));
				datePickerDialog.show();
			}
		});
		mEndDateLabel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker datePicker, int year, int month, int day) {
						mDateChangeListener.onDateChanged();
						mEndDate.set(year, month, day, 23, 59, 59);
						if (mStartDate.compareTo(mEndDate) == 1) {
							mEndDate.set(year, month, day);
						}
						updateDateTexts();
					}
				}, mEndDate.get(Calendar.YEAR), mEndDate.get(Calendar.MONTH), mEndDate.get(Calendar.DAY_OF_MONTH));
				datePickerDialog.show();
			}
		});

		return view;
	}

	void updateDateTexts() {
		mStartDateLabel.setText(String.format(Locale.US, "%d-%d-%d",
				mStartDate.get(Calendar.MONTH) + 1,
				mStartDate.get(Calendar.DAY_OF_MONTH),
				mStartDate.get(Calendar.YEAR)));

		mEndDateLabel.setText(String.format(Locale.US, "%d-%d-%d",
				mEndDate.get(Calendar.MONTH) + 1,
				mEndDate.get(Calendar.DAY_OF_MONTH),
				mEndDate.get(Calendar.YEAR)));
	}
}
