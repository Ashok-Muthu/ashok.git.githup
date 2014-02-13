package com.myappointments.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;

import com.myappointments.R;
import com.myappointments.adapters.MainPagerAdapter;
import com.myappointments.model.AppointmentDataHelper;
import com.myappointments.model.AppointmentsData;
import com.myappointments.model.LocationHelper;
import com.myappointments.network.DownloadManager;
import com.myappointments.network.NetworkStateListener;
import com.myappointments.network.NetworkStateReceiver;
import com.myappointments.parser.AppointmentDataParser;
import com.myappointments.utils.UiUtils;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		NetworkStateListener, ActionBar.TabListener {

	private MainPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;

	private DownloadHandler mHandler;
	private static final int DOWNLOAD_START = 0;
	private static final int DOWNLOAD_COMPLETE = 1;
	private static final int SHOW_APPOINMENT_LIST = 2;
	private static final int SHOW_ERROR_MSG = 3;
	private static final int SHOW_GPS_ALERT_DIALOGE = 4;
	private static final int GET_CURRENT_LOCATION = 5;
	private static final int GET_LOCATION_FOR_ADDRESS = 6;
	private static final int SHOW_MAP_ACTIVITY = 7;
	private static final int SHOW_SETTINGS_GPS_ACTIVITY = 8;

	private DownloadManager mDownloadManager;
	private FrameLayout mProgressLayout;
	private MenuItem mRefreshItem = null;

	private static final String FEED_APPOINMENT = "https://testing.jifflenow.net/cisco_gartner2013/calendar.json";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mViewPager = (ViewPager) findViewById(R.id.pager);

		mProgressLayout = (FrameLayout) findViewById(R.id.progressLayout);

		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		String[] tabnamesList = getResources().getStringArray(R.array.tab_list);

		for (String tabName : tabnamesList) {
			actionBar.addTab(actionBar.newTab().setText(tabName)
					.setTabListener(this));
		}

		mHandler = new DownloadHandler();
		mHandler.sendEmptyMessage(DOWNLOAD_START);
	}

	public void hideProgressBar() {
		mProgressLayout.setVisibility(View.GONE);
		mViewPager.setVisibility(View.VISIBLE);
		// mRefreshItem.setActionView(android.R.drawable.ic_menu_r);
		if (mRefreshItem != null) {
			mRefreshItem.setActionView(null);
		}
	}

	public void showProgressBar() {
		mViewPager.setVisibility(View.GONE);
		mProgressLayout.setVisibility(View.VISIBLE);
	}

	private void initViews(String arg) {

		mSectionsPagerAdapter = new MainPagerAdapter(
				getSupportFragmentManager(), MainActivity.this, arg);

		mViewPager.setAdapter(mSectionsPagerAdapter);
		getActionBar().setSelectedNavigationItem(0);

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				getActionBar().setSelectedNavigationItem(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	/**
	 * 
	 * DownloadHandler class is used to send message to DownloadManager thread
	 * to download feed data. Once download finished DownloadManager will be
	 * return to DownloadHandler with response data. Once get the successful
	 * response, it goes to MainAdapter.
	 */
	class DownloadHandler extends Handler {
		@Override
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);

			if (msg.what == DOWNLOAD_START) {
				mDownloadManager = new DownloadManager(MainActivity.this,
						mHandler, DOWNLOAD_COMPLETE, FEED_APPOINMENT, null,
						null);
				mDownloadManager.start();

			} else if (msg.what == DOWNLOAD_COMPLETE) {
				if (msg.obj != null && msg.obj instanceof byte[]) {
					final String strFeedData = new String((byte[]) msg.obj);
					AppointmentDataHelper data = AppointmentDataHelper
							.getInstance();
					String execepMsg = null;
					Object listObj = null;
					if (strFeedData != null && strFeedData.length() > 0) {
						AppointmentDataParser parser = new AppointmentDataParser(
								strFeedData);
						try {
							listObj = parser.parseDataByJson();
							if (listObj != null) {
								data.setAppoinmentDataMap((HashMap<String, ArrayList<AppointmentsData>>) listObj);
							}
						} catch (JSONException e) {
							execepMsg = e.getMessage();

						}
					}
					hideProgressBar();
					if (execepMsg == null && listObj != null) {
						Message msgObj = Message.obtain();
						msgObj.what = SHOW_APPOINMENT_LIST;
						msgObj.obj = listObj;
						mHandler.sendMessage(msgObj);
					} else if (execepMsg != null) {
						Message msgObj = Message.obtain();
						msgObj.what = SHOW_ERROR_MSG;
						msgObj.obj = execepMsg;
						mHandler.sendMessage(msgObj);
					} else {
						Message msgObj = Message.obtain();
						msgObj.what = SHOW_ERROR_MSG;
						msgObj.obj = getString(R.string.no_data);
						mHandler.sendMessage(msgObj);
					}

				} else {
					hideProgressBar();
					Message msgObj = Message.obtain();
					msgObj.what = SHOW_ERROR_MSG;
					msgObj.obj = msg.obj;
					mHandler.sendMessage(msgObj);
				}

			} else if (msg.what == SHOW_APPOINMENT_LIST) {
				initViews(null);
			} else if (msg.what == SHOW_ERROR_MSG) {
				initViews(msg.obj.toString());
			} else if (msg.what == SHOW_SETTINGS_GPS_ACTIVITY) {
				Intent intent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			} else if (msg.what == SHOW_MAP_ACTIVITY) {
				Intent mapIntent = new Intent(
						android.content.Intent.ACTION_VIEW,
						Uri.parse("geo:0,0?q=Jayanagar 4th Block,Bangalore"));
				startActivity(mapIntent);
			} else if (msg.what == SHOW_GPS_ALERT_DIALOGE) {

				showGPSEnableAlert();

			} else if (msg.what == GET_CURRENT_LOCATION) {

				// String uri = String.format(Locale.ENGLISH,
				// "http://maps.google.com/maps?&daddr=%f,%f (%s)", 12f, 2f,
				// "Where the party is at");
				// Intent intent = new Intent(Intent.ACTION_VIEW,
				// Uri.parse(uri));
				// intent.setClassName("com.google.android.apps.maps",
				// "com.google.android.maps.MapsActivity");
				// try
				// {
				// startActivity(intent);
				// }
				// catch(ActivityNotFoundException ex)
				// {
				// try
				// {
				// Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW,
				// Uri.parse(uri));
				// startActivity(unrestrictedIntent);
				// }
				// catch(ActivityNotFoundException innerEx)
				// {
				// Toast.makeText(this, "Please install a maps application",
				// Toast.LENGTH_LONG).show();
				// }
				// }

			} else if (msg.what == GET_LOCATION_FOR_ADDRESS) {
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mInflate = getMenuInflater();
		mInflate.inflate(R.menu.activity_main, menu);
		mRefreshItem = menu.findItem(R.id.menu_refresh);
		mRefreshItem.setVisible(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			NavUtils.navigateUpFromSameTask(this);
			return true;

		case R.id.menu_refresh:
			if (mRefreshItem != null) {
				mRefreshItem
						.setActionView(R.layout.actionbar_indeterminate_progress);
				mHandler.sendEmptyMessage(DOWNLOAD_START);
				mViewPager.setAdapter(null);
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void updateNetworkState(boolean isAvailable) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		NetworkStateReceiver.getInstance(this).unregisterNetworkBrodcast();
	}

	/**
	 * addCalendar() method declared in the list-item, Call back will come when
	 * you click the view / button in the list-view. view parameter with data
	 * item will receive, for that we have set the data item as viewTag(data) in
	 * Adapter.
	 * 
	 * @param view
	 */
	public void addCalendarItemClick(View view) {
		AppointmentsData data = (AppointmentsData) view.getTag();
		addCalendarEvent(data);
		Toast.makeText(this, "addCalendar", Toast.LENGTH_SHORT).show();
	}

	/**
	 * onNameAddressItemClick() method to get call back when you click name -
	 * Address item in the list.
	 * 
	 * @param view
	 */
	public void onNameAddressItemClick(View view) {
		LocationHelper locationHelper = LocationHelper
				.getLocationHelperInstance(MainActivity.this);
		if (locationHelper.isGPSEnabled()) {
			mHandler.sendEmptyMessage(SHOW_MAP_ACTIVITY);
		} else {
			mHandler.sendEmptyMessage(SHOW_GPS_ALERT_DIALOGE);
		}
	}

	private void showGPSEnableAlert() {

		Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

		alertDialog.setTitle("Enable GPS / Wifi / Internet!");
		alertDialog.setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mHandler.sendEmptyMessage(SHOW_SETTINGS_GPS_ACTIVITY);
			}
		});
		alertDialog.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// DO NOTHING
			}
		});
		alertDialog.create();
		alertDialog.show();
	}

	private void addCalendarEvent(AppointmentsData eventData) {

		if (eventData != null) {

			String timeData = eventData.getTime();
			// TODO: Start time and end time have to be milliseconds.
			long startTime = Long.parseLong(timeData.substring(0,
					timeData.indexOf("-")));
			long endtime = Long.parseLong(timeData.substring(
					timeData.indexOf("-") + 1, timeData.length()));

			StringBuilder description = new StringBuilder(eventData.getName());
			description.append(" - ");
			description.append(eventData.getAddress());

			if (checkCalendarEvent(startTime, endtime, description.toString()) == false) {

				final Intent intent = new Intent(Intent.ACTION_EDIT);// NOPMD
				intent.setType("vnd.android.cursor.item/event");
				intent.putExtra("beginTime", startTime);
				intent.putExtra("description", description.toString());
				intent.putExtra("endTime", endtime);

				intent.putExtra("title", description.toString());

				startActivity(intent);
			} else {
				Toast.makeText(this, "Event already registered",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private boolean checkCalendarEvent(long startTime, long endTime,
			String title) {

		StringBuilder titleMsg = new StringBuilder();

		String heading = DatabaseUtils.sqlEscapeString(title);
		String strAnd = " AND ";

		titleMsg.append("title=");
		titleMsg.append(heading);
		titleMsg.append(strAnd);
		titleMsg.append("dtstart=");
		titleMsg.append(startTime);
		titleMsg.append(strAnd);
		titleMsg.append("dtend=");
		titleMsg.append(endTime);
		titleMsg.append(strAnd);
		titleMsg.append("deleted != 1");

		int res = UiUtils.getCalendarManagedCursor(MainActivity.this,
				new String[] { "title" }, titleMsg.toString(), "events",
				"title", title);

		if (UiUtils.CALENDAR_EVENT_EXISTS == res) {
			return true;
		} else {
			return false;
		}
	}

}
