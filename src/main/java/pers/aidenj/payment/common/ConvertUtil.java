package pers.aidenj.payment.common;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 类名称：实体与Map转换类 描述：实现Map与实体之间的相互转换 创建人：周化益 创建时间：2016-01-04
 */
public class ConvertUtil {
	
	/**
	 * 将一个 JavaBean 对象转化为一个 Map
	 * 
	 * @author 周化益
	 * @param bean 要转化的JavaBean 对象
	 * @return 转化出来的 Map 对象
	 */
	public static Map<String, Object> convertBean(Object bean) {
		// 获取传入的实体的Class
		Class<?> type = bean.getClass();

		// 创建接受对象
		Map<String, Object> returnMap = new HashMap<String, Object>();

		// 定义实体信息对象
		BeanInfo beanInfo;

		try {
			// 获取实体详细信息
			beanInfo = Introspector.getBeanInfo(type);

			// 获取实体属性描述集合
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				// 获取属性描述
				PropertyDescriptor descriptor = propertyDescriptors[i];

				// 获取属性名
				String propertyName = descriptor.getName();

				if (!propertyName.equals("class")) {
					// 获取属性的读取方法
					Method readMethod = descriptor.getReadMethod();

					// 通过反射获取该属性对应的值
					Object result = readMethod.invoke(bean, new Object[0]);

					/* 判断是否为空，为空则赋值空字符串 */
					if (result != null) {
						returnMap.put(propertyName, result);
					}
				}
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return returnMap;
	}

	/**
	 * 将一个Map对象转化为一个JavaBean
	 * 
	 * @author 周化益
	 * @param map 包含属性值的map
	 * @param bean 要转化的类型
	 * @return beanObj 转化出来的JavaBean对象
	 */
	public static <T> T convertMap(Class<T> clazz, Map<String, Object> paramMap) {
		// 定义返回的实体对象
		T beanObj = null;
		try {
			// 初始化返回对象
			beanObj = clazz.newInstance();

			// 定义属性名
			String propertyName = null;

			// 定义属性值
			Object propertyValue = null;
			for (Map.Entry<String, Object> entity : paramMap.entrySet()) {
				// 获取属性名
				propertyName = entity.getKey();

				// 获取属性值
				propertyValue = entity.getValue();

				// 给返回对象进行赋值
				setProperties(beanObj, propertyName, propertyValue);
			}
		} catch (IllegalArgumentException e) {
			
			throw new RuntimeException("不合法或不正确的参数", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("实例化JavaBean失败", e);
		} catch (Exception e) {
			throw new RuntimeException("Map转换为Java Bean对象失败", e);
		}

		return beanObj;
	}

	/**
	 * 给对象进行赋值
	 * 
	 * @author 周化益
	 * @param entity 赋值对象
	 * @param propertyName 属性名
	 * @param value 属性值
	 */
	private static <T> void setProperties(T entity, String propertyName,
			Object value) throws IntrospectionException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		// 获取属性描述
		PropertyDescriptor pd = new PropertyDescriptor(propertyName, entity.getClass());

		// 获取属性的赋值方法
		Method methodSet = pd.getWriteMethod();
		if (value == null) {
			methodSet.invoke(entity, value);
		} else {
			// 将值进行类型转换
			value = typeConvert(pd.getReadMethod().getReturnType(), value);

			// 通过映射将值赋值给返回实体
			methodSet.invoke(entity, value = value.equals("") ? null : value);
		}
	}

	/**
	 * 类型转换
	 * 
	 * @author 周化益
	 * @param typeClass 属性类型的class
	 * @param value 要转换的值
	 * @return 返回转换后的值
	 */
	public static Object typeConvert(Class<?> typeClass, Object value) {
		/* 进行类型判断，并转换类型 */
		if (typeClass == int.class || typeClass == Integer.class) {
			return Integer.valueOf(value.toString()).intValue();
		} else if (typeClass == String.class) {
			return value.toString();
		} else if (typeClass == Float.class) {
			return Float.valueOf(value.toString()).floatValue();
		} else if (typeClass == long.class || typeClass == Long.class) {
			return Long.valueOf(value.toString()).longValue();
		} else if (typeClass == java.sql.Date.class) {
			if (value.getClass() == String.class) {
				return java.sql.Date.valueOf(value.toString());
			} else {
				return value;
			}
		} else if (typeClass == java.util.Date.class) {
			if (value.getClass() == String.class) {
				if (value.toString().length() > 10) {
					return strToDate(value.toString(), "yyyy-MM-dd HH:mm:ss");
				} else {
					return strToDate(value.toString(), "yyyy-MM-dd");
				}
			} else {
				return value;
			}
		} else {
			return null;
		}
	}

	/**
	 * 将日期转换成字符串
	 * 
	 * @author 周化益
	 * @param date 输入的如期
	 * @param format 转换的格式
	 * @return 日期转换后的字符串
	 */
	public static String dateToStr(Date date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateString = formatter.format(date);
		return dateString;
	}
	
	/**
	 * 获取当前时间字符串格式
	 * 
	 * @author zhy
	 * @return
	 */
	public static String getNowTimeStr() {
		return dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 获取下一个月
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String dateAddOneMonth() {
		Date nowDate = new Date();
		nowDate.setMonth(nowDate.getMonth() + 1);
		return dateToStr(nowDate, "yyyy-MM-dd HH:mm:ss");
	}
	
	public static void main(String[] args) {
		System.out.println(getDateByTime("9:30:0"));
		System.out.println(dateAddOneMonth());
	}

	/**
	 * 将字符串转换成日期
	 * 
	 * @author 周化益
	 * @param dateString 输入的字符串
	 * @param format 转换的格式
	 * @return 字符串转换后的日期
	 */
	public static Date strToDate(String dateString, String format) {
		DateFormat dataformatter = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = dataformatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * String转List<T>
	 * 
	 * @author 周化益
	 * @param type List的类型
	 * @param str 传入的字符串
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> strToList(Class<T> type, String str) {
		List<T> list = null;
		try {
			if (str.trim().length() > 1) {
				Object[] strArray = str.split(",");
				List<Object> strList = new ArrayList<Object>();
				strList = Arrays.asList(strArray);
				list = new ArrayList<T>();
				for (Object obj : strList) {
					list.add((T) obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * JSONObejct转换成Map数据
	 * 
	 * @author 周化益
	 * @param json JSONObject对象数据
	 * @return
	 */
	public static Map<String, Object> jsonToMap(JSONObject json) {
		Iterator<String> keys = json.keySet().iterator();
		Map<String, Object> map = new HashMap<String, Object>();
		while(keys.hasNext()) {
			String key = keys.next();
			map.put(key, json.get(key));
		}
		return map;
	}
	
	/**
	 * Map转JSON
	 * 
	 * @author zhy
	 * @param map
	 * @return
	 */
	public static JSONObject mapToJson(Map<String, Object> map) {
		JSONObject json = new JSONObject(map);
		return json;
	}
	
	/**
	 * Map<String,Object> 转换Map<String,String>;
	 * 
	 * @author zhy
	 */
	public static Map<String,String> mObjTomString(Map<String,Object> map) {
		Map<String,String> mapString = new HashMap<String,String>();
		for (Map.Entry<String,Object> entry : map.entrySet()) {
			if(entry.getValue() != null && !entry.getValue().equals("")) {
				mapString.put(entry.getKey(), entry.getValue().toString());
			}
			
		}
		return mapString;
	};
	
	/**
	 * 根据时间获取日期
	 * 
	 * @author zhy
	 * @param times
	 * @return
	 */
	public static Date getDateByTime(String times) {
		String msDate = ConvertUtil.dateToStr(new Date(), "yyyy-MM-dd ") + times;
		return ConvertUtil.strToDate(msDate, "yyyy-MM-dd HH:mm:ss");
	}
}
