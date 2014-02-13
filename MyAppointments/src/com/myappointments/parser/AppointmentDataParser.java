package com.myappointments.parser;

import java.util.ArrayList;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.myappointments.model.AppointmentsData;

/**
 * 
 * Json Parser class for to parse the data of appointment feed.
 *
 */
public class AppointmentDataParser {
	private final Object mData;
	private static final String TAG_OBJ_DAYS = "days";
	private static final String TAG_ATTR_NAME = "name";
	private static final String TAG_ATTR_TIME = "time";
	private static final String TAG_ATTR_PHONE = "phone";
	private static final String TAG_ATTR_ADDRESS = "address";
	private static final String TAG_ATTR_CITY = "city";

	public AppointmentDataParser(Object data) {
		mData = data;
	}

	/**
	 * parseDataByJson() is used to parse the data AppointmentsData.
	 * 
	 * @return list of AppointmentsData with key value pairs.  
	 * @throws JSONException
	 */
	public Object parseDataByJson() throws JSONException {

		HashMap<String, ArrayList<AppointmentsData>> appointmentDatamap = null;

		JSONObject jsonObj = new JSONObject(mData.toString());

		JSONObject jsonObjectDays = jsonObj.getJSONObject(TAG_OBJ_DAYS);

		JSONArray jsonArrayObjNames = jsonObjectDays.names();

		if (jsonArrayObjNames != null) {

			int jsonArrayObjNamesSize = jsonArrayObjNames.length();

			appointmentDatamap = new HashMap<String, ArrayList<AppointmentsData>>();

			for (int i = 0; i < jsonArrayObjNamesSize; i++) {

				JSONArray jsonArrayObjDay = jsonObjectDays
						.getJSONArray(jsonArrayObjNames.getString(i));

				ArrayList<AppointmentsData> daysDataList = null;

				if (jsonArrayObjDay != null) {

					int size = jsonArrayObjDay.length();

					daysDataList = new ArrayList<AppointmentsData>();

					for (int j = 0; j < size; j++) {

						AppointmentsData dataObj = new AppointmentsData();
						dataObj.setName(jsonArrayObjDay.getJSONObject(j)
								.getString(TAG_ATTR_NAME));
						dataObj.setTime(jsonArrayObjDay.getJSONObject(j)
								.getString(TAG_ATTR_TIME));
						dataObj.setPhone(jsonArrayObjDay.getJSONObject(j)
								.getString(TAG_ATTR_PHONE));
						dataObj.setAddress(jsonArrayObjDay.getJSONObject(j)
								.getString(TAG_ATTR_ADDRESS));
						dataObj.setCity(jsonArrayObjDay.getJSONObject(j)
								.getString(TAG_ATTR_CITY));
						daysDataList.add(dataObj);

					}
				}
				appointmentDatamap.put(jsonArrayObjNames.getString(i),
						daysDataList);
			}
		}
		return appointmentDatamap;
	}
}
