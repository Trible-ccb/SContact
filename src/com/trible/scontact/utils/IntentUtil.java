package com.trible.scontact.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.trible.scontact.R;
import com.trible.scontact.components.adpater.TypeHandler;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.value.GlobalValue;

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
	public static void insertContact(Context c,String name,final ContactInfo ci){

		final Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setType("vnd.android.cursor.dir/person");
		intent.setType("vnd.android.cursor.dir/contact");
		intent.setType("vnd.android.cursor.dir/raw_contact");
		new TypeHandler(c) {
			@Override
			protected void onEmail() {
				intent.putExtra("email", ci.getContact()); 
			}
			@Override
			protected void onPhone() {
				intent.putExtra("phone", ci.getContact()); 
			}
		};
//		if ( ci != null ){
//			if ( GlobalValue.CTYPE_EMAIL.equals(ci.getType())){
//				
//			} else if ( GlobalValue.CTYPE_PHONE.equals(ci.getType())
//					|| GlobalValue.CTYPE_ZUOJI.equals(ci.getType()) ){
//			}
//		}
		intent.putExtra("name", name); 
		c.startActivity(intent);
	}
	public static void sendMsg(Context c,String phoneNum,String content){
		if ( TextUtils.isEmpty(phoneNum)) {  
			Bog.toast(StringUtil.catStringFromResId(
					c, R.string.phone_number_lable,R.string.invalid));
			return ;
		}
		Intent msg = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+phoneNum));
		msg.putExtra("sms_body",content);
		msg = Intent.createChooser(msg, c.getString(R.string.choose_app));
		c.startActivity(msg);
		
	}
	public static void sendEmail(Context c,String email,String tilte,String content){
		if ( !StringUtil.isValidEmail(email) )return;
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto", email, null));
		emailIntent.putExtra(Intent.EXTRA_SUBJECT,tilte);
		emailIntent.putExtra(Intent.EXTRA_TEXT,content);
		emailIntent = Intent.createChooser(emailIntent,c.getString(R.string.choose_email));
		c.startActivity(emailIntent);
		
	}
}
