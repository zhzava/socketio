package com.odin.itms.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syni.common.util.Assert;





public class ObjectUtils {
	static Log log = LogFactory.getLog(ObjectUtils.class);
	
	/**
	 * 将一组属性值绑定到一个对象上
	 * map的key必须和对象的属性名一样或者去掉下划线并下一个字母大写之后和对象的属性命一样
	 * 如对象的一个属性名为：aaBbCc，则key要么一样或者是aa_bb_cc
	 * @author linshutao
	 * */
	public static Object bind(Object o,Map attr){
		 Field[] fields = getAllFields(o);
	    	for(int i=0;i<fields.length;i++){
	    		 String fieldName = fields[i].getName();  //属性名称
	    		 String filedType = fields[i].getType().toString(); //属性类型
	    		//去掉前面的class、inteface啊这些说明
	    		 filedType = filedType.substring(filedType.indexOf(" ")+1);
	 	           String firstLetter = fieldName.substring(0, 1).toUpperCase(); 
	 	            //得到方法名称
	 	            String setter = "set" + firstLetter + fieldName.substring(1);  
	    	        try {
	    	        	Method callMethod = o.getClass().getMethod(setter, Class.forName(filedType)); 
	    	        	Iterator iter = attr.keySet().iterator();
	    	        	while(iter.hasNext()){
	    	        		String mapkey = (String)iter.next();
	    	        		//替换下划线并下个字母大写
	    	        		String keyUpper = StringUtils.replaceAndUpper(mapkey, "_", "");
	    	        		if(keyUpper.equals(fieldName)){
	    	        			callMethod.invoke(o, new Object[] {attr.get(mapkey)});
	    	        		}
	    	        	}
	    	        } catch (Exception e) {
	     	          try {
	        	            Method callMethod = o.getClass().getSuperclass().getDeclaredMethod(setter,Class.forName(filedType));
	        	            callMethod.invoke(o, new Object[] {null});
						} catch (Exception e1) {
							log.error(e1.getMessage(),e1);  
						}  
	    	        	log.error(e.getMessage(),e);  
	    	        } 
	    	} 
		return o;
	}
	
	/**
	 * 假如value==null,返回替代值defaultValue
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static Object nullValue(Object value,Object defaultValue) {
		Assert.notNull(defaultValue);
		if(value == null)
			return defaultValue;
		return value;
	}
    /**
     * 如果value为null为其它值进行替代<BR>
     * 假如value为null,返回空字符串<BR>
     * 与nullValue(v,"")等价
     * @param value
     * @return
     */
	public static String nullValue(String value,String defaultValue) {
		return (String)nullValue((Object)value,(Object)defaultValue);
	}
    /**
     * 如果value为null为其它值进行替代<BR>
     * 假如v为null,返回defaultValue
     * @param value
     * @param defaultValue
     * @return
     */
	public static String nullValue(String value) {
		return nullValue(value,"");
	}
	
	public static boolean isEquals(Object o1,Object o2) {
		if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.equals(o2);
	}
	
	/**
	 * 根据属性名获取属性值
	 * */
    public static Object getFieldValueByName(String fieldName, Object o) {
        try {  
            String firstLetter = fieldName.substring(0, 1).toUpperCase();  
            String getter = "get" + firstLetter + fieldName.substring(1);  
            Method method = o.getClass().getMethod(getter, new Class[] {});  
            Object value = method.invoke(o, new Object[] {});  
            return value;  
        } catch (Exception e) {  
            log.error(e.getMessage(),e);  
            return null;  
        }  
    } 
    
    /**
     * 调用对象的setter方法，将Set和自定义的类型统一设置为null值
     * 
     * @author linshutao
     * */
	public static void setSetFieldToNull(Object o) {
		if(o == null) return;
		Field[] fields = getFieldsIncludeParent(o);
		if(fields == null) return;
		
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName(); // 属性名称
			Class class_ = field.getType();
			if(class_.isPrimitive()){
				continue;
			}
			String filedType = class_.toString(); // 属性类型
			// 去掉前面的class啊inteface啊这些说明
			int index = filedType.indexOf(" ");
			filedType = filedType.substring(index + 1);
			
			if (Collection.class.isAssignableFrom(class_) 
					|| o.getClass().getPackage().equals(class_.getPackage())) {
				boolean isAccessible = field.isAccessible();
				field.setAccessible(true);
				try {
					field.set(o, null);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					log.error(e.getMessage(), e);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					log.error(e.getMessage(), e);
				} finally {
					field.setAccessible(isAccessible);
				}
			}
		}
	}
	
	//该函数用到hibernate的api，暂时注释掉
/*	public static void wakeUpPersistentCollection(Object entry) {
		if(entry==null) return;
		
		Field[] fields = getFieldsIncludeParent(entry);
		if(fields == null) return;
		
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			Class class_ = field.getType();
			boolean isAccessible = field.isAccessible();
			try {
				field.setAccessible(true);
				Object o = field.get(entry);
				if(null == o) continue;
				if (Collection.class.isAssignableFrom(class_) 
						|| o.getClass().getPackage().equals(class_.getPackage())) {
					if(PersistentCollection.class.isAssignableFrom(o.getClass())){
						if(Collection.class.isAssignableFrom(o.getClass()))
							((Collection)o).size();
					}
					Iterator it = ((Collection)o).iterator();
					Set set = new HashSet();
					while(it.hasNext()){
						set.add(it.next());
					}
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (Exception e) {
				
			}finally {
				field.setAccessible(isAccessible);
			}
		}
		
	}*/
	
	
    
    /**
     * 调用对象的Set类型的getter方法，可用于实例化集合，
     * 避免hibernate的lazy=true引起的懒加载异常
     * @author linshutao
     * */
	public static void callGetMethodForSetField(Object o) {
		Field[] fields = getFieldsIncludeParent(o);
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName(); // 属性名称
			String filedType = field.getType().toString(); // 属性类型
			// 去掉前面的class啊inteface啊这些说明
			filedType = filedType.substring(filedType.indexOf(" ") + 1);
			// 如果是Set类型的
			if (filedType.indexOf("Set") != -1) {
				String firstLetter = fieldName.substring(0, 1).toUpperCase();
				// 得到方法名称
				String method = "get" + firstLetter + fieldName.substring(1);
				try {
					// 先调用自身的方法
					Method callMethod = o.getClass().getMethod(method,
							new Class[] {});
					// 调用set的size用于实例化
					Set set = (Set) callMethod.invoke(o, new Object[] {});
					if (set != null) {
						set.size();
					}
				} catch (Exception e) {
					try {
						// 上面调用自身的方法如果调不到会抛异常，这里再调用父类的方法
						Method callMethod = o.getClass().getSuperclass()
								.getDeclaredMethod(method, new Class[] {});
						Set set = (Set) callMethod.invoke(o, new Object[] {});
						if (set != null) {
							set.size();
						}
					} catch (Exception e1) {
						log.error(e1.getMessage(), e1);
					}
				}
			}
		}
	} 
    
    /**
     * 获得自己和父类的属性
     * */
    public static Field[] getFieldsIncludeParent(Object o){
    	//获取自己的属性
    	Field[] fields1=o.getClass().getDeclaredFields();
    	//获得父类的属性
    	Field[] fields2=o.getClass().getSuperclass().getDeclaredFields();
    	//合并所有属性
    	Field[] fields = new Field[fields1.length+fields2.length];
    	for(int i=0;i<fields1.length;++i){
    		fields[i] = fields1[i];
    	}
    	for(int j=0;j<fields2.length;++j){
    		fields[fields1.length+j] = fields2[j];
    	}
    	return fields;
    }
    
    /**
     * 获得自己和所有继承root的父类的属性
     * @param o
     * @param root
     * @return
     */
    public static Field[] getAllFields(Object o, Class root){
    	Class clazz = o.getClass();
    	Field[] fields = getSuperFields(clazz, root);
    	return fields;
    }
    
    public static Field[] getSuperFields(Class clazz, Class root) {
    	Field[] fields = clazz.getDeclaredFields();
    	Field[] sfields = null;
    	Class sclass = clazz.getSuperclass();
    	if(sclass!=null && (root==null||root.isAssignableFrom(sclass))) {
    		sfields = getSuperFields(clazz.getSuperclass(), root);
    	}
    	int l1 = 0;
    	int l2 = 0;
    	if(fields!=null) l1=fields.length;
    	if(sfields!=null) l2=sfields.length;
    	Field[] all = new Field[l1+l2];
    	if(l1>0)
    		System.arraycopy(fields, 0, all, 0, l1);
    	if(l2>0)
    		System.arraycopy(sfields, 0, all, l1, l2);
    	return all;
    }
    
    /**
     * 获得自己和所有父类的属性
     * */
    public static Field[] getAllFields(Object o){
    	Class clazz = o.getClass();
    	Field[] fields = getSuperFields(clazz);
    	return fields;
    }
    
    public static Field[] getSuperFields(Class clazz) {
    	return getSuperFields(clazz, null);
    }
    
    /**
     * 获取属性名数组
     * */
    public static  String[] getFiledName(Object o){
    	Field[] fields=o.getClass().getDeclaredFields();
       	String[] fieldNames=new String[fields.length];
    	for(int i=0;i<fields.length;i++){
    		fieldNames[i]=fields[i].getName();
    	}
    	return fieldNames;
    }
    
    /**
     * 获取属性类型(type)，属性名(name)，属性值(value)的map组成的list
     * */
    public static List getFieldsInfo(Object o){
    	Field[] fields=o.getClass().getDeclaredFields();
       	String[] fieldNames=new String[fields.length];
       	List list = new ArrayList();
       	Map infoMap=null;
    	for(int i=0;i<fields.length;i++){
    		infoMap = new HashMap();
    		infoMap.put("type", fields[i].getType().toString());
    		infoMap.put("name", fields[i].getName());
    		infoMap.put("value", getFieldValueByName(fields[i].getName(), o));
    		list.add(infoMap);
    	}
    	return list;
    }
    
    /**
     * 获取Set，属性类型(type)，属性名(name)，属性值(value)的map组成的list
     * */
    public static List getSetFiledsInfo(Object o){
    	Field[] fields=o.getClass().getDeclaredFields();
       	String[] fieldNames=new String[fields.length];
       	List list = new ArrayList();
       	Map infoMap=null;
    	for(int i=0;i<fields.length;i++){
    		if(fields[i].getType().toString().indexOf("Set")!=-1){
        		infoMap = new HashMap();
        		infoMap.put("type", fields[i].getType().toString());
        		infoMap.put("name", fields[i].getName());
        		infoMap.put("value", getFieldValueByName(fields[i].getName(), o));
        		list.add(infoMap);
    		}
    	}
    	return list;
    }
    
    /**
     * 获取对象的所有属性值，返回一个对象数组
     * */
    public static Object[] getFiledValues(Object o){
    	String[] fieldNames=getFiledName(o);
    	Object[] value=new Object[fieldNames.length];
    	for(int i=0;i<fieldNames.length;i++){
    		value[i]=getFieldValueByName(fieldNames[i], o);
    	}
    	return value;
    }
    
    public static void copyProperties(Object target, Object source) {
		try {
			BeanUtils.copyProperties(target, source);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
    public static Object copyProperties(Class targetClass, Object source) {
		Object target = null;
		try {
			target = targetClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		copyProperties(target, source);
		return target;
	}
}
