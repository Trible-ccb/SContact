package com.trible.scontact.networks.params;

import com.google.gson.Gson;
import com.trible.scontact.components.activity.SContactApplication;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.PhoneAndGroupInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.StringUtil;

public class PhoneAndGroupParams {

	static String mPhoneAndGroupPath = "/phone_and_group";
	public static String FullAccountPath = SContactApplication.URL + mPhoneAndGroupPath;
	
	/**
	 * @param gid the special group want to join in
	 * @param contactIds the contact ids of an User's ContactInfo
	 * @return
	 */
	public static String getJoinGroupParams(Long gid,String contactIds){
		PhoneAndGroupInfo info = new PhoneAndGroupInfo();
		info.setContactIds(contactIds);
		info.setGroupId(gid);
		info.setUserId(AccountInfo.getInstance().getId());
		String v = new Gson().toJson(info);
		String url = FullAccountPath 
				+ "/join_or_update_in_group"
				+ "?json=" 
				+ StringUtil.getEncodeURLParams(v);
		Bog.v("getJoinGroupParams url = " + url);
		return url;
	}
	
	/** can not be used now this method 
	 * @param gid
	 * @param uid
	 * @return 
	 */
	public static String getExitGroupParams(Long gid,Long uid){
		PhoneAndGroupInfo info = new PhoneAndGroupInfo();
		info.setGroupId(gid);
		info.setUserId(uid);
		String v = new Gson().toJson(info);
		String url = FullAccountPath 
				+ "/exit_group"
				+ "?json=" 
				+ StringUtil.getEncodeURLParams(v);
		Bog.v("getSearchContactParams url = " + url);
		return url;
	}
	
	public static String getCheckUserInGroupParams(Long gid,Long uid){
		PhoneAndGroupInfo info = new PhoneAndGroupInfo();
		info.setGroupId(gid);
		info.setUserId(uid);
		String v = new Gson().toJson(info);
		String url = FullAccountPath 
				+ "/check_phoneandgroupInfo"
				+ "?json=" 
				+ StringUtil.getEncodeURLParams(v);
		Bog.v("checkUserInGroupParams url = " + url);
		return url;
	}
}
