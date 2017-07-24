package com.odin.itms.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;


import com.syni.common.util.StringUtils;

public class ApiUtils {
	/**
	  * @Title: filterMap
	  * @author zhz
	  * @Description: Hibernate过滤不规则参数
	  * @throws
	 */
	public static Map filterMap(Object obj,Map map){
		Map beanMap = transBean2Map(obj);
		Set<Map.Entry<String, String>> set = map.entrySet();
        for (Map.Entry entry : set) {  
        	if(!beanMap.containsKey(entry.getKey())){
        		map.remove(entry.getKey());
        	}
        }
		return map;
	}
	
	// Map --> Bean 2: 利用org.apache.commons.beanutils 工具类实现 Map --> Bean  
    public Object transMap2Bean(Map<String, Object> map, Object obj) {  
    	 try {  
             BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
             PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
   
             for (PropertyDescriptor property : propertyDescriptors) {  
                 String key = property.getName();  
   
                 if (map.containsKey(key)) {  
                     Object value = map.get(key);  
                     // 得到property对应的setter方法  
                     Method setter = property.getWriteMethod();  
                     setter.invoke(obj, value);  
                 }  
   
             }
   
         } catch (Exception e) {  
        	 e.printStackTrace();
             System.out.println("transMap2Bean Error " + e);  
         }  
    	 return obj;
    }  
    
    // Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map  
    public static Map<String, Object> transBean2Map(Object obj) {  
        if(obj == null){  
            return null;  
        }          
        Map<String, Object> map = new HashMap<String, Object>();  
        try {  
        	//System.out.println("|------------------transBean2Map parameter------------------|"); 
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
            for (PropertyDescriptor property : propertyDescriptors) {  
                String key = property.getName();  
                // 过滤class属性  
                if (!key.equals("class")) {  
                    // 得到property对应的getter方法  
                    Method getter = property.getReadMethod();  
                    Object value = null;  
                    if(getter==null){
                    	value = null;
                    }else{
                    	value = getter.invoke(obj);
                    }
                    //System.out.println("key:"+key+",value:"+value);
                    map.put(key, value);  
                }  
            }  
        } catch (Exception e) {  
        	e.printStackTrace();
            System.out.println("transBean2Map Error " + e);  
        }  
        return map;  
    }  
    
    
    public static Object updateBean(Object obj,Object obj1,Class<?> beanClass) throws Exception{
    	Map oriMap = transBean2Map(obj);
    	Map reqMap = transBean2Map(obj1);
    	Set<Map.Entry<String, String>> set = reqMap.entrySet();  
        for (Map.Entry entry : set) {  
        	if(oriMap.containsKey(entry.getKey())&&entry.getValue()!=null){
        		oriMap.put(entry.getKey(), entry.getValue());
        	}
        }
    	//解决TimeStamp，BigDecimal等数据为空带来的问题
        Set<Map.Entry<String, String>> setO = oriMap.entrySet();
        Map oriMaps = new HashMap();
        for (Map.Entry entry : setO){
    		if(oriMap.get(entry.getKey())!=null)
    			oriMaps.put(entry.getKey(), entry.getValue());
        }
        Object object = mapToObject(oriMaps,beanClass);
        return object;
    }
    
    /**
      * @Title: mapToObject
      * @author zhz
      * @Description: map转成object
      * @throws
     */
    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {    
        if (map == null)  
            return null;  
  
        Object obj = beanClass.newInstance();  
  
        org.apache.commons.beanutils.BeanUtils.populate(obj, map);  
  
        return obj;  
    }
    
    public static Timestamp getCurrentTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp.valueOf(sdf.format(new Date()));
		return Timestamp.valueOf(sdf.format(new Date()));
	}
    
    
    /**
      * @Title: converJson
      * @author zhz
      * @Description: 将json文件转化成Map
      * @param path(文件路径)
     */
    public static Map<String,Object> converJson(String path){
		Map<String,Object> map = new HashMap();
		try {
			File file = new File(path);
			StringBuffer sb = new StringBuffer();
		    BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));  
		    String line = null;  
		    while ((line = br.readLine()) != null) {  
		    	sb.append(line); 
		    }  
			System.out.println("当前获取到的数据为："+sb.toString());
			JSONObject dataJson = JSONObject.fromObject(sb.toString());
			map = dataJson;
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return map;
	}
    
    /**
     * 
      * @Title: checkCellphone
      * @author zhz
      * @Description: 手机号码验证
      * 移动号码段:139、138、137、136、135、134、150、151、152、157、158、159、182、183、187、188、147
      * 联通号码段:130、131、132、136、185、186、145
      * 电信号码段:133、153、180、189
      * @throws
     */
    public static boolean checkCellphone(String cellphone) {
    	String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
    	// 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cellphone);
        boolean flag = matcher.matches();
    	return flag;
	}
}
