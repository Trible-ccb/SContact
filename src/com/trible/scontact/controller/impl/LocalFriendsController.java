package com.trible.scontact.controller.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.trible.scontact.controller.IFriendsControl;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.value.GlobalValue;

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
	public List<AccountInfo> getFriendsListByGroupId(long groupId) {
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
  
        List<AccountInfo> contactList = new ArrayList<AccountInfo>();  
  
        while (cursor.moveToNext()) {  
            // RAW_CONTACT_ID  
            int col = cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID);  
            int raw_contact_id = cursor.getInt(col);  
  
            AccountInfo ce = new AccountInfo();  
  
//            ce.setContactId(raw_contact_id);
            ce.setId((long) raw_contact_id);
  
            Cursor dataCursor = mContext.getContentResolver().query(dataUri,  
                    null, "raw_contact_id=?",  
                    new String[] { raw_contact_id + "" }, null);  
  
            List<ContactInfo> contacts = new ArrayList<ContactInfo>();
            while (dataCursor.moveToNext()) {  
                String data1 = dataCursor.getString(dataCursor  
                        .getColumnIndex("data1"));  
                String mime = dataCursor.getString(dataCursor  
                        .getColumnIndex("mimetype"));
                int id = dataCursor.getInt(dataCursor.getColumnIndex("_id"));
                if( mime != null ){
                	int idx = mime.lastIndexOf("/");
                	if ( idx != -1 ){
                		mime = mime.substring(idx);
                	}
                }
                if ( mime != null && !TextUtils.isEmpty(data1) ){
                	if (mime.toLowerCase().contains("phone")) {
                    	ContactInfo ci = new ContactInfo();
                    	ci.setContact(data1);
                    	contacts.add(ci);
                    	ci.setType(GlobalValue.CTYPE_PHONE);
                    	ci.setId((long) id);
                    } else if (mime.toLowerCase().contains("name")) {
                    	ce.setDisplayName(data1); 
                    } else if (mime.toLowerCase().contains("email")) {
                    	ContactInfo ci = new ContactInfo();
                    	ci.setContact(data1);
                    	contacts.add(ci);
                    	ci.setId((long) id);
                    	ci.setType(GlobalValue.CTYPE_EMAIL);
                    } else if (mime.toLowerCase().contains("im")) {
                    	ContactInfo ci = new ContactInfo();
                    	ci.setContact(data1);
                    	contacts.add(ci);
                    	ci.setId((long) id);
                    	ci.setType("IM");
                    	ci.setType(GlobalValue.CTYPE_IM);
                    }
                }
            }
            ce.setContactsList(contacts);
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

}
