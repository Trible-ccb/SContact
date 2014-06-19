package com.trible.scontact.thirdparty;

import android.content.Context;

import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.Tencent;
import com.trible.scontact.thirdparty.beans.LoginResponse;
import com.trible.scontact.value.GlobalValue;

public class TencentHelper {

	static Context mContext;
	static Tencent mTencent;
	public static QQAuth mQQAuth;
	
	public static void init(Context c){
		mContext = c;
		getTencent();
		LoginResponse re = LoginResponse.getFromSpf();
		if ( re != null ){
			mTencent.setOpenId(re.openid);
			mTencent.setAccessToken(re.access_token, re.expires_in);
		}
		mQQAuth = QQAuth.createInstance(mTencent.getAppId(), mContext.getApplicationContext());
	}
	public static Tencent getTencent(){
		if ( mTencent == null ){
			mTencent =  Tencent.createInstance(GlobalValue.QQAPPID, mContext.getApplicationContext());
		}
		return mTencent;
	}
	public static void setData(LoginResponse re){
		if ( re != null ){
			mTencent.setOpenId(re.openid);
			mTencent.setAccessToken(re.access_token, re.expires_in);
		}
		
	}
}
