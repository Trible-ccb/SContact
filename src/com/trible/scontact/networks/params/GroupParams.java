package com.trible.scontact.networks.params;

import com.google.gson.Gson;
import com.trible.scontact.components.activity.SContactApplication;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.PhoneAndGroupInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.StringUtil;

public class GroupParams {

	static String mGroupPath = "/group";
	public static String GetUserGroups = SContactApplication.URL + mGroupPath + "/get_user_groups";
	public static String GetAllGroups = SContactApplication.URL + mGroupPath + "/get_all_groups";
	public static String GetGroupById = SContactApplication.URL + mGroupPath + "/get_groups";
	public static String GetUpdateGroup = SContactApplication.URL + mGroupPath + "/update_group";
	public static String GetDeleteGroup = SContactApplication.URL + mGroupPath + "/delete_group";
	public static String GetAddGroup = SContactApplication.URL + mGroupPath + "/add_group";
	public static String GetSearchGroup = SContactApplication.URL + mGroupPath + "/search_group";
	
	public static String getUserGroupParams(Long uid){
		AccountInfo info = new AccountInfo();
		info.setId(uid);
		String v = new Gson().toJson(info);
		String url = GetUserGroups + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("getUserGroupParams url = " + url);
		return url;
	}
	
	public static String getAllGroupParams( ){
		String url = GetAllGroups;
		Bog.v("getAllGroupParams url = " + url);
		return url;
	}
	
	public static String getGroupParams(Long gid){
		GroupInfo info = new GroupInfo();
		info.setId(gid);
		String v = new Gson().toJson(info);
		String url = GetGroupById + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("getGroupParams url = " + url);
		return url;
	}
	
	public static String getDeleteGroupParams(Long gid){
		GroupInfo info = new GroupInfo();
		info.setId(gid);
		String v = new Gson().toJson(info);
		String url = GetDeleteGroup + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("getDeleteGroupParams url = " + url);
		return url;
	}
	
	public static String getUpdateParams(GroupInfo info){
		String v = new Gson().toJson(info);
		String url = GetUpdateGroup + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("getUpdateParams url = " + url);
		return url;
	}
	
	public static String getAddParams(GroupInfo info,String contactIds){
		String v = new Gson().toJson(info);
		PhoneAndGroupInfo info2 = new PhoneAndGroupInfo();
		info2.setContactIds(contactIds);
		String v2 = new Gson().toJson(info2);
		String url = GetAddGroup 
				+ "?json=" + StringUtil.getEncodeURLParams(v)
				+ "&contactIds=" + StringUtil.getEncodeURLParams(v2);
		Bog.v("getAddParams url = " + url);
		return url;
	}
	
	public static String getSearchGroupParams(String query){
		GroupInfo info = new GroupInfo();
		info.setDisplayName(query);
		String v = new Gson().toJson(info);
		String url = GetSearchGroup + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("getSearchGroupParams url = " + url);
		return url;
	}
}
