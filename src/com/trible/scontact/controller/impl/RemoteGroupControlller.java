package com.trible.scontact.controller.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.trible.scontact.controller.IGroupControl;
import com.trible.scontact.pojo.GroupInfo;

public class RemoteGroupControlller implements IGroupControl{
	
	public Context mContext;

	public RemoteGroupControlller(Context c){
		mContext = c;
	}
	@Override
	public List<GroupInfo> getGroupInfoList(int userid) {
		List<GroupInfo> gInfos = new ArrayList<GroupInfo>();
		return gInfos;
	}

	@Override
	public boolean deleteGroupById(int groupId, int userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createGroup(GroupInfo info, int userId) {
		return false;
	}

	@Override
	public boolean updateGroup(GroupInfo uInfo, int userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean joinGroup(int groupId, int userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean exitGroup(int groupId, int userId) {
		// TODO Auto-generated method stub
		return false;
	}

}
