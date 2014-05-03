package com.trible.scontact.controller;

import java.util.List;

import com.trible.scontact.pojo.AccountInfo;

public interface IFriendsControl {

	public List<AccountInfo> getFriendsListByGroupId(long groupid);
	public boolean deleteFriend(int friendId);
	public boolean createFriend(AccountInfo info);
	public boolean updateFriend(AccountInfo uInfo);
	public List<AccountInfo> searchFriends(String fName);
	
}
