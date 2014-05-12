package com.trible.scontact.pojo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.StringUtil;

public class GsonHelper {

	public static <T> T getInfoFromJson(byte[] arg2,Type t){
		try {
			String content = StringUtil.getStringForByte(arg2);
			Bog.v(content);
			T result = new Gson().fromJson(content,t );
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public static <T> T getInfosFromJson(byte[] arg2 , Type t){
		String content = StringUtil.getStringForByte(arg2);
		return getInfosFromJson(content, t);
	}
	public static <T> T getInfosFromJson(String arg2 , Type t){
		try {
			String content = arg2;
			Bog.v(content);
			T tmp = new Gson().fromJson(content,t);
			return tmp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
