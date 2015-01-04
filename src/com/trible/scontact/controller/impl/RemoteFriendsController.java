package com.trible.scontact.controller.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVQuery.CachePolicy;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.trible.scontact.controller.IFriendsControl;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.utils.StringUtil;

public class RemoteFriendsController implements IFriendsControl {

	
	private Context mContext;
	
	public RemoteFriendsController(Context c){
		mContext = c;
	}
	
	public interface GetAccountInfoByPhoneNumberListner{
		void getResult(AccountInfo account);
	}
	public void getAccountInfoByPhoneNumber(String number,final GetAccountInfoByPhoneNumberListner listner){
		if ( StringUtil.isValidPhoneNumber(number) ){
			AVQuery<ContactInfo> check = AVQuery.getQuery(ContactInfo.class);
			check.whereEqualTo(ContactInfo.FieldName.contact, number);
			check.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			check.include(ContactInfo.FieldName.owner);
			check.getFirstInBackground(new GetCallback<ContactInfo>() {
				@Override
				public void done(ContactInfo arg0, AVException arg1) {
					if ( arg0 != null ){
						if ( listner != null ){
							listner.getResult(arg0.getOwner());
						}
					}
				}
			});
		}
	}
	/** 
     * 获取某个分组下的 所有联系人信息 
     * 思路：通过组的id 去查询 RAW_CONTACT_ID, 通过RAW_CONTACT_ID去查询联系人 
        	要查询得到 data表的Data.RAW_CONTACT_ID字段 
     * @param groupId 
     * @return 
     */
	@Override
	public List<AccountInfo> getFriendsListByGroupId(long groupId) {
  
        List<AccountInfo> contactList = new ArrayList<AccountInfo>();  
  
        return contactList;  
	}

	@Override
	public boolean deleteFriend(int friendId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createFriend(AccountInfo info) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateFriend(AccountInfo uInfo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<AccountInfo> searchFriends(String fName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AccountInfo> getPhoneFriendsList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AccountInfo> getSIMFriendsList() {
		// TODO Auto-generated method stub
		return null;
	}

}
