package com.trible.scontact.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
/**
 * 
 * @author yuandunlong
 *
 */
public class HttpUserParamsSetter {

	public static List<NameValuePair> put(String key, String value) {

		NameValuePair keyvalue = new BasicNameValuePair(key, value);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(keyvalue);
		return list;

	}
	
	

}
