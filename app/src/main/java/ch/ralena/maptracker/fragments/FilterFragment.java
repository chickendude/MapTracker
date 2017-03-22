package ch.ralena.maptracker.fragments;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
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

/**
 * Created by crater-windoze on 3/18/2017.
 */

public class FilterFragment extends DialogFragment {

	TextView mStartDateLabel, mEndDateLabel;
	Calendar mStartDate, mEndDate;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		getDialog().setCanceledOnTouchOutside(true);
		View view = inflater.inflate(R.layout.fragment_filter, container);

		mStartDateLabel = (TextView) view.findViewById(R.id.startDateLabel);
		mEndDateLabel = (TextView) view.findViewById(R.id.endDateLabel);

		mStartDate = Calendar.getInstance();
		mEndDate = Calendar.getInstance();
		updateDateTexts();


		mStartDateLabel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker datePicker, int year, int month, int day) {
						mStartDate.set(year, month, day);
						if (mStartDate.compareTo(mEndDate) == 1) {
							mEndDate.set(year, month, day);
						}
						updateDateTexts();
					}
				}, mStartDate.get(Calendar.YEAR), mStartDate.get(Calendar.MONTH), mStartDate.get(Calendar.DAY_OF_MONTH));
				datePickerDialog.setTitle("Select Start Date");
				datePickerDialog.show();
			}
		});
		mEndDateLabel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker datePicker, int year, int month, int day) {
						mEndDate.set(year, month, day);
						if (mStartDate.compareTo(mEndDate) == 1) {
							mStartDate.set(year, month, day);
						}
						updateDateTexts();
					}
				}, mStartDate.get(Calendar.YEAR), mStartDate.get(Calendar.MONTH), mStartDate.get(Calendar.DAY_OF_MONTH));
				datePickerDialog.setTitle("Select Start Date");
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
