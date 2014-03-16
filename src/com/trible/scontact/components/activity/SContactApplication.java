package com.trible.scontact.components.activity;

import com.trible.scontact.managers.PrefManager;
import com.trible.scontact.managers.SDCardManager;
import com.trible.scontact.utils.Bog;

import android.app.Application;

/**
 * @author Trible Chen
 *the application class for making some configuration
 */
public class SContactApplication extends Application {

	public static SContactApplication mAppContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mAppContext = this;
		initSystem();
	}

	void initSystem(){
		String name = "SContact";
		Bog.init(this, name);
		PrefManager.initPrefManager(this, name);
		SDCardManager.initStorageWithClassicPaths(getApplicationContext(), name);
	}
}
