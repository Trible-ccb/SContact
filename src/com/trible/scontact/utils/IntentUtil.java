package com.trible.scontact.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.trible.scontact.R;
import com.trible.scontact.components.adpater.TypeHandler;
import com.trible.scontact.pojo.ContactInfo;

public class IntentUtil {

//	static Context mContext;
//	public static void init(Context context){
//		mContext = context;
//	}
	public static void call(Context context,String phoneNum){
		Context c = context; 
		if ( TextUtils.isEmpty(phoneNum)) {  
			Bog.toast(StringUtil.catStringFromResId(
					R.string.phone_number_lable,R.string.invalid));
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
		intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
		new TypeHandler(c) {
			@Override
			protected void onEmail() {
				intent.putExtra(ContactsContract.Intents.Insert.EMAIL, ci.getContact());
			}
			@Override
			protected void onPhone() {
				intent.putExtra(ContactsContract.Intents.Insert.PHONE, ci.getContact());
			}
		}.handle(ci.getType());
		intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
		c.startActivity(intent);
	}
	public static void sendMsg(Context context,String phoneNum,String content){
		Context c = context;
		if ( TextUtils.isEmpty(phoneNum)) {  
			Bog.toast(StringUtil.catStringFromResId(
					R.string.phone_number_lable,R.string.invalid));
			return ;
		}
		Intent msg = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+phoneNum));
		msg.putExtra("sms_body",content);
		msg = Intent.createChooser(msg, c.getString(R.string.choose_app));
		c.startActivity(msg);
		
	}
	public static void sendEmail(Context context,String email,String tilte,String content){
		Context c = context;
		if ( !StringUtil.isValidEmail(email) )return;
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto", email, null));
		emailIntent.putExtra(Intent.EXTRA_SUBJECT,tilte);
		emailIntent.putExtra(Intent.EXTRA_TEXT,content);
		emailIntent = Intent.createChooser(emailIntent,c.getString(R.string.choose_email));
		c.startActivity(emailIntent);
		
	}
}
