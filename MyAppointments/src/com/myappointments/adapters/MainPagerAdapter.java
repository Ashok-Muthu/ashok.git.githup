package com.myappointments.adapters;

import com.myappointments.R;
import com.myappointments.fragments.AppointmentsFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
/**
 * MainPagerAdapter used to set fragments with the View pager of Main screen.
 * 
 * 
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

	private Context mContext;
	private final String mErrorMsg;

	public MainPagerAdapter(FragmentManager fm, Context context, String arg) {
		super(fm);
		mContext = context;
		mErrorMsg = arg;
	}

	@Override
	public Fragment getItem(int position) {

		AppointmentsFragments fragment = new AppointmentsFragments();
		Bundle args = new Bundle();
		args.putInt(AppointmentsFragments.ARG_SECTION_NUMBER, position + 1);
		args.putString("ErrorMsg", mErrorMsg);
		fragment.setArguments(args);
		
		return fragment;
	}

	@Override
	public int getCount() {

		String[] tabNameList = mContext.getResources().getStringArray(
				R.array.tab_list);

		return tabNameList.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {

		String[] tabNameList = mContext.getResources().getStringArray(
				R.array.tab_list);

		return tabNameList[position];
	}

}
