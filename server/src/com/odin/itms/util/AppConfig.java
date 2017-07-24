package com.odin.itms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.odin.itms.constant.Constants;

public class AppConfig {
	/**
	 * 
	 */
	private static final long serialVersionUID = 893965175316829440L;

	private static final Logger log = Logger.getLogger(AppConfig.class);

	//配置参数
	public static final String DATA_PATH_DATA_HOME ="data_path.data_home";
	public static final String DATA_HOME ="user_data";
	
	private static Properties environments = null;
	private static String localCtxPath = null;
	private static String data_home = null;

	static Properties properties = new Properties();
	
	static{
		init();
	}
	
	public static void addProperties(Properties properties){
		AppConfig.properties.putAll(properties);
	}
	
	public static void addProperty(String key,String value){
		properties.put(key, value);
	}
	public static void removeProperty(String key){
		properties.remove(key);
	}

	public static void init() {
		try {
			InputStream is = new FileInputStream(new File(System.getProperty("user.dir") + "/conf/Netty-Socket.properties"));
			Properties dbProps = new Properties();
			dbProps.load(is);
			AppConfig.properties.putAll(dbProps);
			log.info("读取配置"+System.getProperty("user.dir") + "/conf/Netty-Socket.properties"+"成功!");
		} catch (Exception e) {
			log.error("不能读取"+System.getProperty("user.dir") + "/conf/Netty-Socket.properties"+"配置文件");
		}
	}
	
    public static String getProperty(String key){
    	return properties.getProperty(key);
    }
    public static String getPropertyEncoding(String key,String encoding){
   	 String str = null;  
        try {  
            //进行编码转换，解决问题  
            str = new String(properties.getProperty(key).getBytes("ISO8859-1"), encoding);  
        } catch (UnsupportedEncodingException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
   	return str;
   }
    public static String getPropertyEncoding(String key,String defaultValue,String encoding){
      	 String str = null;  
           try {  
               //进行编码转换，解决问题  
        	   String value=properties.getProperty(key);
        	   if(null == value){
        		  str=defaultValue;
        	   }else{
        		   str = new String(value.getBytes("ISO8859-1"), encoding); 
        	   }
               
           } catch (UnsupportedEncodingException e) {  
               // TODO Auto-generated catch block  
               e.printStackTrace();  
           }  
      	return str;
      }
    public static String getProperty(String key,String defaultValue){
    	return properties.getProperty(key,defaultValue);
    }
	
	public static String getLocalCtxPath() {
		return localCtxPath;
	}

	public static void setLocalCtxPath(String _localCtxPath) {
		localCtxPath = _localCtxPath;		
	}
	
	public static String getDataHome() {
		
		if(null==data_home){
			String _data_home = properties.getProperty(DATA_PATH_DATA_HOME);
			if(null!=_data_home && !"".equals(_data_home))
				data_home =  _data_home;
			else
				data_home = localCtxPath + "/" + DATA_HOME;
		}
		
		return data_home;
	}	
	
	/**
	 * mht文件目录
	 */
	public static String getMhtDataHome(String entryId) {
		return getDataHome() + "/" + Constants.FOLDER_NAME_MHT + "/" + entryId;
	}
	
	/**
	 * 临时mht文件目录
	 * @param user
	 * @return
	 */
	public static String getTempMhtDataHome() {
		return getDataHome()+"/"+Constants.FOLDER_NAME_TEMPMHT;
	}
	
	/**
	 * ueditor 存放内容的内容目录
	 */
	public static String getUeditorDataHome(String entryId) {
		return getDataHome() + "/" + Constants.FOLDER_NAME_UEDITOR + "/entry/" + entryId;
	}
	
	/**
	 * 存放内容的附件目录
	 */
	public static String getAttachmentDataHome(String entryId) {
		return getDataHome() + "/" + Constants.FOLDER_NAME_ATTACHMENT + "/" + entryId;
	}
	

	/**
	 * zip文件目录
	 */
	public static String getZipDataHome(String entryId) {
		return getDataHome() + "/" + Constants.FOLDER_NAME_ZIP + "/" + entryId;
	}
	
}