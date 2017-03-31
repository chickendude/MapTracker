package ch.ralena.maptracker;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import ch.ralena.maptracker.fragments.SettingsFragment;

/**
 * Created by crater-windoze on 3/31/2017.
 */

public class SettingsActivity extends Activity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	}
}
