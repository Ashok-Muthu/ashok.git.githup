package com.myappointments.model;

/**
 * 
 * AppointmentsData is a Model class for appointment feed data.
 *
 */
public class AppointmentsData {

	private String mTime;
	private String mName;
	private String mPhone;
	private String mAddress;
	private String mCity;

	public String getTime() {
		return mTime;
	}

	public void setTime(String time) {
		this.mTime = time;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getPhone() {
		return mPhone;
	}

	public void setPhone(String phone) {
		this.mPhone = phone;
	}

	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String address) {
		this.mAddress = address;
	}

	public String getCity() {
		return mCity;
	}

	public void setCity(String city) {
		this.mCity = city;
	}

}
