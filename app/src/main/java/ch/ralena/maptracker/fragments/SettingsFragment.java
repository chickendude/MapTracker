package ch.ralena.maptracker.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import ch.ralena.maptracker.R;

/**
 * Created by crater-windoze on 3/31/2017.
 */

public class SettingsFragment extends PreferenceFragment {
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}
