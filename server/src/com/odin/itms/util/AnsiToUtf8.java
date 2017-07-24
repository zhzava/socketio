package com.odin.itms.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;

import org.apache.log4j.Logger;

public class AnsiToUtf8 {

	private static Logger logger = Logger.getLogger(AnsiToUtf8.class);
	//存储遍历获取
	private Vector<String> fileList =  new Vector<String>();
	
	//遍历指定路径下的所有文件
	//以ArrayList存储于fileList中
	private void RefreshFileList(String strPath){
		File dir = new File(strPath);
		File[] files = dir.listFiles();
		
		if(files == null){
			return;
		}
		for (int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()){
				RefreshFileList(files[i].getAbsolutePath());
			}else{
//				String strFileName = files[i].getAbsolutePath().toLowerCase();
				fileList.add(files[i].getAbsolutePath());
			}
		}
	}
	
	//过滤当前目录下的指定后缀名的文件
	public Vector<String> FileNameOfType(String strPath,String fileType){
		String strFileName =  new String();
		Vector<String> fileListOfJava =  new Vector<String>();
		
		//读取指定路径下的所有文件
		RefreshFileList(strPath);
		
		for (int i = 0; i < fileList.size(); i++) {
			strFileName = fileList.get(i).toString();
			//此处strFileName.length()-4为去除后缀名,如.xml
			strFileName = strFileName.substring(strFileName.length()-4, strFileName.length());
			if(strFileName.equals(fileType)){
				fileListOfJava.add(fileList.get(i));
			}
		}
		return fileListOfJava; 
	}
	
	//ANSI编码转UTF-8编码  filePath：文件路径
	public void ChangeFileEncoding(String filePath) throws Exception{
		File file1 = new File(filePath);
		String encoding =  new FileCharsetDetector().guessFileEncoding(file1);
		logger.info("文件编码:" + encoding);
        if(encoding.indexOf("UTF-8")== -1){
        	BufferedReader bufferedReader = null;
    		OutputStreamWriter outputStreamWriter = null;
    		String str = null;
    		
    		String allStr = "";
    		
    		//用于输入换行符的字节码
    		byte[] c = new byte[2];
    		c[0] = 0x0d;
    		c[1] = 0x0a;
    		String t = new String(c);
    		
    		FileInputStream fileInputStream = new FileInputStream(filePath);
    		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"GBK");
    		bufferedReader =  new BufferedReader(inputStreamReader);
    		while((str = bufferedReader.readLine()) != null){
    			allStr = allStr + str + t;
    		}
    		logger.info("转码后文件内容："+allStr);
    		bufferedReader.close();
    		
    		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
    		outputStreamWriter = new OutputStreamWriter(fileOutputStream,"UTF-8");
    		outputStreamWriter.write(allStr);
    		outputStreamWriter.close();
        }else{
        	
        }
	}
}
