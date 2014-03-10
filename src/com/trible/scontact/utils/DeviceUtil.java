package com.trible.scontact.utils;

import com.trible.scontact.managers.PrefManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

public class DeviceUtil {

	public  static boolean isPad(Context ct) {  
	    WindowManager wm = (WindowManager) ct.getSystemService(Context.WINDOW_SERVICE);  
	    Display display = wm.getDefaultDisplay();  

	    DisplayMetrics dm = new DisplayMetrics(); 
	    
	    display.getMetrics(dm);
	    // 屏幕宽度  
	    float screenWidth = dm.widthPixels; 
	    // 屏幕高度  
	    float screenHeight = dm.heightPixels;  
	    double x = Math.pow(screenWidth / dm.xdpi, 2);  
	    double y = Math.pow(screenHeight / dm.ydpi, 2);  
	    // 屏幕尺寸  
	    double screenInches = Math.sqrt(x + y);  
	    // 大于5尺寸则为Pad  
	    if (screenInches >= 5) {  
	        return true;  
	    }  
	    return false;  
	}
	public static void bePortrait(Activity a){
		if(a.getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
			  a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	} 
	public static void beLandscape(Activity a){
		if(a.getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			  a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}	
	public static void focusePortraitForPhoneButPad(Activity a){
		boolean isDefaultPortrait = PrefManager.getInstance().getBoolean("PORTRAIT_AS_DEFAULT");
		if ( !isPad(a) && isDefaultPortrait ){
			bePortrait(a);
//			if (DeviceUtil.getDeviceWidth(a) < DeviceUtil.getDeviceHeight(a)){
//				bePortrait(a);
//			} else {
//				beLandscape(a);
//			}
		}
	}
	
	public static boolean isConnectInternet(Context c) {
		if (c == null)return false;
		ConnectivityManager conManager = 
			(ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}
	
	public static boolean isConnectWithWifi(Context c) {
		if (c == null)return false;
		ConnectivityManager conManager = 
			(ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable()) {
			return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
		}
		return false;
	}
	
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**
	 * is it the MotionEvent ev touch in the view
	 */
	public static boolean inRangeOfView(View view, MotionEvent ev){
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int x = location[0];
		int y = location[1];
		if(ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())){
		return false;
		}
		return true;
		}

	public static float getDeviceHeight(Context ct) {
		if ( ct == null )return 0;
		WindowManager wm = (WindowManager) ct.getSystemService(Context.WINDOW_SERVICE);  
	    Display display = wm.getDefaultDisplay();  
	    DisplayMetrics dm = new DisplayMetrics(); 
	    display.getMetrics(dm);
		return dm.heightPixels;
		
	}
	
	public static float getDeviceWidth(Context ct) {
		if ( ct == null )return 0;
	    WindowManager wm = (WindowManager) ct.getSystemService(Context.WINDOW_SERVICE);  
	    Display display = wm.getDefaultDisplay();  
	    DisplayMetrics dm = new DisplayMetrics(); 
	    display.getMetrics(dm);
		return dm.widthPixels;
	}
	@SuppressLint("NewApi")
	public static void setAlpha(View view, float alpha)
	{
	    if (Build.VERSION.SDK_INT < 11)
	    {
	        final AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
	        animation.setDuration(0);
	        animation.setFillAfter(true);
	        view.startAnimation(animation);
	    } else { 
	    	view.setAlpha(alpha);
	    }
	}
	
	public static void setAutoLink(TextView textview,String url ){
		if ( !StringUtil.isValidURL(url) )return;
		SpannableStringBuilder ssb = new SpannableStringBuilder(url);
		UnderlineSpan uls = new UnderlineSpan();
		ssb.setSpan(uls, 0,url.length(), 0);
		textview.setText(ssb);
		textview.setTextColor(Color.BLUE);
	}
}
