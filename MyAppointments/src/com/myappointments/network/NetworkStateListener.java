package com.myappointments.network;

/**
 * 
 * NetworkStateListener is a listener interface to know about state of the network.  
 * 
 */
public interface NetworkStateListener {

	public void updateNetworkState(boolean isAvailable);
}
