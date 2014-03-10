package com.trible.scontact.utils;

import java.lang.reflect.InvocationTargetException;

import android.app.Activity;
import android.content.Context;
import android.text.Selection;
import android.text.Spannable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ZoomButtonsController;

public class WebViewCompaUtil {

	public static void setDisplayZoomControl(WebView mWebview,boolean flg){
		  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			    // Use the API 11+ calls to disable the controls
			    mWebview.getSettings().setDisplayZoomControls(flg);
			  } else {
			    ZoomButtonsController zoom_controll;
				try {
					zoom_controll = (ZoomButtonsController) mWebview.getClass().getMethod("getZoomButtonsController").invoke(mWebview, null);
					if ( zoom_controll != null ){
						if (flg){
							zoom_controll.getContainer().setVisibility(View.VISIBLE);
						} else {
							zoom_controll.getContainer().setVisibility(View.GONE);
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}

			  }
	}
	

	public static boolean canZoomIn(WebView mWebview){
		  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			    // Use the API 11+ calls to disable the controls
		    return mWebview.canZoomIn();
		  } else {
			  return false;

		  }
	}
	public static boolean canZoomOut(WebView mWebview){
		  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			    // Use the API 11+ calls to disable the controls
		    return mWebview.canZoomOut();
		  } else {
			  return false;

		  }
	}
}
