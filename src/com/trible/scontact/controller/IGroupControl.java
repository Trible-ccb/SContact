package com.trible.scontact.controller;

import java.util.List;

import com.trible.scontact.pojo.GroupInfo;


public interface IGroupControl {

	/**get the group list belong to the user
	 * @param userid
	 * @return
	 */
	public List<GroupInfo> getGroupInfoList(int userid);
	
	/**delete the group of the user's own.also call apart group
	 * @param groupId
	 * @param userId
	 * @return
	 */
	public boolean deleteGroupById(int groupId,int userId);
	
	/**Note : every user has limited number groups 
	 * @param info
	 * @param userId
	 * @return
	 */
	public boolean createGroup(GroupInfo info,int userId);
	
	/**
	 * @param uInfo
	 * @param userId
	 * @return
	 */
	public boolean updateGroup(GroupInfo uInfo,int userId);

	/**Attention when implement this method
	 * if a user has two or more number , he can choose some of them related to the group
	 * @param groupId
	 * @param userId
	 * @return 
	 */
	public boolean joinGroup(int groupId,int userId);
	
	/**remove the relationship between group and user
	 * @param groupId
	 * @param userId
	 * @return
	 */
	public boolean exitGroup(int groupId,int userId);
	
}
