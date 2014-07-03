package com.trible.scontact.components.activity;

import android.app.Application;

import com.trible.scontact.R;
import com.trible.scontact.database.DBHelper;
import com.trible.scontact.managers.PrefManager;
import com.trible.scontact.managers.SDCardManager;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactTypes;
import com.trible.scontact.utils.Bog;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.sso.SinaSsoHandler;

/**
 * @author Trible Chen
 *the application class for making some configuration
 */
public class SContactApplication extends Application {

	public static SContactApplication mAppContext;
	public static boolean ENV_DEBUG = false;
	public static boolean LOG_DEBUG = true;
	public static String DEV_IP = "192.168.1.100";
	public static String PRODUCT_IP = "121.48.175.134";
	public static String IP;
	public static String URL = "http://" + IP + ":8888/scontact/services";
	public static void setIP(String s){
		IP = s;
		if ( s.contains(":") ){
			URL = "http://" + IP + "/scontact/services";
		} else if(s.startsWith("192")){
			URL = "http://" + IP + ":8888/scontact/services";
		} else {
			URL = "http://" + IP + "/scontact/services";
		}
//		PrefManager.getInstance().putString("IP", s);
	}
	
	public static String getURL() {
//		setIP(PrefManager.getInstance().getString("IP"));
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
		if ( SContactApplication.ENV_DEBUG ){
			setIP(DEV_IP);
		} else {
			setIP(PRODUCT_IP);
		}
		
	}
	void initSystem(){
		String name = getString(R.string.preference_name);
		Bog.init(this, name);
		PrefManager.initPrefManager(this, name);
		SDCardManager.initStorageWithClassicPaths(getApplicationContext(), name);
		ContactTypes.init(this);
		SocializeConfig.getSocializeConfig().setSsoHandler(new SinaSsoHandler());
		DBHelper.init(this);
	}
}
