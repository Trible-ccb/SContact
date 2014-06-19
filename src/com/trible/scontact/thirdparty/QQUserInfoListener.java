package com.trible.scontact.thirdparty;

import com.google.gson.Gson;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.trible.scontact.R;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.thirdparty.beans.QQUserInfo;
import com.trible.scontact.utils.Bog;

public class QQUserInfoListener implements IUiListener{

	@Override
	public void onCancel() {
		Bog.toast(R.string.cancel);
	}

	@Override
	public void onComplete(Object arg0) {
		QQUserInfo info = new Gson().fromJson(arg0.toString(), QQUserInfo.class);
		if ( info.ret == 0 && info.nickname != null ){
			info.saveToAccountInfo(AccountInfo.getInstance());
		}
	}

	@Override
	public void onError(UiError arg0) {
		
	}

}
