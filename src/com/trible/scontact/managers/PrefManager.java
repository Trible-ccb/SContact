package com.trible.scontact.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefManager {

	private static Context mContext;
	private static PrefManager mPrefManager;
	private static String APP_NAME;
	private boolean isFirstOpen;

	private PrefManager(Context context) {

	}

	public static PrefManager getInstance() {

		if (mPrefManager == null) {
			mPrefManager = new PrefManager(null);

		}
		return mPrefManager;
	}

	public static void initPrefManager(Context c, String AppName) {
		APP_NAME = AppName;
		mContext = c;
		getInstance();
		mPrefManager.isFirstOpen = false;
		if (mPrefManager.getIsFirstTimeLauch()) {
			mPrefManager.isFirstOpen = true;
			mPrefManager.setIsFirstTimeLauch(false);
		}
	}

	public SharedPreferences getSPF() {
		SharedPreferences sp = null;
		sp = mContext.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);

		return sp;
	}

	public boolean getIsFirstOpen() {
		return mPrefManager.isFirstOpen;
	}

	private boolean getIsFirstTimeLauch() {
		boolean f = getSPF().getBoolean("FirstTimeTag", true);
		return f;
	}

	private void setIsFirstTimeLauch(boolean f) {
		Editor edt = getSPF().edit();
		edt.putBoolean("FirstTimeTag", f);
		edt.commit();
	}

	public String getString(String key) {
		String f = getSPF().getString(key, "");
		return f;
	}

	public boolean putString(String key, String v) {
		Editor edt = getSPF().edit();
		edt.putString(key, v);
		return edt.commit();
	}

	public int getInteger(String key) {
		int f = getSPF().getInt(key, 0);
		return f;
	}

	public long getLong(String key) {
		Long f = getSPF().getLong(key, 0);
		return f;

	}

	public boolean putLong(String key, long value) {

		Editor edt = getSPF().edit();
		edt.putLong(key, value);
		return edt.commit();
	}

	public boolean putInteger(String key, int v) {
		Editor edt = getSPF().edit();
		edt.putInt(key, v);
		return edt.commit();
	}

	public boolean getBoolean(String key) {
		boolean f = getSPF().getBoolean(key, false);
		return f;
	}

	public boolean putBoolean(String key, boolean v) {
		Editor edt = getSPF().edit();
		edt.putBoolean(key, v);
		return edt.commit();
	}

	public boolean clearUserData(Context c) {
		Editor e = getSPF().edit().clear();
		return e.commit();
	}

}
