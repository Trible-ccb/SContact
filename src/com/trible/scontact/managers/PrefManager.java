package com.trible.scontact.managers;

import java.io.File;

import org.w3c.dom.UserDataHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefManager {

	private static Context mContext;
	private static PrefManager mPrefManager;
	private static String APP_NAME;
	private boolean isFirstOpen;
	SharedPreferences mSpf;
	
	private PrefManager(Context context) {

	}

	public static PrefManager getInstance(String... name) {
		if (mPrefManager == null) {
			mPrefManager = new PrefManager(null);

		}
		if ( name == null || name.length == 0 ){
			mPrefManager.useDefaultSPF();
		} else {
			mPrefManager.useSPFByName(name[0]);
		}
		return mPrefManager;
	}

	public static void initPrefManager(Context c, String AppName) {
		APP_NAME = AppName;
		mContext = c;
		getInstance();
		mPrefManager.isFirstOpen = false;
		mPrefManager.useDefaultSPF();
		if (mPrefManager.getIsFirstTimeLauch()) {
			mPrefManager.isFirstOpen = true;
			mPrefManager.setIsFirstTimeLauch(false);
		}
		
	}

	public SharedPreferences useDefaultSPF() {
		mSpf = mContext.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
		return mSpf;
	}

	public SharedPreferences useSPFByName(String name) {
		mSpf = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
		return mSpf;
	}
	
	public boolean getIsFirstOpen() {
		return mPrefManager.isFirstOpen;
	}

	private boolean getIsFirstTimeLauch() {
		boolean f = mSpf.getBoolean("FirstTimeTag", true);
		return f;
	}

	private void setIsFirstTimeLauch(boolean f) {
		Editor edt = mSpf.edit();
		edt.putBoolean("FirstTimeTag", f);
		edt.commit();
	}

	public String getString(String key) {
		String f = mSpf.getString(key, "");
		return f;
	}

	public boolean putString(String key, String v) {
		Editor edt = mSpf.edit();
		edt.putString(key, v);
		return edt.commit();
	}

	public int getInteger(String key) {
		int f = mSpf.getInt(key, 0);
		return f;
	}

	public long getLong(String key) {
		Long f = mSpf.getLong(key, 0);
		return f;

	}

	public boolean putLong(String key, long value) {

		Editor edt = mSpf.edit();
		edt.putLong(key, value);
		return edt.commit();
	}

	public boolean putInteger(String key, int v) {
		Editor edt = mSpf.edit();
		edt.putInt(key, v);
		return edt.commit();
	}

	public boolean getBoolean(String key) {
		boolean f = mSpf.getBoolean(key, false);
		return f;
	}

	public boolean putBoolean(String key, boolean v) {
		Editor edt = mSpf.edit();
		edt.putBoolean(key, v);
		return edt.commit();
	}

	public boolean clearUserData() {
		Editor e = mSpf.edit().clear();
		return e.commit();
	}
	public void deleteFile(String name) {
		File f = new File(mContext.getFilesDir().getParent()+"/shared_prefs/" + name + ".xml");
		if ( f != null && f.exists() ){
			f.delete();
		}
	}
}
