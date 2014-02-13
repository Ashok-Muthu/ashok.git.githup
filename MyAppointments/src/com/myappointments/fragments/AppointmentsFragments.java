package com.myappointments.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import com.myappointments.R;
import com.myappointments.adapters.AppointmentsListAdapter;
import com.myappointments.model.AppointmentDataHelper;
import com.myappointments.model.AppointmentsData;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A AppointmentsFragments is a fragment representing a Main screen to show the list of appointments for a day
 */
public class AppointmentsFragments extends Fragment  {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";

	private DownloadHandler mHandler;
	private static final int SHOW_APPOINMENT_LIST = 2;
	private static final int SHOW_ERROR_MSG = 3;

	private View mFragView;
	private TextView mErrorView;
	private ListView mListView;
	private FrameLayout mProgressLayout;
	private AppointmentsListAdapter mListAdapter;
	private int mFragmentArgSecNumber = -1;

	public AppointmentsFragments() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mFragView = inflater.inflate(R.layout.appointmentsfragment, null);
		mErrorView = (TextView) mFragView.findViewById(R.id.errormsgview);
		mListView = (ListView) mFragView.findViewById(R.id.days_listview);
		mProgressLayout = (FrameLayout) mFragView
				.findViewById(R.id.progressLayout);

		mHandler = new DownloadHandler();

		mFragmentArgSecNumber = getArguments().getInt(
				AppointmentsFragments.ARG_SECTION_NUMBER);
		String errorMsg = getArguments().getString("ErrorMsg");

		if (errorMsg == null) {
			mHandler.sendEmptyMessage(SHOW_APPOINMENT_LIST);
		} else {
			Message msg = Message.obtain();
			msg.what = SHOW_ERROR_MSG;
			msg.obj = errorMsg;
			mHandler.sendMessage(msg);
		}
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(getActivity(), "List item click", Toast.LENGTH_SHORT).show();
			}
		});
		return mFragView;
	}

	public void showProgressBar() {
		mListView.setVisibility(View.GONE);
		mErrorView.setVisibility(View.GONE);
		mProgressLayout.setVisibility(View.VISIBLE);
	}

	public void hideProgressBar() {
		mListView.setVisibility(View.VISIBLE);
		mProgressLayout.setVisibility(View.GONE);
	}
	
/**
 * 
 * Handler class used to send the object to the Adapter.
 *
 */
	class DownloadHandler extends Handler {
		@Override
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);

			if (msg.what == SHOW_APPOINMENT_LIST) {

				hideProgressBar();

				AppointmentDataHelper appData = AppointmentDataHelper
						.getInstance();

				HashMap<String, ArrayList<AppointmentsData>> listDataSource = appData
						.getAppoinmentDataMap();

				if (listDataSource != null) {
					if (mListAdapter == null) {

						mListAdapter = new AppointmentsListAdapter(
								getActivity());
					}
					String[] dayShotNameList = getResources().getStringArray(
							R.array.day_list);

					String day_key = dayShotNameList[mFragmentArgSecNumber - 1];

					ArrayList<AppointmentsData> dayDataList = listDataSource
							.get(day_key);

					if (dayDataList != null && dayDataList.size() > 0) {
						mListAdapter.setDatasource(dayDataList);
						mListView.setAdapter(mListAdapter);
						mListAdapter.notifyDataSetChanged();
					}
				} else {
					mHandler.sendEmptyMessage(SHOW_ERROR_MSG);
				}
			} else if (msg.what == SHOW_ERROR_MSG) {
				hideProgressBar();
				mErrorView.setVisibility(View.VISIBLE);
				if (msg.obj != null) {
					mErrorView.setText(msg.obj.toString());
				} else {
					mErrorView.setText(getString(R.string.no_data));
				}
			}
		}
	}


	
}
