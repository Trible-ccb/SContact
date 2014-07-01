package com.trible.scontact.thirdparty.beans;

import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.thirdparty.ThirdUserInfo;
import com.trible.scontact.value.GlobalValue;

public class QQUserInfo implements ThirdUserInfo{

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
			a.setRealName(nickname);
		}
		a.setPhotoUrl(getPhotoUrl());
	}
	public String getPhotoUrl(){
		if ( figureurl_qq_2 == null || figureurl_qq_2.trim().equals("")){
			return figureurl_2;
		} else {
			return figureurl_qq_2;
		}
	}
	@Override
	public long getUserId() {
		return -1;
	}
	@Override
	public String getNickname() {
		return nickname;
	}
	@Override
	public String getDescriptioin() {
		return "";
	}
	@Override
	public int getGender() {
		if ( "男".equals(gender) ){
			return GlobalValue.UGENDER_MALE;
		} else if ("女".equals(gender)){
			return GlobalValue.UGENDER_FEMALE;
		}
		return GlobalValue.UGENDER_UNSET;
	}
}
