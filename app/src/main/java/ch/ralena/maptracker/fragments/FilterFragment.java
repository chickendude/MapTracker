package ch.ralena.maptracker.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.ralena.maptracker.R;

/**
 * Created by crater-windoze on 3/18/2017.
 */

public class FilterFragment extends DialogFragment {

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		getDialog().setCanceledOnTouchOutside(true);
		View view = inflater.inflate(R.layout.fragment_filter, container);
		return view;
	}
}
