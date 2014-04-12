package com.trible.scontact.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.Charsets;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.mozilla.universalchardet.UniversalDetector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

public class StringUtil {

	private static UniversalDetector detector = new UniversalDetector(null);

	public static String getStringForByte(byte[] bs){
		try {
			return new String(bs,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	public static String catStringFromResId(Context c, int...args){
		if ( args == null )return null;
		StringBuffer sb = new StringBuffer();
		for ( int i = 0 ; i < args.length ; i++ ){
			sb.append(c.getResources().getString(args[i]));
		}
		return sb.toString();
	}
	@SuppressLint("DefaultLocale")
	public static boolean isValidURL(String url) {
		if (TextUtils.isEmpty(url) || url.contains(" "))
			return false;
		String tmp = url.toLowerCase();
		return (tmp.startsWith("http://") || tmp.startsWith("https://"))
				&& tmp.length() > 7;
	}

	public static boolean isValidPhoneNumber(String phone) {
		String ps = "^1\\d{10}$";
		Pattern p = Pattern.compile(ps);
		Matcher m = p.matcher(phone);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param phone 
	 * @return true if phone is home number
	 */
	public static boolean isValidHomeNumber(String phone) {
		String ps = "^(?<国家代码>(\\+86)|(\\(\\+86\\)))?\\D?(?<电话号码>(?<三位区号>((010|021|022|023|024|025|026|027|028|029|852)|(\\(010\\)|\\(021\\)|\\(022\\)|\\(023\\)|\\(024\\)|\\(025\\)|\\(026\\)|\\(027\\)|\\(028\\)|\\(029\\)|\\(852\\)))\\D?\\d{8}|(?<四位区号>(0[3-9][1-9]{2})|(\\(0[3-9][1-9]{2}\\)))\\D?\\d{7,8}))(?<分机号>\\D?\\d{1,4})?$";
		Pattern p = Pattern.compile(ps);
		Matcher m = p.matcher(phone);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isValidEmail(String email) {

		String pat_string = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$";
		Pattern patten = Pattern.compile(pat_string);
		if (email != null && patten.matcher(email).matches()) {
			return true;
		}

		return false;
	}

	public static boolean isValidName(String name){
		return name == null ? false : name.matches("[^\"'%*]+");
	}
	
	public static boolean isValidPwd(String pwd){
		return pwd == null ? false : pwd.matches("[^\"']+");
	}	
	
	public static boolean isValidPhone(String phone){
		return phone == null ? false : phone.matches("[^\"'%*]+");
	}
	public static String MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = (md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	public static String getFormatDateTime(String formatString, Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatString,
				Locale.ENGLISH);
		return sdf.format(d);

	}

	public static String extracURL(String _str) {


		if (TextUtils.isEmpty(_str))
			return "";

		// Tip: add a space to the end of original string
		String str = _str.trim() + " ";

		// javascript && file: not allowed
		if (str.matches("^\\w+script:") || str.matches("^file:\\/\\/\\/.+"))
			return "";

		Pattern regex = Pattern.compile("(https?://.+?)[\\s'\"<>\\[\\]]");
		Matcher matcher = regex.matcher(str);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return "";
		}
	}

	public static String getTitle(final String s) {
		String regex;
		String title = "";
		final List<String> list = new ArrayList<String>();
		regex = "<title>.*?</title>";
		final Pattern pa = Pattern.compile(regex);
		final Matcher ma = pa.matcher(s);
		while (ma.find()) {
			list.add(ma.group());
		}
		for (int i = 0; i < list.size(); i++) {
			title = title + list.get(i);
		}
		return outTag(title);

	}

	public static String outTag(final String s) {
		return s.replaceAll("<.*?>", "");
	}

	public static String getCharset(Header[] headers) {

		String charset = "UTF-8";

		if (headers == null || headers.length == 0) {

			return charset;
		} else {

			for (Header header : headers) {
				if ("Content-Type".equalsIgnoreCase(header.getName())) {

					String value = header.getValue();

					value = StringUtils.substringAfter(value, "charset=");
					if (StringUtils.isNotBlank(value)) {
						charset = Charsets.toCharset(value).name();
					}
				}

			}
		}
		return charset;

	}

	public static String detectCharset(byte[] content) {

		detector.handleData(content, 0, content.length);
		detector.dataEnd();
		String encoding = detector.getDetectedCharset();
		detector.reset();
		if (encoding != null) {
			return encoding;
		} else {

			return Charsets.UTF_8.name();
		}
	}

	public static String replaceAll(String ss, String splitter, String replace,
			String groupString) {
		String[] stra = ss.split(groupString);
		int i = 0;
		String[] temp;

		StringBuffer sb = new StringBuffer();
		for (String s : stra) {

			if (StringUtils.isBlank(s)) {
				i++;
				continue;
			}
			// 如果�?"内的字符串则直接写入到结果集中，否则分隔，获取子字符�?
			if (i % 2 == 0) {
				temp = s.split(splitter);
				if (temp.length > 0) {
					for (String ts : temp) {
						if (StringUtils.isNotBlank(ts.trim())) {
							sb.append(ts.trim());
							sb.append(replace);
						}
					}
				}
			} else {
				sb.append(groupString);
				sb.append(s.trim());
				sb.append(groupString);
				sb.append(replace);
			}
			i++;
		}
		String sss = sb.toString();
		return StringUtils.substring(sss, 0, sss.length() - replace.length());
	}
	
	
	public static String [] spliterAll(String ss, String splitter, String replace,
			String groupString){
		
		String[] stra = ss.split(groupString);
		int i = 0;
		String[] temp;
		List<String> ret=new LinkedList<String>();
		for (String s : stra) {

			if (StringUtils.isBlank(s)) {
				i++;
				continue;
			}
			// 如果�?"内的字符串则直接写入到结果集中，否则分隔，获取子字符�?
			if (i % 2 == 0) {
				temp = s.split(splitter);
				if (temp.length > 0) {
					for (String ts : temp) {
						if (StringUtils.isNotBlank(ts.trim())) {
							ret.add(ts.trim());
						}
					}
				}
			} else {
				ret.add(s.trim());
			}
			i++;
		}
		
		return  ret.toArray(new String[ret.size()]);
	}
	
	public static String getEncodeURL(String s){
		StringBuffer sb = new StringBuffer();
		if (isValidURL(s)){
			String tmp = null;
			tmp = s.replaceAll(" ", "%20")
					.replaceAll("\\{", "%7B")
					.replaceAll("\\|", "%7C")
					.replaceAll("\\}", "%7D")
					.replaceAll("~", "%7E")
					.replaceAll("%[^((2-7)([0-9|A-F]))]", "%25");
			for ( int i = 0 ; i < tmp.length() ; i++ ){
				char c = tmp.charAt(i);
				if ( c >= 127 || c < 0 ){
					try {
						sb.append(URLEncoder.encode("" + c, "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				} else {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}
	
	public static String getEncodeURLParams(String v){
		Bog.v("before encode v = " + v);
		String ev = null;
		try {
			ev = URLEncoder.encode(v, "UTF-8");
			Bog.v("after encode v = " + ev);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ev;
	}
	public static void main(String[] args) {

		String s = "12345678900";
		Bog.v("s is home number:" + isValidHomeNumber(s));
//		System.out.println(s);
//		System.out.println(Arrays.toString((spliterAll(s, "\\s+", ",", "\""))));
////		String ss = replaceAll(s, "\\s+", ",", "\"");
//		System.out.println(Arrays.toString((spliterAll(s, ",", " ", "\""))));

	}
}
