package com.trible.scontact.controller.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
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

	@Override
	public List<AccountInfo> getPhoneFriendsList() {
		ContentResolver resolver = mContext.getContentResolver();  
	    // 获取手机联系人  
	    Cursor phoneCursor = resolver.query(
	    		Phone.CONTENT_URI,null, null, null, " sort_key asc ");  
	    List<AccountInfo> contactList = new ArrayList<AccountInfo>();   
	    Set<String> unique = new HashSet<String>();
	    
	    if (phoneCursor != null) {  
	        while (phoneCursor.moveToNext()) {
//	        	[data_version, phonetic_name, data_set, phonetic_name_style, contact_id, lookup,
//	        	 data12, data11, data10, mimetype, data15, data14, data13,
//	        	 display_name_source, photo_uri, data_sync1, data_sync3,
//	        	 data_sync2, contact_chat_capability, data_sync4,
//	        	 account_type, account_type_and_data_set, custom_ringtone,
//	        	 photo_file_id, has_phone_number, nickname, status, data1,
//	        	 chat_capability, data4, data5, data2, data3, data8, data9, data6,
//	        	 group_sourceid, times_used, account_name, data7, display_name,
//	        	 raw_contact_is_user_profile, in_visible_group, display_name_alt,
//	        	 company, contact_account_type, contact_status_res_package, is_primary,
//	        	 contact_status_ts, raw_contact_id, times_contacted, contact_status, 
//	        	 status_res_package, status_icon, contact_status_icon, version, mode, 
//	        	 last_time_contacted, contact_last_updated_timestamp, res_package, _id,
//	        	 name_verified, dirty, status_ts, is_super_primary, photo_thumb_uri, 
//	        	 photo_id, send_to_voicemail, name_raw_contact_id, contact_status_label,
//	        	 status_label, sort_key_alt, starred, sort_key, contact_presence, sourceid, last_time_used]
	        //得到手机号码  
	        String phoneNumber = phoneCursor.getString(
	        		phoneCursor.getColumnIndex("data1"));  
	        //当手机号码为空的或者为空字段 跳过当前循环  
	        if (TextUtils.isEmpty(phoneNumber) || unique.contains(phoneNumber) )  
	            continue;
	        unique.add(phoneNumber);
	        AccountInfo tmp = new AccountInfo();
	        List<ContactInfo> contacts = new ArrayList<ContactInfo>();
	        ContactInfo ci = new ContactInfo();
	        //得到联系人名称  
	        String displayName = phoneCursor.getString(
	        		phoneCursor.getColumnIndex("display_name"));  
	        //得到联系人ID  
	        Long contactid = phoneCursor.getLong(
	        		phoneCursor.getColumnIndex("raw_contact_id"));  
	      
	        //得到联系人头像ID  
	        Long photoid = phoneCursor.getLong(
	        		phoneCursor.getColumnIndex("photo_id"));  
	        Long lasttime = phoneCursor.getLong(
	        		phoneCursor.getColumnIndex("last_time_contacted"));  
	        String mime = phoneCursor.getString(phoneCursor  
                    .getColumnIndex("mimetype"));
	        if( mime != null ){
            	int idx = mime.lastIndexOf("/");
            	if ( idx != -1 ){
            		mime = mime.substring(idx);
            	}
            }
            if ( mime != null ){
            	if (mime.toLowerCase().contains("phone")) {
                	ci.setType(GlobalValue.CTYPE_PHONE);
                } else if (mime.toLowerCase().contains("email")) {
                	ci.setType(GlobalValue.CTYPE_EMAIL);
                } else if (mime.toLowerCase().contains("im")) {
                	ci.setType(GlobalValue.CTYPE_IM);
                }
            }
	        //得到联系人头像Bitamp  
//	        Bitmap contactPhoto = null;  
//	      
//	        //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的  
//	        if(photoid > 0 ) {  
//	            Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);  
//	            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);  
//	            contactPhoto = BitmapFactory.decodeStream(input);  
//	        }else {  
//	            contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.contact_photo);  
//	        }  
	          ci.setId(contactid);
	          ci.setContact(phoneNumber);
	          ci.setLastestUsedTime(lasttime);
	          contacts.add(ci);
	          tmp.setDisplayName(displayName);
	          tmp.setId(contactid);
	          tmp.setContactsList(contacts);
	          tmp.setPhoneNumber(phoneNumber);
	          contactList.add(tmp);
	        }
	    }
	        phoneCursor.close();
		return contactList;
	}

	@Override
	public List<AccountInfo> getSIMFriendsList() {
		return null;
	}

}
