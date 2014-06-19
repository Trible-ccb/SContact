package com.trible.scontact.thirdparty.beans;

import com.google.gson.Gson;
import com.trible.scontact.managers.PrefManager;

public class LoginResponse {
	public int ret;
	public String pay_token;
	public String pf;
	public String expires_in;
	public String openid;
	public String pfkey;
	public String msg;
	public String access_token;
	
	public void saveToSpf(){
		String gson = new Gson().toJson(this, LoginResponse.class);
		PrefManager.getInstance("QQ").putString("TokenInfo", gson);
	}
	public static LoginResponse getFromSpf(){
		LoginResponse rep = getFromJson(PrefManager.getInstance("QQ").getString("TokenInfo"));
		return rep;
	}
	public static LoginResponse getFromJson(String json){
		LoginResponse rep ;
		rep = new Gson().fromJson(json,LoginResponse.class);
		return rep;
	}
}
