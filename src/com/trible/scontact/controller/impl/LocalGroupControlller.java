package com.trible.scontact.controller.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Groups;

import com.trible.scontact.controller.IGroupControl;
import com.trible.scontact.models.Groupsinfo;
import com.trible.scontact.utils.Bog;

public class LocalGroupControlller implements IGroupControl{
	
	public Context mContext;

	public LocalGroupControlller(Context c){
		mContext = c;
	}
	@Override
	public List<Groupsinfo> getGroupsInfoList(int userid) {
		List<Groupsinfo> gInfos = new ArrayList<Groupsinfo>();
        Cursor cursor = null;  
        try {  
            cursor = mContext.getContentResolver()
            		.query(Groups.CONTENT_URI,  
                    null, null, null, null);  
            while (cursor.moveToNext()) {  
            	Groupsinfo ge = new Groupsinfo();  
                int groupId = cursor.getInt(
                		cursor.getColumnIndex(Groups._ID)); // 组id  
                String groupName = cursor.getString(
                		cursor.getColumnIndex(Groups.TITLE)); // 组名  
                ge.setGroupId(Long.valueOf(groupId));
                ge.setmGroupName(groupName);  
                gInfos.add(ge);  
                ge = null;  
            }  
            return gInfos;  
        } finally {  
            if (cursor != null) {  
                cursor.close();  
            }
        }
       
	}

	@Override
	public boolean deleteGroupById(int groupId, int userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createGroup(Groupsinfo info, int userId) {
		// TODO Auto-generated method stub
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
