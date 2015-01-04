package com.trible.scontact.networks.params;

import java.net.URLEncoder;

import com.google.gson.Gson;
import com.trible.scontact.components.activity.SContactApplication;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.ContactTypes;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.UserRelationInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.StringUtil;
import com.trible.scontact.value.GlobalValue;

public class AccountParams {

	static String mAccountPath = "/account";
	
	/**
	 * @param name
	 * @param password
	 * @param uuid an id got from androidpn server
	 * @return
	 */
	public static String getLoginParams(String name,String password,String uuid){
		String Login = SContactApplication.getURL() + mAccountPath + "/login";
		AccountInfo info = new AccountInfo();
		info.setDisplayName(name);
		info.setPassword(password);
//		info.setNotifyId(uuid);
		String v = new Gson().toJson(info);
		String url = Login + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("login url = " + url);
		return url;
	}
	
	public static String getLoginWithSocialParams(AccountInfo info){
		String Login = SContactApplication.getURL() + mAccountPath + "/loginWithThirdParty";
		String v = new Gson().toJson(info);
		String url = Login + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("loginWithThirdParty url = " + url);
		return url;
	}
	
	public static String getUpdateParams(AccountInfo tmp){
		String Login = SContactApplication.getURL() + mAccountPath + "/update";
		AccountInfo info = tmp;
		String v = new Gson().toJson(info);
		String url = Login + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("update url = " + url);
		return url;
	}
	
	/**
	 * @param name
	 * @param password
	 * @param phone
	 * @return 
	 */
	public static String getRegisterParams(String name,String password,String phone){
		String Register = SContactApplication.getURL() + mAccountPath + "/register";
		AccountInfo info = new AccountInfo();
		info.setDisplayName(name);
		info.setPassword(password);
		info.setPhoneNumber(phone);
		info.setType(ContactTypes.getInstance().getCellPhoneType());
		String v = new Gson().toJson(info);
		String url = Register + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("register url = " + url);
		return url;
	}
	
	/**
	 * @param id for getting the AccountInfo
	 * @return  
	 */
	public static String getAccountByIdParams(Long id){
		String GetAccount = SContactApplication.getURL() + mAccountPath + "/get_account";
		AccountInfo info = new AccountInfo();
		info.setId(id+"");
		String v = new Gson().toJson(info);
		String url = GetAccount + "?json=" + StringUtil.getEncodeURLParams(v);
		return url;
	}
	
	public static String getDeleteAccountByIdParams(Long id){
		String Delete = SContactApplication.getURL() + mAccountPath + "/delete";
		AccountInfo info = new AccountInfo();
		info.setId(id+"");
		String v = new Gson().toJson(info);
		String url = Delete + "?json=" + StringUtil.getEncodeURLParams(v);
		return url;
	}
	
	public static String getAccountByGroupIdParams(Long gid){
		String GetFromGroup = SContactApplication.getURL() + mAccountPath + "/get_accounts_of_group";
		GroupInfo info = new GroupInfo();
//		info.setId(gid);
		String v = new Gson().toJson(info);
		String url = GetFromGroup + "?json=" + StringUtil.getEncodeURLParams(v);
		return url;
	}
	public static String getAccountByContactIdParams(Long cid){
		String GetFromGroup = SContactApplication.getURL() + mAccountPath + "/get_contact_friends";
		ContactInfo info = new ContactInfo();
		info.setId(cid+"");
		String v = new Gson().toJson(info);
		String url = GetFromGroup + "?json=" + StringUtil.getEncodeURLParams(v);
		return url;
	}
	public static String getFriendsByUserIdParams(Long uid){
		String GetFromGroup = SContactApplication.getURL() + mAccountPath + "/get_friends_of_user";
		AccountInfo info = new AccountInfo();
		info.setId(uid+"");
		String v = new Gson().toJson(info);
		String url = GetFromGroup + "?json=" + StringUtil.getEncodeURLParams(v);
		return url;
	}
	public static String getCheckIsFriendsParams(Long uid,Long fid){
		String GetFromGroup = SContactApplication.getURL() + mAccountPath + "/checkIsFriend";
		UserRelationInfo info = new UserRelationInfo();
//		info.setFollowUserId(fid);
//		info.setUserId(uid);
		String v = new Gson().toJson(info);
		String url = GetFromGroup + "?json=" + StringUtil.getEncodeURLParams(v);
		return url;
	}
	
	public static String getSearchAccountInfosParams(String query){
		String GetFromGroup = SContactApplication.getURL() + mAccountPath + "/search_account_infos";
		String url = GetFromGroup + "?query=" + StringUtil.getEncodeURLParams(query);
		return url;
	}
}
