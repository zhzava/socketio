package com.odin.itms.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.odin.itms.entity.Trace;

public class FileUtil {
	private static Logger logger = Logger.getLogger(FileUtil.class);

	// 判断目录 存在，没有则创建
	public static boolean createNewDir(String path) {
		try {
			String[] paths = path.split("\\\\");
			StringBuffer fullPath = new StringBuffer();
			
			for (int i = 0; i < paths.length; i++) {
				fullPath.append(paths[i]).append("\\\\");
				System.out.println(fullPath.toString());
				File file = new File(fullPath.toString());
				if ((paths.length - 1 != i) && (!file.exists())) {
					file.mkdir();
					logger.info("创建目录" + fullPath);
				}
			}

			File file = new File(fullPath.toString());
			if (!file.exists()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.error("创建目录报错:" + Trace.getTrace(e));
		}
		return true;
	}

	// 到dir目录及其目中查找符合给定格式的文件
	public static File[] searchImgFile(File dir, final String regex) {
		// allDir中所有的目录中匹配的文件
		ArrayList<File> list = new ArrayList<File>();
		Deque<File> stack = new LinkedList<File>();
		Deque<File> allDir = new LinkedList<File>();
		if(!dir.isDirectory()){
			logger.error(dir+"目录不存在！");
			return list.toArray(new File[0]);
		}
		stack.push(dir);
		while (!stack.isEmpty()) {
			dir = stack.poll();
			allDir.push(dir);
			File[] dirs = dir.listFiles(new FileFilter() {
				public boolean accept(File arg0) {
					return false;
				}
			});
			for (File f : dirs) {
				stack.push(f);
			}
		}
		
		while (!allDir.isEmpty()) {
			File d = allDir.pop();
			File[] files = d.listFiles(new FileFilter() {
				public boolean accept(File f) {
					if (f.isDirectory()) {
						return false;
					}
					return f.getName().matches(regex);
				}
			});
			for (File f : files) {
				list.add(f);
			}
		}

		File[] arr = new File[list.size()];
		return list.toArray(arr);
	}

	// 删除指定目录下指定格式文件
	public static void deleteFile(File allList, String type) {
		File[] fileArray = allList.listFiles();
		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].isDirectory()) {
				deleteFile(fileArray[i].getAbsoluteFile(), type);
			} else if ((fileArray[i].isFile())
					&& (fileArray[i].getName().toUpperCase().endsWith(type))) {
				fileArray[i].delete();
			}
		}
	}

}
