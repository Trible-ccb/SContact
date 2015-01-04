package com.trible.scontact.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;

import android.content.Context;
import android.text.TextUtils;

public class StringUtil {

	static Context mContext;
	public static void init(Context context){
		mContext = context;
	}
	public static String getStringForByte(byte[] bs){
		if ( bs == null )return "";
		try {
			return new String(bs,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	public static String catStringFromResId( int...args){
		Context c = mContext;
		if ( args == null )return null;
		StringBuffer sb = new StringBuffer();
		for ( int i = 0 ; i < args.length ; i++ ){
			sb.append(c.getResources().getString(args[i]));
		}
		return sb.toString();
	}
	public static boolean isValidURL(String url) {
		return UrlValidator.getInstance().isValid(url);
//		if (TextUtils.isEmpty(url) || url.contains(" "))
//			return false;
//		String tmp = url.toLowerCase();
//		return (tmp.startsWith("http://") || tmp.startsWith("https://"))
//				&& tmp.length() > 7;
	}

	public static boolean isValidPhoneNumber(String phone) {
		if ( phone == null )return false;
		String ps = "^\\d{3,15}$";
		Pattern p = Pattern.compile(ps);
		Matcher m = p.matcher(phone.replaceAll("[\\+\\-\\(\\)\\s]+", ""));
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
		return EmailValidator.getInstance().isValid(email);
//		String pat_string = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$";
//		Pattern patten = Pattern.compile(pat_string);
//		if (email != null && patten.matcher(email).matches()) {
//			return true;
//		}
//
//		return false;
	}

	public static boolean isValidName(String name){
		return name == null ? false : name.matches("[^\"'%*]{2,30}");
	}
	
	public static boolean isValidPwd(String pwd){
		return pwd == null ? false : pwd.matches("[^\"']{6,20}");
	}	
	
	public static String getHexStringOfByte(byte[] bytes){
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			int val = (bytes[i]) & 0xff;
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ev;
	}
	public static String getDecodeURLParams(String v){
		Bog.v("before Decode v = " + v);
		String ev = null;
		try {
			ev = URLDecoder.decode(v, "UTF-8");
			Bog.v("after Decode v = " + ev);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ev;
	}
	
	/** 
    * 汉字转换位汉语拼音，英文字符不变 
    * @param chines 汉字 
    * @return 拼音 
    */  
    public static String converterToSpell(String chines){ 
    	if ( chines == null )return "";
        StringBuffer pinyinName = new StringBuffer("");  
        char[] nameChar = chines.toCharArray();  
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();  
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
        for (int i = 0; i < nameChar.length; i++) {  
            if (nameChar[i] > 128) {  
                try {  
                    pinyinName.append(
                    		PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0]);  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }else{  
                pinyinName.append(nameChar[i]);  
            }  
        }  
        return pinyinName.toString();  
    }  
	      
	public static void main(String[] args) {
		System.out.println(converterToSpell("欢迎来到最棒的Java中文社区"));  
		String s = "12345678900";
		Bog.v("s is home number:" + isValidHomeNumber(s));
		Bog.v("s is number:" + isValidPhoneNumber(s));
//		System.out.println(s);
//		System.out.println(Arrays.toString((spliterAll(s, "\\s+", ",", "\""))));
////		String ss = replaceAll(s, "\\s+", ",", "\"");
//		System.out.println(Arrays.toString((spliterAll(s, ",", " ", "\""))));

	}
}
