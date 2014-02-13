package com.myappointments.model;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

public class LocationHelper {

	private static LocationHelper sInstance;
	private float mLatitude;
	private float mLongitude;
	private boolean mIsLocationAvailable;
	private LocationListenerHelper mLocListenerHelper;
	private LocationManager mLocationManager;
	private Context mContext;

	private LocationHelper(Context context) {

		if (sInstance == null) {
			mContext = context;
			mLocListenerHelper = new LocationListenerHelper();
			mLocationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, mLocListenerHelper);
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, mLocListenerHelper);
		}
	}

	public static LocationHelper getLocationHelperInstance(Context context) {

		if (sInstance == null) {
			sInstance = new LocationHelper(context);
		}
		return sInstance;

	}

	public float getLattitude() {
		return mLatitude;
	}

	public void setmLattitude(float mLattitude) {
		this.mLatitude = mLattitude;
	}

	public float getmangitude() {
		return mLongitude;
	}

	public void setmLangitude(float mLangitude) {
		this.mLongitude = mLangitude;
	}

	public boolean isIsLocationAvailable() {
		return mIsLocationAvailable;
	}

	public void setmIsLocationAvailable(boolean mIsLocationAvailable) {
		this.mIsLocationAvailable = mIsLocationAvailable;
	}

	public boolean isGPSEnabled() {

		/**
		 * Since it has using contentresolver it could be heavy . so we can able
		 * to fine with LocationManager itself.
		 */
		// boolean isGPSEnabled = false;
		// String locationProviders = Settings.Secure.getString(
		// mContext.getContentResolver(),
		// Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		// if (locationProviders.contains("gps")
		// || locationProviders.contains("network")) {
		// isGPSEnabled = true;
		// }

		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| mLocationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			return true;
		}
		return false;
	}

	class LocationListenerHelper implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			mIsLocationAvailable = true;
			mLatitude = (float) location.getLatitude();
			mLongitude = (float) location.getLongitude();
			// now we have our location we can stop the service from sending
			// updates
			// comment out this line if you want the service to continue
			// updating the users location
			mLocationManager.removeUpdates(mLocListenerHelper);

		}

		@Override
		public void onProviderDisabled(String provider) {
			mIsLocationAvailable = false;

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	}

}
