
package com.odin.itms.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public final class FileUtils {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(FileUtils.class);

    /**
     * Hidden default constructor.
     */
    private FileUtils() {
    }

    /**
     * Determine whether a file or directory is actually a symbolic link.
     * 
     * @param file the file or directory to check
     * @return true if so
     */
    public static boolean isLink(final File file) {
        try {
            String os = System.getProperty("os.name");
            if (os.indexOf("Windows") >= 0) {
                return false;
            }
            if (file == null || !file.exists()) {
                return false;
            } else {
                String cnnpath = file.getCanonicalPath();
                String abspath = file.getAbsolutePath();
                log.debug("comparing " + cnnpath + " and " + abspath);
                return !abspath.equals(cnnpath);
            }
        }
        catch (IOException e) {
            log.warn("could not determine whether " + file.getAbsolutePath() + " is a symbolic link", e);
            return false;
        }
    }

    /**
     * Recursively remove a directory.
     * 
     * @param sourceDir the Directory to be removed
     * 
     * @return true on success, false otherwise.
     *         <p>
     */
    public static boolean removeDir(final File sourceDir) {
        // try {
        // org.apache.commons.io.FileUtils.deleteDirectory(sourceDir);
        // } catch (IOException e) {
        // log.warn("could not delete " + sourceDir, e);
        // return false;
        // }
        // log.debug("Succesfully removed directory: " + sourceDir);
        // return true;

        if (sourceDir == null) {
            return false;
        }

        boolean allsuccess = true;
        boolean success = true;
        int nrOfFilesDeleted = 0;
        int nrOfDirsDeleted = 0;

        if (sourceDir.isDirectory()) {
            File[] files = sourceDir.listFiles();

            // I've seen listFiles return null, so be carefull, guess dir names too long for OS
            if (files == null) {
                log.warn("Something funny with '" + sourceDir + "'. Name or path too long?");
                log.warn("Could not delete '" + sourceDir + "' from cache");

                // see whether we can rename the dir
                if (sourceDir.renameTo(new File(sourceDir.getParent(), "1"))) {
                    log.warn("Renamed '" + sourceDir + "'");

                    return removeDir(sourceDir); // try again
                } else {
                    log.warn("Could not rename '" + sourceDir + "' to '" + sourceDir.getParent() + "1'");
                }

                return false;
            }

            log.debug(sourceDir + ": is a directory with " + files.length + " docs");

            for (int i = 0; i < files.length; i++) {
                log.debug("removing " + files[i]);

                if (files[i].isDirectory()) {
                    success = removeDir(files[i]);
                } else {
                    success = files[i].delete();
                }

                if (!success) {
                    log.warn("could not delete " + files[i] + " from cache");
                } else {
                    nrOfFilesDeleted++;
                }

                allsuccess = allsuccess && success;
            }

            log.debug("removing " + sourceDir);
            success = sourceDir.delete();

            if (!success) {
                log.warn("could not delete " + sourceDir + " from cache");
            } else {
                nrOfDirsDeleted++;
            }

            allsuccess = allsuccess && success;
        }

        // TODO: make this info at outer level of recursion
        log.debug("Deleted: " + nrOfDirsDeleted + " directories and " + nrOfFilesDeleted + " files from " + sourceDir);
        log.debug("Exiting removeDir for: " + sourceDir + ", " + allsuccess);

        return allsuccess;
    }

    /**
     * Determine whether File is somewhere within Directory.
     * 
     * @param file the File.
     * @param dir the Directory.
     * 
     * @return true, if so.
     */
    public static boolean isIn(final File file, final File dir) {
        if ((file == null) || !file.isFile()) {
            return false;
        }

        if ((dir == null) || !dir.isDirectory()) {
            return false;
        }

        String fileString;
        String directoryString;

        try {
            directoryString = dir.getCanonicalPath();
            fileString = file.getCanonicalPath();

            return fileString.startsWith(directoryString);
        }
        catch (IOException e) {
            log.error("Can't determine whether file is in Dir", e);
        }

        return false;
    }

    /**
     * Get the casesensitive extension (without the '.') of a file.
     * 
     * @param sourceFile the File the extension is extracted from.
     * 
     * @return extension, empty string if no extension.
     */
    public static String getExtension(final File sourceFile) {
        if (sourceFile == null) {
            return "";
        }

        // get the extension of the source file
        int index = sourceFile.getName().lastIndexOf('.');

        if (index != -1) {
            return sourceFile.getName().substring(index + 1);
        }

        return "";
    }

    /**
     * Create a new directory in the given directory, with prefix and postfix.
     * 
     * @param sourceFile the sourceFile to use for the new directory
     * @param dir the (existing) directory to create the directory in.
     * 
     * @return newly created Directory or null.
     * @throws IOException directory can't be created
     */
    public static File createTempDir(final File sourceFile, final File dir) throws IOException {
        File unZipDestinationDirectory = null;

        try {
            // get the full path (not just the name, since we could have recursed into newly created directory)
            String destinationDirectory = sourceFile.getCanonicalPath();

            log.debug("destinationDirectory: " + destinationDirectory);

            // change extension into _
            int index = destinationDirectory.lastIndexOf('.');
            String extension;

            if (index != -1) {
                extension = destinationDirectory.substring(index + 1);
                destinationDirectory = destinationDirectory.substring(0, index) + '_' + extension;
            }

            // actually create the directory
            unZipDestinationDirectory = new File(destinationDirectory);
            boolean canCreate = unZipDestinationDirectory.mkdirs();

            if (!canCreate) {
                log.warn("Could not create: " + unZipDestinationDirectory);
            }

            log.debug("Created: " + unZipDestinationDirectory + " from File: " + sourceFile);
        }
        catch (Exception e) {
            log.error("error creating directory from file: " + sourceFile, e);
        }

        return unZipDestinationDirectory;
    }

    /**
     * Get the casesensitive basename (without the '.') of a file.
     * 
     * @param sourceFile the File the basename is extracted from.
     * 
     * @return basename, entire name if no extension.
     */
    public static String getBasename(final File sourceFile) {
        if (sourceFile == null) {
            return "";
        }

        // get the basename of the source file
        int index = sourceFile.getName().lastIndexOf('.');

        if (index != -1) {
            return sourceFile.getName().substring(0, index);
        }

        return sourceFile.getName();
    }
    
    public static String getBasename(final String sourceFile) {
        if (sourceFile == null) {
            return "";
        }

        // get the basename of the source file
        int index = sourceFile.lastIndexOf('.');

        if (index != -1) {
            return sourceFile.substring(0, index);
        }

        return sourceFile;
    }
    /**
     * Get the MD5 hash (unique identifier based on contents) of a file.
     * 
     * <p>
     * N.B. This is an expensive operation, since the entire file is read.
     * </p>
     * 
     * @param sourceFile the File the MD5 hash is created from, can take null or not a normalFile
     * 
     * @return MD5 hash of file as a String, null if it can't create a hash.
     */
    public static String getMD5Hash(final File sourceFile) {
        log.debug("Getting MD5 hash for " + sourceFile);

        final char[] HEX = "0123456789abcdef".toCharArray();

        if (sourceFile == null || !sourceFile.isFile()) {
            log.error("Error creating MD5 Hash for " + sourceFile);
            return null;
        }
        BufferedInputStream bis = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // IMessageDigest md = HashFactory.getInstance("MD5");
            if (md == null) {
                log.error("Error creating MessageDigest for " + sourceFile);
                return null;
            }

            bis = new BufferedInputStream(new FileInputStream(sourceFile));
            md.reset();
            int len = 0;
            byte[] buffer = new byte[8192];
            while ((len = bis.read(buffer)) > -1) {
                md.update(buffer, 0, len);
            }

            byte[] bytes = md.digest();
            if (bytes == null) {
                log.error("MessageDigest has no bytes for " + sourceFile);

                return null;
            }

            // base64? encode the digest
            StringBuffer sb = new StringBuffer(bytes.length * 2);
            int b;
            for (int i = 0; i < bytes.length; i++) {
                b = bytes[i] & 0xFF;
                sb.append(HEX[b >>> 4]);
                sb.append(HEX[b & 0x0F]);
            }

            log.debug("MD5 hash for " + sourceFile + " is " + sb);
            return sb.toString();
        }
        catch (Exception e) {
            log.error("Can't determine MD5 hash for " + sourceFile, e);

            return null;
        }
        finally {
            if (bis != null) {
                try {
                    bis.close();
                }
                catch (IOException e) {
                    log.warn("Can't close stream for " + sourceFile, e);
                }
            }
        }
    }
    public static String getNamePart(String fileName) {
        int point = getPathLsatIndex(fileName);
        int length = fileName.length();
        if (point == -1) {
          return fileName;
        }
        else if (point == length - 1) {
          int secondPoint = getPathLsatIndex(fileName, point - 1);
          if (secondPoint == -1) {
            if (length == 1) {
              return fileName;
            }
            else {
              return fileName.substring(0, point);
            }
          }
          else {
            return fileName.substring(secondPoint + 1, point);
          }
        }
        else {
          return fileName.substring(point + 1);
        }
      }
    public static String getPathPart(String fileName) {
	    int point = getPathLsatIndex(fileName);
	    int length = fileName.length();
	    if (point == -1) {
	      return "";
	    }
	    else if (point == length - 1) {
	      int secondPoint = getPathLsatIndex(fileName, point - 1);
	      if (secondPoint == -1) {
	        return "";
	      }
	      else {
	        return fileName.substring(0, secondPoint);
	      }
	    }
	    else {
	      return fileName.substring(0, point);
	    }
	  }

	public static int getPathLsatIndex(String fileName) {
		int point = fileName.lastIndexOf('/');
		if (point == -1) {
			point = fileName.lastIndexOf('\\');
		}
		return point;
	}

	public static int getPathLsatIndex(String fileName, int fromIndex) {
		int point = fileName.lastIndexOf('/', fromIndex);
		if (point == -1) {
			point = fileName.lastIndexOf('\\', fromIndex);
		}
		return point;
	}

	/**
	 * 创建目录
	 * 
	 * @param realPath
	 */
	public static File createDirs(String realPath) {
		File file = new File(realPath);
		return createDirs(file);
	}

	/**
	 * 创建目录
	 * 
	 * @param file
	 */
	public static File createDirs(File file) {
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}
	
	/**
	 * 文件写入
	 * @param fileName
	 * @param content
	 */
	public static void writeFile(File fileFolder, String fileName, String content){
		if(content==null){
			return;
		}
		if(!fileFolder.exists()){
			fileFolder.mkdirs();
		}
		File file = new File(fileFolder, fileName);
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8")));
		    out.write(content);
		    out.flush();
		    out.close();
		} catch (IOException e) {			
			log.error(e.getMessage(),e);
		}
	}
	
	/** 
     * 文件转换为字符串 
     * @author linshutao
     * @param f 文件 
     * @param charset 文件的字符集 
     * @return 文件内容 
     */ 
	public static String file2String(File f) {
		return file2String(f, getCharset(f), -1);
	}
	
	/** 
     * 文件转换为字符串 
     * @author linshutao
     * @param f 文件 
     * @param charset 文件的字符集 
     * @return 文件内容 
     */ 
	public static String file2String(File f, String charset) {
		return file2String(f, charset, -1);
	}
	
    /** 
     * 文件转换为字符串 
     * @author linshutao
     * @param f 文件 
     * @param charset 文件的字符集 
     * @param max	最多读取的字节数
     * @return 文件内容 
     */ 
	public static String file2String(File f, String charset,int max) {
		String result = null;
		if (f != null) {
			try {
				if(charset==null){
					charset = getCharset(f);
				}
				result = stream2String(new FileInputStream(f), charset,	max);
			} catch (FileNotFoundException e) {
				log.error(e.getMessage(),e);
			}
			
		}
		return result;
	}
	
	/** 
     * 文件转换为字符串 
     * @author linshutao
     * @param in 字节流 
     * @param charset 文件的字符集 
     * @return 文件内容 
     */ 
    public static String stream2String(InputStream in, String charset) {
    	return stream2String(in, charset, -1);
    }

    /** 
     * 文件转换为字符串 
     * @author linshutao
     * @param in 字节流 
     * @param charset 文件的字符集 
     * @param max	最多读取的字节数
     * @return 文件内容 
     */ 
    public static String stream2String(InputStream in, String charset, int max) {
		StringBuffer sb = new StringBuffer();
		InputStreamReader reader = null;
		try {
			if (charset != null && charset.length() > 0) {
				reader = new InputStreamReader(in, charset);
			} else {
				reader = new InputStreamReader(in);
			}
			boolean limit = max > 0;
			int length = 0;
			char[] c = new char[1024];
			for (; (length = reader.read(c)) != -1;) {
				sb.append(c, 0, length);
				if (limit) {
					max = max - length;
					if (max <= 0) {
						break;
					}
				}
			}
			reader.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	/**
	 * @param file 要写入内容的文件
	 * @param content 文件内容
	 * @param charset 文件编码
	 * @author linshutao
	 * */
	public static File string2File(File file,String content,String charset){
			OutputStreamWriter out = null;
			try {
				if(charset!=null && charset.length()>0){
					out = new OutputStreamWriter(new FileOutputStream(file),charset);
				}else{
					out = new OutputStreamWriter(new FileOutputStream(file));
				}				
				out.write(content);
				out.flush();
				out.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(),e);
			} 
			return file;
	}
	
	/**
	 * @param file 要写入内容的文件
	 * @param content 文件内容
	 * @param charset 文件编码
	 * @author Leo
	 * */
	public static File string2File(String folderName, String fileName,String content,String charset){
		if(content==null||folderName==null||fileName==null){
			return null;
		}
		File folder = new File(folderName);
		if(!folder.exists()){
			folder.mkdirs();
		}
		File file = new File(folder, fileName);
		return string2File(file,content,charset);
	}
	
	/**
	 * 往文件尾部追加内容
	 * @param file 文件
	 * @param content 要追加的内容
	 * @author linshutao
	 * */
	public static void appendContent2File(File file, String content){
		try {
	    // 打开一个随机访问文件流，按读写方式
	    RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
	    // 文件长度，字节数
	    long fileLength = randomFile.length();
	    //将写文件指针移到文件尾。
	    randomFile.seek(fileLength);
	    randomFile.writeBytes(content);
	    randomFile.close();
	   } catch (IOException e){
	    e.printStackTrace();
	   }
	}
	
	/**
	 * 复制文件
	 * @param sourceFile 源文件
	 * @param destFile 目标文件
	 * @param ifcover 如果目标文件已经存在是否覆盖
	 * @return 如果复制成功返回true,否则返回false
	 * @author linshutao
	 * */
	public static boolean copyFile(File sourceFile,File destFile,boolean ifcover){
		if(!sourceFile.exists()){
			//log.error("源文件不存在:"+sourceFile.getAbsolutePath());
			return false;
		}
		//目标文件若已存在则覆盖，即删除掉原来的
		if(destFile.exists()){
			if(ifcover){
				destFile.delete();
			}else{
				//log.error("无法复制，目标文件已存在!");
				return false;
			}
		}
		//目标文件所在的目录
		if(!destFile.getParentFile().exists()){
			if(!destFile.getParentFile().mkdirs()){
				//log.error("创建目标文件所在目录失败!");
				return false;
			}
		}
		try {
			FileInputStream fis = new FileInputStream(sourceFile);;
			FileOutputStream fos = new FileOutputStream(destFile);	;
			byte buffer[] = new byte[1024*5];
			int readbyte = 0;
			while((readbyte=fis.read(buffer))!=-1){
				fos.write(buffer,0,readbyte);
			}
			fis.close();
			fis=null;
			fos.close();
			fos=null;
			sourceFile.delete();
			return true;
		} catch (Exception e) {
			//log.error(e.getMessage(),e);
			return false;
		}
	}
	
    /**
     * 复制文件夹
     * */ 
    public static void copyDirectiory(String sourceDir, String targetDir){
        // 新建目标目录
    	try{
            (new File(targetDir)).mkdirs();
            // 获取源文件夹当前下的文件或目录
            File[] file = (new File(sourceDir)).listFiles();
            for (int i = 0; i < file.length; i++) {
                if (file[i].isFile()) {
                    // 源文件
                    File sourceFile = file[i];
                    // 目标文件
                    File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                    copyFile(sourceFile, targetFile,true);
                }
                if (file[i].isDirectory()) {
                    // 准备复制的源文件夹
                    String dir1 = sourceDir + "/" + file[i].getName();
                    // 准备复制的目标文件夹
                    String dir2 = targetDir + "/" + file[i].getName();
                    //递归复制文件夹
                    copyDirectiory(dir1, dir2);
                }
            }
    	}catch(Exception e){
    		log.error(e.getMessage(),e);
    	}

    }
    
    /**
     * 创建文件夹（如文件夹存在，则清理）
     * @author lizhiwei
     * @param dir
     * @return
     */
    public static File cleanOrMkDirs(String dir){
    	return cleanOrMkDirs(new File(dir));
    }
    
    /**
     * 创建文件夹（如文件夹存在，则清理）
     * @author lizhiwei
     * @param saveFolder
     * @return
     */
    public static File cleanOrMkDirs(File saveFolder){
    	if(saveFolder.exists()){  //删除文件夹内文件
			File[] files = saveFolder.listFiles();
			for(int i = 0; i < files.length; i++){
				files[i].delete();
			}
		}else{		
			saveFolder.mkdirs();
		}
		return saveFolder;
    }
    
    /**
     * 简单判断文件编码格式
     * @param file
     * @return
     */
    public static String getCharset(File file) {
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		try {
			boolean checked = false;
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			bis.mark(0);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1)
				return charset;
			if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
				charset = "UTF-16LE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE
					&& first3Bytes[1] == (byte) 0xFF) {
				charset = "UTF-16BE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF
					&& first3Bytes[1] == (byte) 0xBB
					&& first3Bytes[2] == (byte) 0xBF) {
				charset = "UTF-8";
				checked = true;
			}
			bis.reset();
			if (!checked) {//
				while ((read = bis.read()) != -1) {
					if (read >= 0xF0)
						break;
					if (0x80 <= read && read <= 0xBF)// 单独出现BF以下的，也算是GBKbreak;
						if (0xC0 <= read && read <= 0xDF) {
							read = bis.read();
							if (0x80 <= read && read <= 0xBF)// 双字节(0xC0-0xDF)(0x80
								// -0xBF),也可能在GB编码内
								continue;
							else
								break;
						} else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
							read = bis.read();
							if (0x80 <= read && read <= 0xBF) {
								read = bis.read();
								if (0x80 <= read && read <= 0xBF) {
									charset = "UTF-8";
									break;
								} else
									break;
							} else
								break;
						}
				}
			}
			bis.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return charset;
	}
    
    /**
     * 保存字节数组
     * @param folder
     * @param fileName
     * @param bytes
     */
    public static void byte2File(File folder, String fileName, byte[] bytes) {
    	byte2File(new File(folder,fileName), bytes);
    }
    
    /**
     * 保存字节数组
     * @param file
     * @param bytes
     */
    public static void byte2File(File file, byte[] bytes) {
		// TODO Auto-generated method stub
		
		try {
			FileOutputStream fos=new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close(); 
		} catch (IOException e) {			
			log.error(e.getMessage(),e);
		}
	}
    
    /**
     * 获取文件（不包含目录）
     * @author lizhiwei
     * @param file
     * @param fileList
     * @param suffixs 只获取suffixs中包含的后缀文件，空表示不过滤后缀
     */
    public static void filterFileList(File file, Set<File> fileList, Set<String> suffixs) {
    	if(file.isDirectory()) {
    		File[] files = file.listFiles();
    		for(File f:files) {
    			filterFileList(f, fileList, suffixs);
    		}
    	} else if(suffixs==null||suffixs.size()<1) {
    		fileList.add(file);
    	} else {
    		String fileName = file.getName();
    		int ld = fileName.lastIndexOf(".");
    		if(ld>-1&&suffixs.contains(fileName.substring(ld+1)))
    			fileList.add(file);
    	}
    	
    }
    
    /**  
    * 删除目录（文件夹）以及目录下的文件  
    * @param   sPath 被删除目录的文件路径  
    * @return  目录删除成功返回true，否则返回false  
    */  
    public static boolean deleteDirectory(String sPath) {   
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符   
    	boolean flag = false;
        if (!sPath.endsWith(File.separator)) {   
           sPath = sPath + File.separator;   
        }   
        File dirFile = new File(sPath);   
        //如果dir对应的文件不存在，或者不是一个目录，则退出   
        if (!dirFile.exists() || !dirFile.isDirectory()) {   
            return false;   
        }   
        flag = true;   
        //删除文件夹下的所有文件(包括子目录)   
        File[] files = dirFile.listFiles();   
        for (int i = 0; i < files.length; i++) {   
            //删除子文件   
            if (files[i].isFile()) {   
                flag = deleteFile(files[i].getAbsolutePath());   
                if (!flag) break;   
            } //删除子目录   
            else {   
                flag = deleteDirectory(files[i].getAbsolutePath());   
                if (!flag) break;   
            }   
        }   
        if (!flag) return false;   
        //删除当前目录   
        if (dirFile.delete()) {   
            return true;   
        } else {   
            return false;   
        }   
    }
    
    /**  
     * 删除单个文件  
     * @param   sPath    被删除文件的文件名  
     * @return 单个文件删除成功返回true，否则返回false  
     */  
    public static boolean deleteFile(String sPath) {   
    	boolean flag = false;
        File file = new File(sPath);   
        // 路径为文件且不为空则进行删除   
        if (file.isFile() && file.exists()) {   
        	
            file.getAbsoluteFile().delete();
            //System.out.println("刪除成功！");
            flag = true;   
        }   
        return flag;   
    } 
    
    /**
     * 读取某个文件夹下的所有文件
     * @param filepath 文件夹的路径  
     */
    public static List getFileList(String filepath,String regex) throws FileNotFoundException, IOException {
    	List list = new ArrayList();
    	try {
            File file = new File(filepath);
            if (!file.isDirectory()) {
                //System.out.println("文件");
                //System.out.println("path=" + file.getPath());
                //System.out.println("absolutepath=" + file.getAbsolutePath());
                //System.out.println("name=" + file.getName());
                list.add(file.getName());
            } else if (file.isDirectory()) {
                //System.out.println("文件夹");
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(filepath + "\\" + filelist[i]);
                    if (!readfile.isDirectory()) {
                        //System.out.println("path=" + readfile.getPath());
                        //System.out.println("absolutepath=" + readfile.getAbsolutePath());
                        if(readfile.getName().matches(regex)){
                        	list.add(readfile.getName());
                        	//System.out.println("name=" + readfile.getName());
                        }
                    } else if (readfile.isDirectory()) {
                    	getFileList(filepath + "\\" + filelist[i],regex);
                    }
                }

            }

        } catch (FileNotFoundException e) {
                System.out.println("readfile()   Exception:" + e.getMessage());
        }
        return list;
    }
    
    //生成文件
    public void writeToJson(String filePath,String string) throws IOException
	{
		File file = new File(filePath);
		char [] stack = new char[1024];
		int top=-1;
		
		
		StringBuffer sb = new StringBuffer();
		char [] charArray = string.toCharArray();
		for(int i=0;i<charArray.length;i++){
			char c= charArray[i];
			if ('{' == c || '[' == c) {  
                stack[++top] = c; 
                sb.append("\n"+charArray[i] + "\n");  
                for (int j = 0; j <= top; j++) {  
                    sb.append("\t");  
                }  
                continue;  
            }
			 if ((i + 1) <= (charArray.length - 1)) {  
	                char d = charArray[i+1];  
	                if ('}' == d || ']' == d) {  
	                    top--; 
	                    sb.append(charArray[i] + "\n");  
	                    for (int j = 0; j <= top; j++) {  
	                        sb.append("\t");  
	                    }  
	                    continue;  
	                }  
	            }  
	            if (',' == c) {  
	                sb.append(charArray[i] + "");  
	                for (int j = 0; j <= top; j++) {  
	                    sb.append("");  
	                }  
	                continue;  
	            }  
	            sb.append(c);  
	        }  
	          
	        Writer write = new FileWriter(file);  
	        write.write(sb.toString());  
	        write.flush();  
	        write.close();  
	}
  
}
