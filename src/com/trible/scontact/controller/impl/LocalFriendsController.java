package com.trible.scontact.controller.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.trible.scontact.controller.IFriendsControl;
import com.trible.scontact.models.Friendinfo;

public class LocalFriendsController implements IFriendsControl {

	
	private Context mContext;
	
	public LocalFriendsController(Context c){
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
		String[] RAW_PROJECTION = new String[] { ContactsContract.Data.RAW_CONTACT_ID, };  
		  
        String RAW_CONTACTS_WHERE = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID  
                + "=?"  
                + " and "  
                + ContactsContract.Data.MIMETYPE  
                + "="  
                + "'"  
                + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE  
                + "'";  
  
        // 通过分组的id 查询得到RAW_CONTACT_ID  
        Uri dataUri = ContactsContract.Data.CONTENT_URI;
        Cursor cursor = mContext.getContentResolver().query(  
        		dataUri, RAW_PROJECTION,  
                RAW_CONTACTS_WHERE, new String[] { groupId + "" }, "data1 asc");  
  
        List<Friendinfo> contactList = new ArrayList<Friendinfo>();  
  
        while (cursor.moveToNext()) {  
            // RAW_CONTACT_ID  
            int col = cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID);  
            int raw_contact_id = cursor.getInt(col);  
  
            Friendinfo ce = new Friendinfo();  
  
//            ce.setContactId(raw_contact_id);
            ce.setmFriendId(raw_contact_id);
  
            Cursor dataCursor = mContext.getContentResolver().query(dataUri,  
                    null, "raw_contact_id=?",  
                    new String[] { raw_contact_id + "" }, null);  
  
            StringBuffer sb = new StringBuffer();
            while (dataCursor.moveToNext()) {  
                String data1 = dataCursor.getString(dataCursor  
                        .getColumnIndex("data1"));  
                String mime = dataCursor.getString(dataCursor  
                        .getColumnIndex("mimetype"));
                if ("vnd.android.cursor.item/phone_v2".equals(mime)) {
                	sb.append(data1 + "|");
                } else if ("vnd.android.cursor.item/name".equals(mime)) {  
                    ce.setmFriendName(data1); 
                }  
            }
            if ( sb != null && sb.length() > 0 && sb.lastIndexOf("|") == sb.length() - 1){
            	ce.setmFriendNumber(sb.substring(0, sb.length() - 1 ));
            }
            dataCursor.close();  
            contactList.add(ce);  
            ce = null;  
        }
        if ( cursor != null )
        cursor.close();  
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
