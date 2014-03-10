package com.trible.scontact.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

/**
 * 
 * @author yuandunlong
 * 
 */
public class BeanUtil {
	/**
	 * 
	 * @param t
	 * @return convert bean to map ,key is the fieldname of bean. if
	 *         fieldvlaue==null ingored
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<String, Object> convertBean2Map(T t) {

		Map<String, Object> map = new HashMap<String, Object>();

		if (t != null) {
			Class<T> clazz = (Class<T>) t.getClass();

			Field[] fields = clazz.getDeclaredFields();

			for (Field field : fields) {
				try {
					field.setAccessible(true);
					if (field.get(t) != null) {
						map.put(field.getName(), field.get(t));
					}
				} catch (IllegalArgumentException e) {
					// it will not appear forerver
				} catch (IllegalAccessException e) {
					// it will not appear forever due to
					// field.setAccessible(true) method
				}

			}
		}
		return map;

	}

	/**
	 * 
	 * @param t
	 * @param pattern
	 * @return if you want custom the map, set the pattern<br>
	 *         <p>
	 *         explame
	 *         </P>
	 * 
	 *         local_id:id|server_id:serverId|xxx_xxx:uuuu
	 * 
	 *         the map keys will
	 *         be{local_id=bean.id,serverId=serverId,xxx_xxx=uuuu}
	 * 
	 */
	public static <T> Map<String, Object> convertBean2Map(T t, String pattern) {
		Map<String, Object> map = new HashMap<String, Object>();

		if (t != null && pattern != null) {

			String[] keyMappings = StringUtils.split(pattern, "|");
			@SuppressWarnings("unchecked")
			Class<T> clazz = (Class<T>) t.getClass();
			Field field = null;
			for (String keyMapping : keyMappings) {
				String[] keyMap = StringUtils.split(keyMapping.trim(), ":");

				try {
					if (keyMap.length == 1) {
						field = clazz.getDeclaredField(keyMap[0].trim());
					} else if (keyMap.length == 2) {
						field = clazz.getDeclaredField(keyMap[1].trim());

					}
					field.setAccessible(true);
					Object value = field.get(t);
					map.put(keyMap[0], value);

				} catch (IllegalArgumentException e) {
					// ignored because it never happen
				} catch (IllegalAccessException e) {
					// ignored because it never happen
				} catch (NoSuchFieldException e) {
					// Dog.e(e.getMessage(), e);
					e.printStackTrace();
				}

			}

		}

		return map;
	}

	/**
	 * 
	 * @param jsonObject
	 * @param clazz
	 * @param pattern
	 * @return
	 * 
	 *         the pattern is jsonkey:fieldname<br>
	 *         the fieldName if not given default is the same as jsonkey
	 *         <p>
	 *         example
	 *         </P>
	 *         local_id:id|cmd|
	 */
	public static <T> T copyProproertyFromJSON(JSONObject jsonObject,
			Class<T> clazz, String pattern) {

		String[] patterns = StringUtils.split(pattern, "|");
		T t = null;

		try {
			t = clazz.newInstance();
		} catch (InstantiationException e1) {
			throw new RuntimeException(e1);
		} catch (IllegalAccessException e1) {
			throw new RuntimeException(e1);
		}

		for (String sigleConfig : patterns) {

			String pros[] = StringUtils.split(sigleConfig.trim(), ":");
			Field field = null;
			try {

				if (pros.length == 1) {
					field = clazz.getDeclaredField(pros[0].trim());

				}

				else if (pros.length == 2) {

					field = clazz.getDeclaredField(pros[1].trim());
				}

				if (field == null) {

					continue;
				}

				field.setAccessible(true);
				String type = field.getType().getSimpleName();
				Object value = null;
				if ("Long".equalsIgnoreCase(type)) {

					long temp = jsonObject.optLong(pros[0].trim());
					value = Long.valueOf(temp);

				}

				else if ("Integer".equalsIgnoreCase(type)) {
					int temp = jsonObject.optInt(pros[0].trim());
					value = Integer.valueOf(temp);

				} else if ("String".equalsIgnoreCase(type)) {

					value = jsonObject.optString(pros[0].trim());

				} else if ("Boolean".equalsIgnoreCase(type)) {
					boolean temp = jsonObject.optBoolean(pros[0].trim());
					value = Boolean.valueOf(temp);

				} else if ("Double".equalsIgnoreCase(pros[0].trim())) {

					double temp = jsonObject.optDouble(pros[0].trim());
					value = Double.valueOf(temp);

				}
				field.set(t, value);

			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				// ingored never happen
			}

		}

		return t;

	}

	public static void main(String[] args) {
		// Item item = new Item();
		// item.setCmd("cmdsss");
		// item.setServerId(12L);
		// Class clazz = item.getClass();
		//
		// try {
		// Field field = clazz.getDeclaredField("id");
		//
		// System.out.println(field.getGenericType());
		// System.out.println(field.getType().getSimpleName());
		// } catch (NoSuchFieldException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// Map<String, Object> map = convertBean2Map(item, "cmd");
		// for (Entry<String, Object> en : map.entrySet()) {
		// System.out.println(en.getKey() + "   " + en.getValue());
		//
		// }
	}

}
