package com.myappointments.push;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
public class PubnubSubscription {
	private Pubnub mPubnub;
	private String mPublishKey="pub-c-3f8cc25a-a819-4e41-9eaf-c31bda3fbb4c";
	private String mSubscribeKey="sub-c-9e3b7f82-94b9-11e3-b381-02ee2ddab7fe";
	private Handler mHandler;
	public PubnubSubscription(Handler handler){
		mHandler = handler;
		mPubnub = new Pubnub(mPublishKey, mSubscribeKey);
		try {
			subscribePubnub();
		} catch (PubnubException e) {
			e.printStackTrace();
		}
	}
	
private void subscribePubnub() throws PubnubException{
	mPubnub.subscribe("Ashok_chennal", new Callback() {
		
		@Override
		public void successCallback(String channel, Object message) {
			 Log.d("PUBNUB","SUBSCRIBE : " + channel + " : "
                     + message.getClass() + " : " + message.toString());
			 Message msg = Message.obtain();
			 msg.obj = message;
			 msg.what  = 1;
			 mHandler.sendMessage(msg);
		}
		@Override
		public void connectCallback(String channel, Object message) {
			Log.d("PUBNUB","SUBSCRIBE : CONNECT on channel:" + channel
                    + " : " + message.getClass() + " : "
                    + message.toString());
		}
		@Override
		public void disconnectCallback(String channel, Object message) {
			Log.d("PUBNUB","SUBSCRIBE : DISCONNECT on channel:" + channel
                    + " : " + message.getClass() + " : "
                    + message.toString());
		}
		@Override
		public void reconnectCallback(String channel, Object message) {
			 Log.d("PUBNUB","SUBSCRIBE : RECONNECT on channel:" + channel
                     + " : " + message.getClass() + " : "
                     + message.toString());
		}
		@Override
		public void errorCallback(String channel, PubnubError error) {
			Log.d("PUBNUB","SUBSCRIBE : ERROR on channel " + channel
                    + " : " + error.toString());
		}
	});
}
	
	
}
