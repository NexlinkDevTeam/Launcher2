package com.nexlink.mods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TabHost;

import com.nexlink.launcher.R;

public class PackageSelectActivity extends Activity {

	private TabHost mTabHost;
	private ListView mUserListView;
	private ListView mSystemListView;
	private PackageItemAdapter mUserArrayAdapter;
	private PackageItemAdapter mSystemArrayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_package_select);
		
		mTabHost = (TabHost) findViewById(R.id.tabhost);
		mTabHost.setup();
		mTabHost.addTab(mTabHost.newTabSpec("User").setContent(R.id.package_select_user_listview_container).setIndicator("User", null));
		mTabHost.addTab(mTabHost.newTabSpec("System").setContent(R.id.package_select_system_listview_container).setIndicator("System", null));
		
		mUserListView = (ListView) findViewById(R.id.package_select_user_list);
		mSystemListView = (ListView) findViewById(R.id.package_select_system_list);
		
		mUserArrayAdapter = new PackageItemAdapter(this);
		mSystemArrayAdapter = new PackageItemAdapter(this);
		
		final PackageManager packageManager = getPackageManager();
	    Intent intent = new Intent(Intent.ACTION_MAIN, null);
	    intent.addCategory(Intent.CATEGORY_LAUNCHER);
	    List<ResolveInfo> resInfos = packageManager.queryIntentActivities(intent, 0);
	    HashSet<String> packageNames = new HashSet<String>(0);
	    List<ApplicationInfo> appInfos = new ArrayList<ApplicationInfo>(0);

	    for(ResolveInfo resolveInfo : resInfos) {
	        packageNames.add(resolveInfo.activityInfo.packageName);
	    }
	    for(String packageName : packageNames) {
	        try {
	            appInfos.add(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
	        } catch (NameNotFoundException e) {}
	    }
	    Collections.sort(appInfos, new ApplicationInfo.DisplayNameComparator(packageManager));
	    HashSet<String> enabled = (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getStringSet("shownApps", new HashSet < String > ());
		for(ApplicationInfo appInfo : appInfos){
			if ((appInfo.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0) {
			    mSystemArrayAdapter.add(appInfo, enabled.contains(appInfo.packageName));       
	        }
	        else {
	    	    mUserArrayAdapter.add(appInfo, enabled.contains(appInfo.packageName));
	       }
		}
		mUserListView.setAdapter(mUserArrayAdapter);
		mSystemListView.setAdapter(mSystemArrayAdapter);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		finish();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		HashSet<String> packageNames = new HashSet<String>();
		packageNames.addAll(mUserArrayAdapter.getEnabled());
		packageNames.addAll(mSystemArrayAdapter.getEnabled());
		PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
		.edit().putStringSet("shownApps", new HashSet<String>(packageNames)).commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.layout.package_select_menu, menu);
        return true;
    }
	
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item)
	 {
		PackageItemAdapter arrayAdapter = (mTabHost.getCurrentTab() == 0) ? mUserArrayAdapter : mSystemArrayAdapter;
		arrayAdapter.setAll(item.getItemId() == R.id.package_select_all);
		arrayAdapter.notifyDataSetChanged();
		return true;
	 }
}
