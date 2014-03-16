package com.trible.scontact.controller.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.trible.scontact.controller.IGroupControl;
import com.trible.scontact.models.Groupsinfo;

public class RemoteGroupControlller implements IGroupControl{
	
	public Context mContext;

	public RemoteGroupControlller(Context c){
		mContext = c;
	}
	@Override
	public List<Groupsinfo> getGroupsInfoList(int userid) {
		List<Groupsinfo> gInfos = new ArrayList<Groupsinfo>();
		return gInfos;
	}

	@Override
	public boolean deleteGroupById(int groupId, int userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createGroup(Groupsinfo info, int userId) {
		return false;
	}

	@Override
	public boolean updateGroup(Groupsinfo uInfo, int userId) {
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
