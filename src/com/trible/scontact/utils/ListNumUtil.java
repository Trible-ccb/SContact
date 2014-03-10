package com.trible.scontact.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ListNumUtil {

	public static <T> String toArrayString(List<T> list) {

		if (list == null || list.size() == 0) {

			return "[ ]";
		}

		return list.toString();

	}

	public static List<Long> arrayString2ListLong(String args) {

		List<Long> ret = new ArrayList<Long>();
		if (StringUtils.isNotBlank(args) && args.length() > 1) {
			String sub = args.substring(1, args.length() - 1);

			for (String value : sub.split(",")) {
				if (StringUtils.isNotBlank(value)) {
					ret.add(Long.parseLong(value.trim()));
				}

			}
		}

		return ret;

	}

	public static void main(String[] args) {

		List<Long> list = new LinkedList<Long>();
		list.add(1L);
		list.add(2L);
		System.out.println(list.toString());
		System.out.println(arrayString2ListLong(list.toString()));

	}

}
