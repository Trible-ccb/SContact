package com.trible.scontact.controller;

import java.util.List;

import com.trible.scontact.models.Groupsinfo;

public interface IGroupControl {

	public List<Groupsinfo> getGroupsInfoList(int userid);
	public boolean deleteGroupById(int groupId,int userId);
	public boolean createGroup(Groupsinfo info,int userId);
	public boolean updateGroup(Groupsinfo uInfo,int userId);
	public boolean joinGroup(int groupId,int userId);
	public boolean exitGroup(int groupId,int userId);
	
}
