package com.trible.scontact.database;

public class DBConstants {

	public static final int VERSION = 1;
	public static final String DBNAME = "oo.db";
	
	public interface GroupFieldName{
		String table_name = "groups";
		String id = "id";
		String displayName = "group_display_name";
		String description = "group_description";
		String type = "group_type";
		String owner_user_id = "group_owner_user_id";
		String group_capacity = "group_capacity";
		String group_status = "group_status";
		String group_create_time = "group_create_time";
		String group_update_time = "group_update_time";
		String group_identify = "group_identify";
	}
	
	public interface AccountFieldName{
		String table_name = "accounts";
		String user_id = "user_id";
		String user_display_name = "user_display_name";
		String user_description = "user_description";
		String user_email = "user_email";
		String user_real_name = "user_real_name";
		String user_gender = "user_gender";
		String user_photo_url = "user_photo_url";
		String user_notify_id = "user_notify_id";
		String user_create_time = "user_create_time";
		String user_type = "user_type";
	}
	
	/**
	 * @author Trible Chen
	 *		a table hold the relationship between contact and group
	 */
	public interface PhoneGroupInfoFieldName{
		String table_name = "contact_group";
		String group_id = "group_id";
		String id = "id";
		String user_id = "user_id";
		String contact_ids = "contact_ids";
	}
	
	public interface UserRelationFieldName{
		String table_name = "user_relation";
		String follow_user_id = "follow_user_id";
		String id = "id";
		String user_id = "user_id";
		String contact_ids = "contact_ids";
	}
	
	public interface RawContactFieldName{
		String table_name = "contacts";
		String id = "id";
		String user_id = "user_id";
		String contact_string = "contact_string";
		String status = "status";
		String latest_used_time = "latest_used_time";
		String type = "type";
	}
}
