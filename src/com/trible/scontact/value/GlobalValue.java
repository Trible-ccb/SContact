package com.trible.scontact.value;

import java.util.HashMap;

import com.trible.scontact.pojo.ErrorInfo;

/**
 * @author Trible Chen
 *
 */
/**
 * @author Trible Chen
 *
 */
public class GlobalValue {

	public static final int CODE_UNKOWN_ERROR = -1;
	public static final String STR_UNKOWN_ERROR = "Unkown Err";
	
	//do not forget add MESSAGE when adding message info 
	public static final int CODE_SERVER_ERROR = 1000;
	public static final int CODE_TIME_OUT = 1001;
	public static final int CODE_INVALID_REQUEST = 1002;
	public static final String STR_SERVER_ERROR = "SERVER ERROR";
	public static final String STR_TIME_OUT = "TIME OUT";
	public static final String STR_INVALID_REQUEST = "INVALID REQUEST";
	
	//for users
	private static final int CODE_USER_LEVEL = 2000;
	public static final int CODE_LOGIN_ERROR = 2001;
	public static final int CODE_REGISTER_ERROR = 2002;
	public static final int CODE_NAME_UNAVAILABAL= 2003;
	public static final int CODE_ACCOUNT_INVALID= 2004;
	public static final int CODE_DELETE_ERROR = 2005;
	public static final int CODE_GET_ACCOUNT_ERROR = 2006;
	
	public static final String STR_LOGIN_ERROR  = "LOGIN ERROR";
	public static final String STR_REGISTER_ERROR = "REGISTER ERROR";
	public static final String STR_NAME_UNAVAILABAL = "NAME UNAVAILABAL";
	public static final String STR_ACCOUNT_INVALID = "ACCOUNT IS INVALID";
	public static final String STR_DELETE_ERROR  = "DELETE ERROR";
	public static final String STR_GET_ACCOUNT_ERROR = "GET ACCOUNT ERROR";
	
	
	// for contacts
	public static final int CODE_CONTACT_ERROR = 3000;
	public static final String STR_CONTACT_ERROR  = "CONTACT ERROR";
	
	// for groups
	public static final int CODE_GROUP_ERROR = 4000;
	public static final String STR_GROUP_ERROR  = "GROUP ERROR";
	
	// for groups and contacts relationship
	public static final int CODE_PHONE_GROUP_ERROR = 5000;
	public static final String STR_PHONE_GROUP_ERROR  = "ERROR BETWEEN PHONE AND GROUP";
	
	public static HashMap<String, ErrorInfo> MESSAGES = new HashMap<String, ErrorInfo>();
	private static void putMessage(int code,String msg){
		MESSAGES.put(msg, new ErrorInfo(code, msg));
	}
	static{
		putMessage(CODE_UNKOWN_ERROR, STR_UNKOWN_ERROR);
		putMessage(CODE_INVALID_REQUEST, STR_INVALID_REQUEST);
		putMessage(CODE_LOGIN_ERROR, STR_LOGIN_ERROR);
		putMessage(CODE_REGISTER_ERROR,STR_REGISTER_ERROR);
		putMessage(CODE_SERVER_ERROR, STR_SERVER_ERROR);
		putMessage(CODE_TIME_OUT, STR_TIME_OUT);
		putMessage(CODE_NAME_UNAVAILABAL, STR_NAME_UNAVAILABAL);
		putMessage(CODE_ACCOUNT_INVALID, STR_ACCOUNT_INVALID);
		putMessage(CODE_DELETE_ERROR, STR_DELETE_ERROR);
		putMessage(CODE_GET_ACCOUNT_ERROR, STR_GET_ACCOUNT_ERROR);
		putMessage(CODE_CONTACT_ERROR, STR_CONTACT_ERROR);
		putMessage(CODE_GROUP_ERROR, STR_GROUP_ERROR);
		putMessage(CODE_PHONE_GROUP_ERROR, STR_PHONE_GROUP_ERROR);
	}
	
	/** ===User Static Value Start=== */
//	User Type
	public static final String UTYPE_NORMAL = "normal";
	public static final String UTYPE_DEV = "dev";
	public static final String UTYPE_LEVEL1 = "level1";
	public static final String UTYPE_LEVEL2 = "level2";
	public static final String UTYPE_NULL = null;
	
//	User status 0 normal by default, 1 deleted, 2 frozen
	public static final Integer USTATUS_NORMAL = 0;
	public static final Integer USTATUS_DELETED = 1;
	public static final Integer USTATUS_FROZEN = 2;
	public static final Integer USTATUS_NULL = null;
	public static final Integer USTATUS_SIGN_OUT = 3;
	
//	gender : 0 unset, 1 female, 2 male, 3 other
	public static final Integer UGENDER_UNSET = 0;
	public static final Integer UGENDER_FEMALE = 1;
	public static final Integer UGENDER_MALE = 2;
	public static final Integer UGENDER_OTHER = 3;
	public static final Integer UGENDER_NULL = null;
	/** ===User Static Value End=== */
	
	/** ===Contact Static Value Start=== */
//	status : 0 used, 1 deleted.
	public static final Integer CSTATUS_USED = 0;
	public static final Integer CSTATUS_DELETED = 1;
	public static final Integer CSTATUS_NULL = CSTATUS_USED;
	
//	contact types values;
	public static final String CTYPE_PHONE = "Phone";
	public static final String CTYPE_EMAIL = "Email";
	public static final String CTYPE_IM = "IM";
	public static final String CTYPE_QQ = "QQ";
	public static final String CTYPE_WEIXIN = "WeiXin";
	public static final String CTYPE_ZUOJI = "Home Phone";
	public static final String CTYPE_FB = "Facebook";
	public static final String CTYPE_TWITTER = "Twitter";
	public static final String CTYPE_WHATAPP = "WhatsApp";
	
	public static String[] CONTACT_TYPES = {
		CTYPE_PHONE,CTYPE_EMAIL,CTYPE_QQ,
		CTYPE_WEIXIN,CTYPE_ZUOJI,CTYPE_FB,
		CTYPE_TWITTER,CTYPE_WHATAPP};
	/** ===Contact Static Value End=== */
	
	/** ===Group Static Value Start=== */
//	Group Type
	public static final String GTYPE_NORMAL = "normal";
	public static final String GTYPE_DEV = "dev";
	public static final String GTYPE_LEVE1 = "level1";
	public static final String GTYPE_LEVEL2 = "level2";
	public static final String GTYPE_NULL = UTYPE_NORMAL;
	
//	status : 0 used, 1 deleted.2 frozen
	public static final Integer GSTATUS_USED = 0;
	public static final Integer GSTATUS_DELETED = 1;
	public static final Integer GSTATUS_FROZEN = 2;
	public static final Integer GSTATUS_NULL = null;
	
//	identify : 0 or null no identify ,1 need identify msg when user join in 
	public static final Integer GIDENTIFY_NONE = 0;
	public static final Integer GIDENTIFY_NEEDED = 1;
	public static final Integer GIDENTIFY_NULL = null;
	/** ===Group Static Value End=== */
	
	/**
	 * Rules for Group and User
	 * */
	public static HashMap<String, Integer> CREATE_GROUP_NUMBERS_RULE
																= new HashMap<String, Integer>();
	public static HashMap<String, Integer> GROUP_CAPACITY_RULE
																= new HashMap<String, Integer>();
	static{
		CREATE_GROUP_NUMBERS_RULE.put(UTYPE_DEV, Integer.MAX_VALUE);
		CREATE_GROUP_NUMBERS_RULE.put(UTYPE_LEVEL2,Integer.MAX_VALUE);
		CREATE_GROUP_NUMBERS_RULE.put(UTYPE_LEVEL1, 20);
		CREATE_GROUP_NUMBERS_RULE.put(UTYPE_NORMAL,3);
		CREATE_GROUP_NUMBERS_RULE.put(UTYPE_NULL, 3);
		
		GROUP_CAPACITY_RULE.put(GTYPE_DEV, 2000);
		GROUP_CAPACITY_RULE.put(GTYPE_LEVEL2, 2000);
		GROUP_CAPACITY_RULE.put(GTYPE_LEVE1, 500);
		GROUP_CAPACITY_RULE.put(GTYPE_NORMAL, 100);
		GROUP_CAPACITY_RULE.put(GTYPE_NULL, 50);
	}
	
	public static final String CONTACT_EMAIL = "chenchuibo@gmail.com";
}
