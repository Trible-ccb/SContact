package com.trible.scontact.components.widgets;

import org.androidpn.client.Constants;
import org.androidpn.client.ServiceManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;

import com.trible.scontact.R;
import com.trible.scontact.managers.PrefManager;

public class NotifyHelper {

	public static void register(Context c){
		ServiceManager sm = new ServiceManager(c.getApplicationContext());
		sm.setNotificationIcon(R.drawable.ic_launcher);
		sm.startService();
	}
	public static void removeRegister(){
		Editor e = PrefManager.getInstance().useSPFByName(Constants.SHARED_PREFERENCE_NAME).edit();
		e.remove(Constants.XMPP_PASSWORD);
		e.remove(Constants.XMPP_USERNAME);
		e.commit();
	}
	public static String getUserNotifyID(){
		return PrefManager.getInstance(Constants.SHARED_PREFERENCE_NAME)
				.getString(Constants.XMPP_USERNAME);
	}
	public static void setCallbackActivity(Activity a){
		ServiceManager sm = new ServiceManager(a);
		sm.setCallbackActivity(a);
	}
	public static void stopConnect(Context c){
		ServiceManager sm = new ServiceManager(c);
		sm.stopService();
	}
}
