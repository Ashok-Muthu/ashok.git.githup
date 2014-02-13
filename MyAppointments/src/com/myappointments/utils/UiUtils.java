package com.myappointments.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * 
 * Utility class to having general static methods for reuse.  
 *
 */
public class UiUtils {

	public static final int CALENDAR_EVENT_NOT_EXISTS = 0;
	public static final int CALENDAR_EVENT_EXISTS = 1;
	public static final int CALENDAR_EVENT_ADDING_FAILURE = 2;

	public static int getCalendarManagedCursor(Context mContext,
			String[] projection, String selection, String path, String column,
			String data) {
		Cursor managedCursor = null;
		Uri calendars = Uri.parse(getCalendarUriBase(mContext) + path);
		int res = CALENDAR_EVENT_NOT_EXISTS;

		try {
			managedCursor = mContext.getContentResolver().query(calendars,
					projection, selection, null, null);

			if (managedCursor != null && managedCursor.getCount() > 0) {

				while (managedCursor.moveToNext()) {
					if (managedCursor.getColumnIndex(column) != -1
							&& ((managedCursor.getString(managedCursor
									.getColumnIndex(column)) != null))) {

						if (managedCursor.getString(
								managedCursor.getColumnIndex(column)).equals(
								data)) {
							res = CALENDAR_EVENT_EXISTS;
							break;
						}
					}

				}

			}

		} catch (IllegalArgumentException e) {
			res = CALENDAR_EVENT_ADDING_FAILURE;
			Log.d("LOG_TAG",
					"Failed to get provider at [" + calendars.toString() + "]");
		}
		if (managedCursor != null) {
			managedCursor.close();
		}
		return res;
	}

	private static String getCalendarUriBase(Context mContext) {

		String calendarUriBase = null;
		Cursor managedCursor = null;

		Uri calendars = Uri.parse("content://calendar/calendars");

		try {
			managedCursor = mContext.getContentResolver().query(calendars,
					null, null, null, null);
		} catch (Exception e) {
			// eat
		}

		if (managedCursor != null) {
			calendarUriBase = "content://calendar/";
		} else {
			calendars = Uri.parse("content://com.android.calendar/calendars");
			try {
				managedCursor = mContext.getContentResolver().query(calendars,
						null, null, null, null);
			} catch (Exception e) {
				// eat
			}

			if (managedCursor != null) {
				calendarUriBase = "content://com.android.calendar/";
			}

		}
		if (managedCursor != null) {
			managedCursor.close();
		}
		return calendarUriBase;
	}

}
