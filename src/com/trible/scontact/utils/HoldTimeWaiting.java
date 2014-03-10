package com.trible.scontact.utils;

import java.util.HashMap;
import java.util.Map;

public class HoldTimeWaiting {
	private static Map<String, Long> timeHolder = new HashMap<String, Long>();
	

	/**
	 * 
	 * @param key
	 * @param time
	 *            mis
	 * @return
	 */
//	public static boolean canExecute(String key, long time) {
//
//		if (timeHolder.containsKey(key+DiigoApp.user.getId())) {
//
//			long lastExecuteTime = timeHolder.get(key+DiigoApp.user.getId());
//			long timeDis = System.currentTimeMillis() - lastExecuteTime;
//			if (timeDis <= time) {
//
//				return false;
//			} else {
//				timeHolder.put(key+DiigoApp.user.getId(), System.currentTimeMillis());
//				return true;
//			}
//
//		} else {
//
//			timeHolder.put(key+DiigoApp.user.getId(), System.currentTimeMillis());
//			return true;
//		}
//
//	}

}
