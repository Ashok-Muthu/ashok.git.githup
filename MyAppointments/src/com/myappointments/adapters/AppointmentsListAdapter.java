package com.myappointments.adapters;

import java.util.ArrayList;

import com.myappointments.R;
import com.myappointments.model.AppointmentsData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * AppointmentsListAdapter used to bind list of data with UI.   
 * 
 *
 */
public class AppointmentsListAdapter extends BaseAdapter {

	private LayoutInflater mLayoutInflater;
	private ArrayList<AppointmentsData> mDataSource;

	public AppointmentsListAdapter(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
	}

	public void setDatasource(ArrayList<AppointmentsData> data) {
		mDataSource = data;
	}

	@Override
	public int getCount() {
		return (mDataSource != null) ? mDataSource.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return (mDataSource != null && mDataSource.size() >= position) ? mDataSource
				.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ListItemHolder viewHolder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					R.layout.appointment_list_item, null);
			viewHolder = new ListItemHolder();
			viewHolder.timeTextView = (TextView) convertView
					.findViewById(R.id.view_time);
			viewHolder.nameTextView = (TextView) convertView
					.findViewById(R.id.view_name);
			viewHolder.phoneTextView = (TextView) convertView
					.findViewById(R.id.view_phone);
			viewHolder.addressTextView = (TextView) convertView
					.findViewById(R.id.view_address);
			viewHolder.cityTextView = (TextView) convertView
					.findViewById(R.id.view_city);
			viewHolder.addToCalendar = (ImageView) convertView
					.findViewById(R.id.add_reminder);
			viewHolder.nameAddressLayout = (LinearLayout) convertView
					.findViewById(R.id.name_address_layout);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ListItemHolder) convertView.getTag();
		}

		AppointmentsData data = mDataSource.get(position);
		viewHolder.addToCalendar.setTag(data);
		viewHolder.nameAddressLayout.setTag(data);
		if (data != null) {
			viewHolder.timeTextView.setText(data.getTime());
			viewHolder.nameTextView.setText(data.getName());
			viewHolder.phoneTextView.setText(data.getPhone());
			viewHolder.addressTextView.setText(data.getAddress());
			viewHolder.cityTextView.setText(data.getCity());

		}

		return convertView;
	}

	static class ListItemHolder {
		public TextView timeTextView;
		public TextView nameTextView;
		public TextView phoneTextView;
		public TextView addressTextView;
		public TextView cityTextView;
		public ImageView addToCalendar;
		public LinearLayout nameAddressLayout;

	}
}
