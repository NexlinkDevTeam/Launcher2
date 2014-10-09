package com.nexlink.mods;

import java.util.HashSet;

import android.preference.PreferenceManager;

import com.nexlink.launcher.LauncherApplication;

public class Blocker {
	public static final String ACTION_RELOAD = "com.nexlink.launcher.ACTION_RELOAD";
	
	private static Boolean whitelistEnabled;
	private static HashSet<String> whitelist;
	
	public static boolean isBlocked(String packageName){
		if(whitelist == null){
			whitelist = (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(LauncherApplication.appContext).getStringSet("shownApps", new HashSet<String>());
			System.out.println("LOADED " + whitelist.size());
			whitelist.add("com.nexlink.mdmcontrolpanel");
		}
		if(whitelistEnabled == null){
			whitelistEnabled = Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(LauncherApplication.appContext).getBoolean("appWhitelisting", false));
		}
		if(!whitelistEnabled.booleanValue()){
			return false;
		}
		boolean blocked = true;
		if(packageName != null){
			for(String filter : whitelist){
				if(!filter.isEmpty() && packageName.contains(filter.trim())){
					blocked = false;
				}
			}
		}
		return blocked;
	}
}
