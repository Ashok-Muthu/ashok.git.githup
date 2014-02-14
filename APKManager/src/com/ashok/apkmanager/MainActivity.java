package com.ashok.apkmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	private static final int LOAD_AVAIALBLE_APPLICATIONS = 0;
	private static final int SHARE_SELECTED_APPLICATION = 1;
	private ListView mAppListView;
	private List<ResolveInfo> mPackageList;
	private ArrayList<String> mAppList;
	private ApkManagerHandler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mAppListView = (ListView) findViewById(R.id.apk_list);
		mHandler = new ApkManagerHandler();
		mHandler.sendEmptyMessage(LOAD_AVAIALBLE_APPLICATIONS);
		mAppListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				ResolveInfo info = mPackageList.get(position);
				Message msg = Message.obtain();
				msg.what = SHARE_SELECTED_APPLICATION;
				msg.obj = info;
				mHandler.sendMessage(msg);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class ApkManagerHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == LOAD_AVAIALBLE_APPLICATIONS) {
				Intent intent = new Intent(Intent.ACTION_MAIN, null);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				mPackageList = getPackageManager().queryIntentActivities(
						intent, 0);
				mAppList = new ArrayList<String>();
				for (ResolveInfo ResInfo : mPackageList) {
					ResolveInfo infoObj = ResInfo;
					String appName = infoObj.loadLabel(getPackageManager())
							.toString();
					mAppList.add(appName);
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						MainActivity.this, android.R.layout.simple_list_item_1,
						mAppList);
				mAppListView.setAdapter(adapter);

			} else if (msg.what == SHARE_SELECTED_APPLICATION) {
				ResolveInfo info = (ResolveInfo) msg.obj;
				File file = new File(
						info.activityInfo.applicationInfo.publicSourceDir);

				File f2 = new File(Environment.getExternalStorageDirectory()
						.toString() + "/Myapps");
				f2.mkdirs();
				f2 = new File(f2.getPath() + "/"
						+ info.loadLabel(getPackageManager()) + ".apk");
				try {
					f2.createNewFile();

					InputStream in = new FileInputStream(file);

					OutputStream out = new FileOutputStream(f2);

					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					in.close();
					out.close();

				Intent sendIntent = new Intent(Intent.ACTION_SEND);
				sendIntent.setType("message/rfc822");
				sendIntent.putExtra(Intent.EXTRA_EMAIL,
						new String[] { "ashok.m.mca@gmail.com" });
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Android App : "+info.loadLabel(getPackageManager()));
				sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi,\n Please find the Android application which I have attached \n\n Thanks,\nAshok");

				
				
				File pngDir = new File(Environment.getExternalStorageDirectory(),"Myapps/"); 
	            if (!pngDir.exists())
	            {
	                pngDir.mkdirs();
	            }              
//	            File pngfile=new File(pngDir,"MyAppointments.apk");
//	            Uri pngUri =Uri.fromFile(pngfile);
	            Uri path = Uri.fromFile(f2.getAbsoluteFile());

				sendIntent.putExtra(Intent.EXTRA_STREAM, path);
				// Log.d(TAG, "Size of the ArrayList :: " +uriList.size());
				startActivity(Intent.createChooser(sendIntent, "Email:"));
			}catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

}
