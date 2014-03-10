package com.trible.scontact.utils;

import android.app.Activity;
import android.content.Context;
import android.text.Selection;
import android.text.Spannable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class InputUtil {

	public static void moveInputToEnd(EditText et){
		if (et == null)return;
		et.requestFocus();
		Selection.setSelection((Spannable)et.getText(), et.getText().toString().length());
	}
	
	public static void showIME(Context activity) {
//		if (activity.getCurrentFocus() ==  null)return false;
		((InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE))
		.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
	}
	
	public static boolean hideIME(Activity activity) {
		if (activity.getCurrentFocus() ==  null)return false;
		return ((InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE))
		.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
	
	public static boolean hideIME(View v) {
		if ( v == null )return false;
		return ((InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
		.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
}
