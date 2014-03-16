package com.trible.scontact.controller;

import java.util.List;

import com.trible.scontact.models.Friendinfo;

public interface IFriendsControl {

	public List<Friendinfo> getFriendsListByGroupId(long groupid);
	public boolean deleteFriend(int friendId);
	public boolean createFriend(Friendinfo info);
	public boolean updateFriend(Friendinfo uInfo);
	public List<Friendinfo> searchFriends(String fName);
	
}
