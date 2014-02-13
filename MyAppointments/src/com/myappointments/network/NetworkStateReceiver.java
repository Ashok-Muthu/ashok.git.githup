package com.myappointments.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 
 * NetworkStateReceiver is a Receiver to get notified when ever changes
 * happening in the network states.
 * 
 */
public class NetworkStateReceiver extends BroadcastReceiver {

	private static NetworkStateReceiver sInstance;
	private NetworkStateListener mListener;
	private static boolean sIsNetworkAvailable = true;
	private static Context sContext;

	private NetworkStateReceiver() {
		sIsNetworkAvailable = isNetworkConnectionAvailable();

	}

	public static NetworkStateReceiver getInstance(Context _context) {
		if (sInstance == null) {
			sContext = _context;
			sInstance = new NetworkStateReceiver();
		}
		return sInstance;
	}

	public void setNetworkListener(NetworkStateListener listener) {
		this.mListener = listener;
		listener.updateNetworkState(sIsNetworkAvailable);
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		sIsNetworkAvailable = isNetworkConnectionAvailable();
		mListener.updateNetworkState(sIsNetworkAvailable);

	}

	/**
	 * isNetworkConnectionAvailable() is used to get the state of Internet connection.
	 *  
	 * @return isAvailable, if "true" Internet is connected and false when Internet is not connected.   
	 */
	private boolean isNetworkConnectionAvailable() {

		boolean isAvailable = false;
		final ConnectivityManager cm = (ConnectivityManager) sContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			final NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isAvailable()) {
				isAvailable = true;
			} else {
				isAvailable = false;
			}
		} else {
			isAvailable = false;
		}

		return isAvailable;
	}

	/**
	 * To get the status of network changes if any. 
	 * @param listener
	 */
	public void registerNetworkBroadcast(NetworkStateListener listener) {
		try {
			IntentFilter filter = new IntentFilter(
					android.net.ConnectivityManager.CONNECTIVITY_ACTION);
			this.mListener = listener;
			sContext.registerReceiver(this, filter);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void unregisterNetworkBrodcast() {
		try {
			sIsNetworkAvailable = false;
			sContext.unregisterReceiver(this);
			sInstance = null;
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public boolean isNetworkAvailable() {
		return sIsNetworkAvailable;
	}

}
