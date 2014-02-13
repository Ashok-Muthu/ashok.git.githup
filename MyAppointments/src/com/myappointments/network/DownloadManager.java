package com.myappointments.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

/**
 * DownloadManager is a helper class to do download any data on Internet. 
 */
public class DownloadManager extends Thread implements NetworkStateListener {

	private final Handler mHandler;
	private final int mWhat;
	private final String mUrl;
	private boolean isNetworkConnectionAvailable = true;
	private Activity mActivity;

	public DownloadManager(Activity activity, final Handler handler,
			final int what, final String url, final String method,
			final HashMap<?, ?> params) {
		mHandler = handler;
		mWhat = what;
		mUrl = url;
		mActivity = activity;
		 NetworkStateReceiver.getInstance(mActivity).setNetworkListener(this);
		 NetworkStateReceiver.getInstance(mActivity).registerNetworkBroadcast(this);
	}

	public final void run() {

		if (isNetworkConnectionAvailable) {
			HttpURLConnection connectionObj = null;

			trustAllHosts();
			try {

				URL url = new URL(mUrl);

				connectionObj = (HttpURLConnection) url.openConnection();
				connectionObj.setConnectTimeout(30000);//30 seconds
				int responseCode = connectionObj.getResponseCode();

				if (responseCode == HttpURLConnection.HTTP_OK) {
					InputStream in = connectionObj.getInputStream();
					Message msg = Message.obtain();
					msg.what = mWhat;
					msg.obj = streamToBytes(in);
					mHandler.sendMessage(msg);
				} else {
					Message msg = Message.obtain();
					msg.what = mWhat;
					msg.obj = connectionObj.getResponseMessage();
					mHandler.sendMessage(msg);
				}

			} catch (MalformedURLException e) {

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				connectionObj.disconnect();
			}
		} else {
			Message msg = Message.obtain();
			msg.what = mWhat;
			msg.obj = "No network";
			mHandler.sendMessage(msg);
		}

	}

	/**
	 * Fully reads the given InputStream and returns the byte array content
	 * 
	 * @return
	 * @throws IOException
	 */
	public byte[] streamToBytes(final InputStream in) throws IOException {
		ByteArrayOutputStream tempOutputStream = new ByteArrayOutputStream();
		int length = 0;
		InputStream inputStream = in;
		byte[] tempData = new byte[512];
		while ((length = inputStream.read(tempData)) != -1) {
			tempOutputStream.write(tempData, 0, length);
		}
		tempData = null;
		tempData = tempOutputStream.toByteArray();

		tempOutputStream.close();
		if (inputStream != null) {
			inputStream.close();
		}
		tempOutputStream = null;
		return tempData;
	}

	/**
	 * Trust every server - dont check for any certificate
	 */
	private void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(final X509Certificate[] chain,
					final String authType) throws CertificateException {
				// do nothing here.
			}

			public void checkServerTrusted(final X509Certificate[] chain,
					final String authType) throws CertificateException {
				// do nothing here.
			}
		} };

		// Install the all-trusting trust manager
		SSLContext sslCtx;
		try {
			sslCtx = SSLContext.getInstance("TLS");
			sslCtx.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslCtx
					.getSocketFactory());
		} catch (NoSuchAlgorithmException e) {
			// Log.e(SubscriptionConstants.TAG,
			// "trustAllHosts -> NoSuchAlgorithmException : " + e.getMessage());
		} catch (KeyManagementException e) {
			// Log.e(SubscriptionConstants.TAG,
			// "trustAllHosts -> KeyManagementException : " + e.getMessage());
		}
		
	
	}

	@Override
	public void updateNetworkState(boolean isAvailable) {
		 isNetworkConnectionAvailable = isAvailable;

	}


}
