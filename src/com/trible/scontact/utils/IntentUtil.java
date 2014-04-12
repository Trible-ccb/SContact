package com.trible.scontact.utils;

import com.trible.scontact.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class IntentUtil {

	public static void call(Context c, String phoneNum){
		if ( TextUtils.isEmpty(phoneNum)) {  
			Bog.toast(StringUtil.catStringFromResId(
					c, R.string.phone_number_lable,R.string.invalid));
			return ;
		}  
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ phoneNum));
		intent = Intent.createChooser(intent, "");
		c.startActivity(intent);
	}
	
	public static void sendTo(Context c,String phoneNum){
		if ( TextUtils.isEmpty(phoneNum)) {  
			Bog.toast(StringUtil.catStringFromResId(
					c, R.string.phone_number_lable,R.string.invalid));
			return ;
		}
		Intent msg = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+phoneNum));
		msg = Intent.createChooser(msg, "");
		c.startActivity(msg);
		
	}
	
}
