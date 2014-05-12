package com.trible.scontact.controller;

import com.trible.scontact.pojo.AccountInfo;


public interface IAccountControl {

	public boolean deleteUserInfo(int friendId);
	public boolean createUserInfo(AccountInfo info);
	public boolean updateUserInfo(AccountInfo uInfo);
	public boolean searchUserInfo(String fName);

	
}
