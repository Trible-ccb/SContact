package com.trible.scontact.components.activity;

import com.trible.scontact.managers.PrefManager;
import com.trible.scontact.managers.SDCardManager;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.utils.Bog;

import android.app.Application;
import android.text.TextUtils;

/**
 * @author Trible Chen
 *the application class for making some configuration
 */
public class SContactApplication extends Application {

	public static SContactApplication mAppContext;
	
	private static String IP = "192.168.1.104";
	private static String URL = "http://" + IP + ":8888/scontacts/services";
	public static void setIP(String s){
		IP = s;
		if ( s.contains(":") ){
			URL = "http://" + IP + "/scontacts/services";
		} else {
			URL = "http://" + IP + ":8888/scontacts/services";
		}
		
		PrefManager.getInstance().putString("IP", s);
	}
	
	public static String getURL() {
		setIP(PrefManager.getInstance().getString("IP"));
		return URL;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mAppContext = this;
		initSystem();
		initData();

		
	}

	void initData(){
		AccountInfo.setAccountInfo(AccountInfo.getFromPref());
		setIP(IP);
	}
	void initSystem(){
		String name = "SContact";
		Bog.init(this, name);
		PrefManager.initPrefManager(this, name);
		SDCardManager.initStorageWithClassicPaths(getApplicationContext(), name);
	}
}
