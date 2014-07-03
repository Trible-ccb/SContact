package com.trible.scontact.database;

import java.sql.SQLException;

import com.trible.scontact.database.dao.ContactDao;
import com.trible.scontact.database.dao.ContactGroupDao;
import com.trible.scontact.database.dao.GroupDao;
import com.trible.scontact.database.dao.UserRelationDao;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.PhoneAndGroupInfo;
import com.trible.scontact.pojo.UserRelationInfo;

public class DaoMangers {

	static GroupDao mGroupDao;
	static ContactDao mContactDao;
	static ContactGroupDao mContactGroupDao;
	static UserRelationDao mUserRelationDao;
	
	
	public static void reset(){
		mGroupDao = null;
		mContactDao = null;
		mContactGroupDao = null;
		mUserRelationDao = null;
	}
	
	public static GroupDao getGroupDao(){
		if ( mGroupDao == null ){
			try {
				mGroupDao = DBHelper.getInstance().getDao(GroupInfo.class);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IllegalAccessError();
			}
		}
		return mGroupDao;
	}
	
	public static ContactDao getContactDao(){
		if ( mContactDao == null ){
			try {
				mContactDao = DBHelper.getInstance().getDao(ContactInfo.class);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IllegalAccessError();
			}
		}
		return mContactDao;
	}
	
	public static ContactGroupDao getContactGroupDao(){
		if ( mContactGroupDao == null ){
			try {
				mContactGroupDao = DBHelper.getInstance().getDao(PhoneAndGroupInfo.class);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IllegalAccessError();
			}
		}
		return mContactGroupDao;
	}
	
	public static UserRelationDao getUserRelationDao(){
		if ( mUserRelationDao == null ){
			try {
				mUserRelationDao = DBHelper.getInstance().getDao(UserRelationInfo.class);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IllegalAccessError();
			}
		}
		return mUserRelationDao;
	}
}
