package com.trible.scontact.controller.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.trible.scontact.controller.IFriendsControl;
import com.trible.scontact.models.Friendinfo;

public class RemoteFriendsController implements IFriendsControl {

	
	private Context mContext;
	
	public RemoteFriendsController(Context c){
		mContext = c;
	}
	
	/** 
     * 获取某个分组下的 所有联系人信息 
     * 思路：通过组的id 去查询 RAW_CONTACT_ID, 通过RAW_CONTACT_ID去查询联系人 
        	要查询得到 data表的Data.RAW_CONTACT_ID字段 
     * @param groupId 
     * @return 
     */
	@Override
	public List<Friendinfo> getFriendsListByGroupId(long groupId) {
  
        List<Friendinfo> contactList = new ArrayList<Friendinfo>();  
  
        return contactList;  
	}

	@Override
	public boolean deleteFriend(int friendId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createFriend(Friendinfo info) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateFriend(Friendinfo uInfo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Friendinfo> searchFriends(String fName) {
		// TODO Auto-generated method stub
		return null;
	}

}
