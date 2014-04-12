package com.trible.scontact.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.trible.scontact.BuildConfig;
import com.trible.scontact.pojo.ErrorInfo;
import com.trible.scontact.pojo.GsonHelper;

public class Bog {

	private static boolean debug = BuildConfig.DEBUG;
	private static String TAG = "Bog";
	private static Context mContext;
	
	public static void init(Context c,String tag){
		if ( tag != null ) TAG = tag;
		mContext = c;
	}
	public static void i(String s){
		if ( debug ){
			Log.i(TAG, s);
		}
	}
	public static void e(String s){
		if ( debug ){
			Log.e(TAG, s);
		}
	}
	public static void v(String s){
		if ( debug ){
			Log.v(TAG, s);
		}
	}
	public static void toast(int id){
		toast(mContext.getResources().getString(id));
	}
	
	public static void toast(String s){
		if ( TextUtils.isEmpty(s) ){
			return;
		}
		Toast t = Toast.makeText(mContext, s, Toast.LENGTH_SHORT);
		if ( s.length() > 15 ){
			t.setDuration(Toast.LENGTH_LONG);
		}
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}
	public static void toastDebug(int id){
		if ( debug ){
			toast(id);
		}
	}
	public static void toastDebug(String s){
		if ( debug ){
			toast(s);
		}
	}
	
	public static void toastErrorInfo(byte[] arg2){
		ErrorInfo err = GsonHelper.getInfoFromJson(arg2, ErrorInfo.class);
		Bog.toast(err == null ? ErrorInfo.getUnkownErr().toString() : err.toString());
	}
}
