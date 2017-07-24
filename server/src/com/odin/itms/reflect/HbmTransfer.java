package com.odin.itms.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.odin.itms.util.ObjectUtils;
import com.odin.itms.util.StringUtils;


public class HbmTransfer {
	
	private static Log log = LogFactory.getLog(HbmTransfer.class);
	
	public static boolean publicOrProtected (int modifiers) {
		return Modifier.PUBLIC==modifiers||Modifier.PROTECTED==modifiers;
	}
	
	/**
	 * 根据对象自动获取Converter
	 * @param obj
	 * @return
	 */
	public static Converter getConverter(Object obj) {
		if(obj==null) return null;
		if(!(obj instanceof Class))
			obj = obj.getClass();
		return ConvertUtils.lookup((Class) obj);
	}
	
	/**
	 * 将map映射到bean中
	 * @param bean		要被映射的bean
	 * @param map		映射值来源的map
	 * @param isHumpKey	bean的字段是否是的map的key的驼峰写法
	 * @param keyType	仅在isHumpKey=true时有效！map的key的书写类型(0:全部小写；1:全部大写)
	 */
	public static void bindMapToBean(ReflectBean bean,Map<String,?> map, boolean isHumpKey, int keyType) throws IllegalArgumentException {
		if(bean==null||map==null||map.size()<1) return;
		HbmBean hb = bean.getClass().getAnnotation(HbmBean.class);
		List<String> expls = new ArrayList<String>();
		if(hb!=null) {
			String[] eps = hb.exposeFields();
			if(eps!=null) {
				expls.addAll(Arrays.asList(eps));
			}
		}
		
		Field[] fields = ObjectUtils.getAllFields(bean,ReflectBean.class);
		
		for(Field field:fields) {
			try {
				String name = field.getName();
				if(expls.contains(name)) 
					continue;
				HbmType mt = field.getAnnotation(HbmType.class);
				boolean isEps = false;
				String alias = null;
				String[] mapNames = null;
				String converter = null;
				if(mt!=null){
					isEps = mt.expose();
					if(isEps)
						continue;
					alias = mt.alias();
					converter = mt.converter();
					mapNames = mt.mapNames();
				}
				String firstLetter = name.substring(0, 1).toUpperCase();
				// 得到方法名称
				String setter = "set" + firstLetter + name.substring(1);
				Method callMethod = bean.getClass().getMethod(setter, field.getType());
				if(callMethod==null||!publicOrProtected(callMethod.getModifiers())) 
					continue;
				String key = name;
				if(isHumpKey) {
					key = StringUtils.deHumpString(name, keyType);
				}
				Object o = map.get(key);
				if(o==null) {
					//尝试使用别称
					if(!StringUtils.isEmpty(alias)) {
						key = alias;
						if(isHumpKey)
							key = StringUtils.deHumpString(alias, keyType);
						o = map.get(key);
					}
					//尝试使用可选的key
					if(o==null&&mapNames!=null&&mapNames.length>0) {
						for(String k : mapNames) {
							o = map.get(k);
							if(o!=null)
								break;
						}
					}
				}
				if(o==null||field.getType().isAssignableFrom(o.getClass())) {
					callMethod.invoke(bean, new Object[]{o});
				} else {
					Converter cv = null;
					if(!StringUtils.isEmpty(converter)) {
						Class<Converter> classObj = null;
						try {
							classObj = (Class<Converter>) Class.forName(converter);
							cv = classObj.newInstance();
						} catch (Exception e1) {
							log.error("converter : " + converter + " not find! \n" + e1.getMessage(), e1);
							cv = getConverter(field.getType());
						}
					} else {
						cv = getConverter(field.getType());
					}
					if(cv!=null) {
						try{
							Object co = cv.convert(null, o);
							o = co;
						}catch(ConversionException e) {
							log.error(e, e);
						}
						callMethod.invoke(bean, new Object[]{o});
					}
				} 
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				//log.error(e.getMessage(), e);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * 将bean映射到map中
	 * @param bean		映射值来源的bean
	 * @param map		要被映射的map
	 * @param isHumpKey	bean的字段是否是的map的key的驼峰写法
	 * @param keyType	仅在isHumpKey=true时有效！map的key的书写类型(0:全部小写；1:全部大写；其它：不变)
	 */
	public static void bindBeanToMap(ReflectBean bean,Map<String,Object> map, boolean isHumpKey, int keyType) {
		if(bean==null||map==null) return;
		HbmBean hb = bean.getClass().getAnnotation(HbmBean.class);
		List<String> expls = new ArrayList<String>();
		if(hb!=null) {
			String[] eps = hb.exposeFields();
			if(eps!=null) {
				expls.addAll(Arrays.asList(eps));
			}
		}
		
		Field[] fields = ObjectUtils.getAllFields(bean,ReflectBean.class);
		
		for(Field field:fields) {
			try {
				String name = field.getName();
				if(expls.contains(name)) 
					continue;
				HbmType mt = field.getAnnotation(HbmType.class);
				boolean isEps = false;
				String alias = null;
				if(mt!=null){
					isEps = mt.expose();
					if(isEps)
						continue;
					alias = mt.alias();
				}
				String firstLetter = name.substring(0, 1).toUpperCase();
				// 得到方法名称
				String getter = "get" + firstLetter + name.substring(1);
				Method callMethod = bean.getClass().getMethod(getter);
				if(!publicOrProtected(callMethod.getModifiers())) {
					continue;
				}
				String key = name;
				if(isHumpKey) {
					if(!StringUtils.isEmpty(alias)) {
						key = StringUtils.deHumpString(alias, keyType);
					}else{
						key = StringUtils.deHumpString(name, keyType);
					}
				}
				Object o = callMethod.invoke(bean);
				map.put(key, o);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * 将source映射到bean中
	 * @param bean		要被映射的bean
	 * @param source	映射值来源的Object
	 */
	public static void bindBeanToBean(ReflectBean bean, Object source) {
		if(bean==null||source==null) return;
		HbmBean hb = bean.getClass().getAnnotation(HbmBean.class);
		List<String> expls = new ArrayList<String>();
		if(hb!=null) {
			String[] eps = hb.exposeFields();
			if(eps!=null) {
				expls.addAll(Arrays.asList(eps));
			}
		}
		
		Field[] fields = ObjectUtils.getAllFields(bean,ReflectBean.class);
		
		for(Field field:fields) {
			try {
				String name = field.getName();
				if(expls.contains(name)) 
					continue;
				HbmType mt = field.getAnnotation(HbmType.class);
				boolean isEps = false;
				String alias = null;
				String converter = null;
				if(mt!=null){
					isEps = mt.expose();
					if(isEps)
						continue;
					alias = mt.alias();
					converter = mt.converter();
				}
				String firstLetter = name.substring(0, 1).toUpperCase();
				// 得到方法名称
				String setter = "set" + firstLetter + name.substring(1);
				Method setMethod = bean.getClass().getMethod(setter, field.getType());
				if(setMethod==null||!publicOrProtected(setMethod.getModifiers())) {
					continue;
				}
				// 得到方法名称
				String getter = "get" + firstLetter + name.substring(1);
				Method getMethod = source.getClass().getMethod(getter);
				// 如果标准的get方法不存在，尝试使用别称的get方法
				if(getMethod==null&&!StringUtils.isEmpty(alias)) {
					firstLetter = alias.substring(0, 1).toUpperCase();
					getter = "get" + firstLetter + alias.substring(1);
					getMethod = source.getClass().getMethod(getter);
				}
				if(getMethod==null||!publicOrProtected(getMethod.getModifiers())) {
					continue;
				}
				Object o = getMethod.invoke(source);
				if(o==null||field.getType().isAssignableFrom(o.getClass())) {
					setMethod.invoke(bean, new Object[]{o});
				} else {
					Converter cv = null;
					if(!StringUtils.isEmpty(converter)) {
						Class<Converter> classObj = null;
						try {
							classObj = (Class<Converter>) Class.forName(converter);
							cv = classObj.newInstance();
						} catch (Exception e1) {
							log.error("converter : " + converter + " not find! \n" + e1.getMessage(), e1);
							cv = getConverter(field.getType());
						}
					} else {
						cv = getConverter(field.getType());
					}
					if(cv!=null) {
						try{
							Object co = cv.convert(null, o);
							o = co;
						}catch(ConversionException e) {
							log.error(e, e);
						}
						setMethod.invoke(bean, new Object[]{o});
					}
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} 
		}
	}
	
}
