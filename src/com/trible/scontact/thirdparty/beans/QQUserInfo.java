package com.trible.scontact.thirdparty.beans;

import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.value.GlobalValue;

public class QQUserInfo {

	public int ret;
	public String nickname;
	public String gender;
	public String figureurl_2,figureurl_qq_2;
	
	public void saveToAccountInfo(AccountInfo tmp){
		AccountInfo a = tmp;
		if ( "男".equals(gender) ){
			a.setGender(GlobalValue.UGENDER_MALE);
		}
		if ( a.getDescription() == null || a.getDescription().trim().equals("") ){
			a.setDescription(nickname);
		}
		if ( figureurl_qq_2 == null || figureurl_qq_2.trim().equals("")){
			a.setPhotoUrl(figureurl_2);
		} else {
			a.setPhotoUrl(figureurl_qq_2);
		}
		
	}
}
