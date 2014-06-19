package com.trible.scontact.thirdparty;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.trible.scontact.R;
import com.trible.scontact.thirdparty.beans.LoginResponse;
import com.trible.scontact.utils.Bog;

public class QQLoginListener implements IUiListener{

	@Override
	public void onCancel() {
//		Bog.toast(R.string.cancel);
	}

	@Override
	public void onComplete(Object arg0) {
//		Bog.toast(R.string.done);
		onLoginDone(arg0.toString());
	}

	@Override
	public void onError(UiError arg0) {
		Bog.toast(arg0.errorMessage);
	}

	void onLoginDone(String json){
		LoginResponse re = LoginResponse.getFromJson(json);
		if ( re.access_token != null && re.ret == 0 ){
			re.saveToSpf();
		}
		
		Bog.i(json);
	}
}
