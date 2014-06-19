package com.trible.scontact.components.activity;

import org.androidpn.client.ServiceManager;

import com.tencent.tauth.Tencent;
import com.trible.scontact.R;
import com.trible.scontact.components.widgets.NotifyHelper;
import com.trible.scontact.managers.PrefManager;
import com.trible.scontact.managers.SDCardManager;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactTypes;
import com.trible.scontact.thirdparty.TencentHelper;
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
		} else if(s.startsWith("192")){
			URL = "http://" + IP + ":8888/scontacts/services";
		} else {
			URL = "http://" + IP + "/services";
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
		String ip = PrefManager.getInstance().getString("IP");
		if ( !TextUtils.isEmpty(ip) ){
			SContactApplication.setIP(ip);
		} else {
			setIP(IP);
		}
		
	}
	void initSystem(){
		String name = "SContact";
		Bog.init(this, name);
		PrefManager.initPrefManager(this, name);
		SDCardManager.initStorageWithClassicPaths(getApplicationContext(), name);
		TencentHelper.init(this);
		ContactTypes.init(this);
		NotifyHelper.register(this);
	}
}
