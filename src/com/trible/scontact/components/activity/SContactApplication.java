package com.trible.scontact.components.activity;

import com.trible.scontact.managers.PrefManager;
import com.trible.scontact.managers.SDCardManager;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.utils.Bog;

import android.app.Application;

/**
 * @author Trible Chen
 *the application class for making some configuration
 */
public class SContactApplication extends Application {

	public static SContactApplication mAppContext;
	
	public static String IP = "192.168.1.110";
	public static String URL = "http://" + IP + ":8888/scontacts/services";
	
	public static void setIP(String s){
		IP = s;
		URL = "http://" + IP + ":8888/scontacts/services";
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
	}
	void initSystem(){
		String name = "SContact";
		Bog.init(this, name);
		PrefManager.initPrefManager(this, name);
		SDCardManager.initStorageWithClassicPaths(getApplicationContext(), name);
	}
}
