package com.trible.scontact.controller.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Groups;
import android.text.TextUtils;

import com.trible.scontact.controller.IGroupControl;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.utils.Bog;

public class LocalGroupControlller implements IGroupControl{
	
	public Context mContext;

	public LocalGroupControlller(Context c){
		mContext = c;
	}
	@Override
	public List<GroupInfo> getGroupInfoList(int userid) {
		List<GroupInfo> gInfos = new ArrayList<GroupInfo>();
        Cursor cursor = null;  
        try {  
            cursor = mContext.getContentResolver()
            		.query(Groups.CONTENT_URI,  
                    null, null, null, null);  
            while (cursor.moveToNext()) {  
            	GroupInfo ge = new GroupInfo();  
                int groupId = cursor.getInt(
                		cursor.getColumnIndex(Groups._ID)); // 组id  
                String groupName = cursor.getString(
                		cursor.getColumnIndex(Groups.TITLE)); // 组名  
                ge.setId(groupId+"");
                ge.setDisplayName(groupName);
                if ( !TextUtils.isEmpty(groupName) ){
                	gInfos.add(ge);
                }
                
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
	public boolean createGroup(GroupInfo info, int userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateGroup(GroupInfo uInfo, int userId) {
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
