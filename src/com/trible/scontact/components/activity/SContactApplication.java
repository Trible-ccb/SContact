package com.trible.scontact.components.activity;

import com.trible.scontact.utils.Bog;

import android.app.Application;

/**
 * @author Trible Chen
 *the application class for making some configuration
 */
public class SContactApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	void initSystem(){
		Bog.init(getApplicationContext(), "SContact");
	}
}
