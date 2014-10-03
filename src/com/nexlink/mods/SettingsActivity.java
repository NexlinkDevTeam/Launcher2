package com.nexlink.mods;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.nexlink.launcher.LauncherApplication;
import com.nexlink.launcher.R;

public class SettingsActivity extends Activity {
	private static Context mContext;
	private BroadcastReceiver mPackageSelectReceiver;

	public static class PrefsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
			findPreference("selectPackages").setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					Intent intent = new Intent(mContext, PackageSelectActivity.class);
					startActivity(intent);
					return true;
				}
			});
		}
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		if(mPackageSelectReceiver != null) unregisterReceiver(mPackageSelectReceiver);
	}
	
	public static String getAppNameByPID(Context context, int pid){
	    ActivityManager manager 
	               = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

	    for(RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()){
	        if(processInfo.pid == pid){
	            return processInfo.processName;
	        }
	    }
	    return "";
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		registerReceiver(mPackageSelectReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				ArrayList < String > packageNames = intent.getExtras().getStringArrayList("packageNames");
				if (packageNames != null) {
					PreferenceManager.getDefaultSharedPreferences(mContext)
					.edit().putStringSet("shownApps", new HashSet<String>(packageNames)).commit();
					Toast.makeText(context, "Saved launcher apps!", Toast.LENGTH_LONG).show();
				}
			}
		}, new IntentFilter("com.nexlink.launcher.PACKAGE_SELECT"));
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
	}
}