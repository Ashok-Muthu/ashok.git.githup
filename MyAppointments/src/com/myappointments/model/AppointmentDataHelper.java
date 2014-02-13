package com.myappointments.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * AppointmentDataHelper is a singleton class used to get the appointment data mapping for all the days.  
 *
 */
public class AppointmentDataHelper {

	private static AppointmentDataHelper sInstance;

	private AppointmentDataHelper() {

	}

	public static AppointmentDataHelper getInstance() {
		if (sInstance == null) {
			sInstance = new AppointmentDataHelper();
		}
		return sInstance;
	}

	private HashMap<String, ArrayList<AppointmentsData>> mAppoinmentDataMap;

	public HashMap<String, ArrayList<AppointmentsData>> getAppoinmentDataMap() {
		return mAppoinmentDataMap;
	}

	public void setAppoinmentDataMap(
			HashMap<String, ArrayList<AppointmentsData>> mAppoinmentDataMap) {
		this.mAppoinmentDataMap = mAppoinmentDataMap;
	}

}
