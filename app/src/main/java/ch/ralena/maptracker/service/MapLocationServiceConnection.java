package ch.ralena.maptracker.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by crater-windoze on 4/8/2017.
 */

public class MapLocationServiceConnection implements ServiceConnection {
	private boolean mBound = false;

	@Override
	public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
		mBound = true;
	}

	@Override
	public void onServiceDisconnected(ComponentName componentName) {
		mBound = false;
	}

	public void setIsBound(boolean bound) {
		mBound = bound;
	}

	public boolean isBound() {
		return mBound;
	}
}
