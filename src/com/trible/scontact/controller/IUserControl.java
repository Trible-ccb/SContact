package com.trible.scontact.controller;

import com.trible.scontact.models.UserInfo;


public interface IUserControl {

	public boolean deleteUserInfo(int friendId);
	public boolean createUserInfo(UserInfo info);
	public boolean updateUserInfo(UserInfo uInfo);
	public boolean searchUserInfo(String fName);

	
}
