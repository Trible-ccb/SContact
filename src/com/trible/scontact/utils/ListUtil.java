package com.trible.scontact.utils;

import java.util.List;

/**
 * @author yuandunlong
 * @createAt 2014-2-25
 */
public class ListUtil {

	public static boolean isEmpty(@SuppressWarnings("rawtypes") List list) {

		boolean ret= (list == null || list.isEmpty());
		return ret;
	}
	
	public static boolean isNotEmpty(@SuppressWarnings("rawtypes") List list){
		
		return !isEmpty(list);
	}

}
