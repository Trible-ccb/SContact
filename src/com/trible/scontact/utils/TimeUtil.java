package com.trible.scontact.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtil {
	/**
	 * 
	 * @return GMT0 time in senconds
	 */
	public static long getUTCTime() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
//
//		// 3、取得夏令时差：
//		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
//
//		// 4、从本地时间里扣除这些差量，即可以取得UTC时间�?
//		cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));

		return cal.getTimeInMillis() / 1000;

	}

	public static String convertUTC2Locale(String pattern, long seconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(seconds*1000);
//		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
//
//		// 3、取得夏令时差：
//		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
//		cal.add(java.util.Calendar.MILLISECOND, (zoneOffset + dstOffset));
		
		SimpleDateFormat sdf=new SimpleDateFormat(pattern,Locale.ENGLISH);
		
        
		return sdf.format(cal.getTime());

	}

	public static void main(String[] args) {
		
		//System.out.println(getUTCTime());
		Calendar cl=Calendar.getInstance();
		cl.set(Calendar.MILLISECOND, 1387348923*1000);
		System.out.println(cl.getTime().getTimezoneOffset());
		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")); 
		System.out.println(cl.getTime().toString());
		
		System.out.println(getUTCTime());
		
		System.out.println(convertUTC2Locale("yyyy-MM-dd hh:mm:ss",getUTCTime()));

//		SimpleDateFormat foo = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		System.out.println("foo:" + foo.format(new Date()));
//
//		Calendar gc = GregorianCalendar.getInstance();
//		System.out.println("gc.getTime():" + gc.getTime());
//		System.out.println("gc.getTimeInMillis():"
//				+ new Date(gc.getTimeInMillis()));
//
//		// 当前系统默认时区的时间：
//		Calendar calendar = new GregorianCalendar();
//		System.out.print("时区�? + calendar.getTimeZone().getID() + "  ");
//		System.out.println("时间�? + calendar.get(Calendar.HOUR_OF_DAY) + ":"
//				+ calendar.get(Calendar.MINUTE));
//		// 美国洛杉矶时�?
//		TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
//		// 时区转换
//		calendar.setTimeZone(tz);
//		System.out.print("时区�? + calendar.getTimeZone().getID() + "  ");
//		System.out.println("时间�? + calendar.get(Calendar.HOUR_OF_DAY) + ":"
//				+ calendar.get(Calendar.MINUTE));
//		Date time = new Date();
//
//		// 1、取得本地时间：
//		java.util.Calendar cal = java.util.Calendar.getInstance();
//
//		// 2、取得时间偏移量�?
//		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
//
//		// 3、取得夏令时差：
//		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
//
//		// 4、从本地时间里扣除这些差量，即可以取得UTC时间�?
//		cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
//
//		// 之后调用cal.get(int x)或cal.getTimeInMillis()方法�?��得的时间即是UTC标准时间�?
//		System.out.println("UTC:" + new Date(cal.getTimeInMillis()));
//
//		Calendar calendar1 = Calendar.getInstance();
//		TimeZone tztz = TimeZone.getTimeZone("GMT");
//		calendar1.setTimeZone(tztz);
//		System.out.println(calendar.getTime());
//		System.out.println(calendar.getTimeInMillis());
	}

}
