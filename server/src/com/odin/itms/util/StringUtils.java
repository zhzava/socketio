package com.odin.itms.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils {
	
	public static Pattern LG_PATTERN = Pattern.compile("\\<([^\\>]+)\\>");
	  private static final char[] QUOTE_ENCODE = "&quot;".toCharArray();
	  private static final char[] AMP_ENCODE = "&amp;".toCharArray();
	  private static final char[] LT_ENCODE = "&lt;".toCharArray();
	  private static final char[] GT_ENCODE = "&gt;".toCharArray();

	  private static MessageDigest digest = null;
	  private static final int fillchar = 61;
	  private static final String cvt = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	  private static Random randGen = new Random();

	  private static char[] numbersAndLetters = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

	  private static char[] numbers = "0123456789".toCharArray();

	  private static final char[] zeroArray = "0000000000000000".toCharArray();

	  public static String nullValue(String value)
	  {
	    return ObjectUtils.nullValue(value);
	  }

	  public static String nullValue(String value, String defaultValue)
	  {
	    return ObjectUtils.nullValue(value, defaultValue);
	  }

	  public static String emptyValue(String value, String defaultValue)
	  {
	    if ((value == null) || (value.length() == 0) || (value.matches("\\s*")))
	      return defaultValue;
	    return value;
	  }

	  public static final String replaceIgnoreCase(String line, String oldString, String newString)
	  {
	    if (line == null)
	      return null;
	    String lcLine = line.toLowerCase();
	    String lcOldString = oldString.toLowerCase();
	    int i = 0;
	    if ((i = lcLine.indexOf(lcOldString, i)) >= 0) {
	      char[] line2 = line.toCharArray();
	      char[] newString2 = newString.toCharArray();
	      int oLength = oldString.length();
	      StringBuffer buf = new StringBuffer(line2.length);
	      buf.append(line2, 0, i).append(newString2);
	      i += oLength;
	      int j = i;
	      while ((i = lcLine.indexOf(lcOldString, i)) > 0) {
	        buf.append(line2, j, i - j).append(newString2);
	        i += oLength;
	        j = i;
	      }
	      buf.append(line2, j, line2.length - j);
	      return buf.toString();
	    }
	    return line;
	  }

	  public static final String replaceIgnoreCase(String line, String oldString, String newString, int[] count)
	  {
	    if (line == null)
	      return null;
	    String lcLine = line.toLowerCase();
	    String lcOldString = oldString.toLowerCase();
	    int i = 0;
	    if ((i = lcLine.indexOf(lcOldString, i)) >= 0) {
	      int counter = 1;
	      char[] line2 = line.toCharArray();
	      char[] newString2 = newString.toCharArray();
	      int oLength = oldString.length();
	      StringBuffer buf = new StringBuffer(line2.length);
	      buf.append(line2, 0, i).append(newString2);
	      i += oLength;
	      int j = i;
	      while ((i = lcLine.indexOf(lcOldString, i)) > 0) {
	        counter++;
	        buf.append(line2, j, i - j).append(newString2);
	        i += oLength;
	        j = i;
	      }
	      buf.append(line2, j, line2.length - j);
	      count[0] = counter;
	      return buf.toString();
	    }
	    return line;
	  }

	  public static final String replace(String line, String oldString, String newString, int[] count)
	  {
	    if (line == null)
	      return null;
	    int i = 0;
	    if ((i = line.indexOf(oldString, i)) >= 0) {
	      int counter = 1;
	      char[] line2 = line.toCharArray();
	      char[] newString2 = newString.toCharArray();
	      int oLength = oldString.length();
	      StringBuffer buf = new StringBuffer(line2.length);
	      buf.append(line2, 0, i).append(newString2);
	      i += oLength;
	      int j = i;
	      while ((i = line.indexOf(oldString, i)) > 0) {
	        counter++;
	        buf.append(line2, j, i - j).append(newString2);
	        i += oLength;
	        j = i;
	      }
	      buf.append(line2, j, line2.length - j);
	      count[0] = counter;
	      return buf.toString();
	    }
	    return line;
	  }

	  public static final String escapeHTMLTags(String in)
	  {
	    if (in == null) {
	      return null;
	    }
	    int i = 0;
	    int last = 0;
	    char[] input = in.toCharArray();
	    int len = input.length;
	    StringBuffer out = new StringBuffer((int)(len * 1.3D));
	    for (; i < len; i++) {
	      char ch = input[i];
	      if (ch > '>')
	        continue;
	      if (ch == '<') {
	        if (i > last)
	          out.append(input, last, i - last);
	        last = i + 1;
	        out.append(LT_ENCODE);
	      } else if (ch == '>') {
	        if (i > last)
	          out.append(input, last, i - last);
	        last = i + 1;
	        out.append(GT_ENCODE);
	      }
	    }
	    if (last == 0)
	      return in;
	    if (i > last)
	      out.append(input, last, i - last);
	    return out.toString();
	  }

	  public static final synchronized String hash(String data)
	  {
	    if (digest == null) {
	      try {
	        digest = MessageDigest.getInstance("MD5");
	      } catch (NoSuchAlgorithmException nsae) {
	        System.err.println("Failed to load the MD5 MessageDigest. Jive will be unable to function normally.");

	        nsae.printStackTrace();
	      }
	    }
	    digest.update(data.getBytes());
	    return encodeHex(digest.digest());
	  }

	  public static final String encodeHex(byte[] bytes)
	  {
	    StringBuffer buf = new StringBuffer(bytes.length * 2);

	    for (int i = 0; i < bytes.length; i++) {
	      if ((bytes[i] & 0xFF) < 16)
	        buf.append("0");
	      buf.append(Long.toString(bytes[i] & 0xFF, 16));
	    }
	    return buf.toString();
	  }

	  public static final byte[] decodeHex(String hex)
	  {
	    char[] chars = hex.toCharArray();
	    byte[] bytes = new byte[chars.length / 2];
	    int byteCount = 0;
	    for (int i = 0; i < chars.length; i += 2) {
	      byte newByte = 0;
	      newByte = (byte)(newByte | hexCharToByte(chars[i]));
	      newByte = (byte)(newByte << 4);
	      newByte = (byte)(newByte | hexCharToByte(chars[(i + 1)]));
	      bytes[byteCount] = newByte;
	      byteCount++;
	    }
	    return bytes;
	  }

	  private static final byte hexCharToByte(char ch)
	  {
	    switch (ch) {
	    case '0':
	      return 0;
	    case '1':
	      return 1;
	    case '2':
	      return 2;
	    case '3':
	      return 3;
	    case '4':
	      return 4;
	    case '5':
	      return 5;
	    case '6':
	      return 6;
	    case '7':
	      return 7;
	    case '8':
	      return 8;
	    case '9':
	      return 9;
	    case 'a':
	      return 10;
	    case 'b':
	      return 11;
	    case 'c':
	      return 12;
	    case 'd':
	      return 13;
	    case 'e':
	      return 14;
	    case 'f':
	      return 15;
	    case ':':
	    case ';':
	    case '<':
	    case '=':
	    case '>':
	    case '?':
	    case '@':
	    case 'A':
	    case 'B':
	    case 'C':
	    case 'D':
	    case 'E':
	    case 'F':
	    case 'G':
	    case 'H':
	    case 'I':
	    case 'J':
	    case 'K':
	    case 'L':
	    case 'M':
	    case 'N':
	    case 'O':
	    case 'P':
	    case 'Q':
	    case 'R':
	    case 'S':
	    case 'T':
	    case 'U':
	    case 'V':
	    case 'W':
	    case 'X':
	    case 'Y':
	    case 'Z':
	    case '[':
	    case '\\':
	    case ']':
	    case '^':
	    case '_':
	    case '`': } return 0;
	  }

	  public static String encodeBase64(String data)
	  {
	    return encodeBase64(data.getBytes());
	  }

	  public static String encodeBase64(byte[] data)
	  {
	    int len = data.length;
	    StringBuffer ret = new StringBuffer((len / 3 + 1) * 4);
	    for (int i = 0; i < len; i++) {
	      int c = data[i] >> 2 & 0x3F;
	      ret.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(c));
	      c = data[i] << 4 & 0x3F;
	      i++; if (i < len) {
	        c |= data[i] >> 4 & 0xF;
	      }
	      ret.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(c));
	      if (i < len) {
	        c = data[i] << 2 & 0x3F;
	        i++; if (i < len) {
	          c |= data[i] >> 6 & 0x3;
	        }
	        ret.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(c));
	      } else {
	        i++;
	        ret.append('=');
	      }

	      if (i < len) {
	        c = data[i] & 0x3F;
	        ret.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(c));
	      } else {
	        ret.append('=');
	      }
	    }
	    return ret.toString();
	  }

	  public static String decodeBase64(String data)
	  {
	    return decodeBase64(data.getBytes());
	  }

	  public static String decodeBase64(byte[] data)
	  {
	    int len = data.length;
	    StringBuffer ret = new StringBuffer(len * 3 / 4);
	    for (int i = 0; i < len; i++) {
	      int c = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(data[i]);
	      i++;
	      int c1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(data[i]);
	      c = c << 2 | c1 >> 4 & 0x3;
	      ret.append((char)c);
	      i++; if (i < len) {
	        c = data[i];
	        if (61 == c) {
	          break;
	        }
	        c = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf((char)c);
	        c1 = c1 << 4 & 0xF0 | c >> 2 & 0xF;
	        ret.append((char)c1);
	      }

	      i++; if (i < len) {
	        c1 = data[i];
	        if (61 == c1) {
	          break;
	        }
	        c1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf((char)c1);
	        c = c << 6 & 0xC0 | c1;
	        ret.append((char)c);
	      }
	    }
	    return ret.toString();
	  }

	  public static final String[] toLowerCaseWordArray(String text)
	  {
	    if ((text == null) || (text.length() == 0)) {
	      return new String[0];
	    }
	    ArrayList wordList = new ArrayList();
	    BreakIterator boundary = BreakIterator.getWordInstance();
	    boundary.setText(text);
	    int start = 0;

	    for (int end = boundary.next(); end != -1; )
	    {
	      String tmp = text.substring(start, end).trim();

	      tmp = replace(tmp, "+", "");
	      tmp = replace(tmp, "/", "");
	      tmp = replace(tmp, "\\", "");
	      tmp = replace(tmp, "#", "");
	      tmp = replace(tmp, "*", "");
	      tmp = replace(tmp, ")", "");
	      tmp = replace(tmp, "(", "");
	      tmp = replace(tmp, "&", "");
	      if (tmp.length() > 0)
	        wordList.add(tmp);
	      start = end; end = boundary.next();
	    }

	    return (String[])wordList.toArray(new String[wordList.size()]);
	  }

	  public static final String randomString(int length)
	  {
	    if (length < 1) {
	      return null;
	    }
	    char[] randBuffer = new char[length];
	    for (int i = 0; i < randBuffer.length; i++)
	      randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
	    return new String(randBuffer);
	  }

	  public static final String randomNumber(int length)
	  {
	    if (length < 1) {
	      return null;
	    }
	    char[] randBuffer = new char[length];
	    for (int i = 0; i < randBuffer.length; i++)
	      randBuffer[i] = numbers[randGen.nextInt(10)];
	    return new String(randBuffer);
	  }

	  public static final String chopAtWord(String string, int length)
	  {
	    if ((string == null) || (string.length() == 0)) {
	      return string;
	    }
	    char[] charArray = string.toCharArray();
	    int sLength = string.length();
	    if (length < sLength) {
	      sLength = length;
	    }

	    for (int i = 0; i < sLength - 1; i++)
	    {
	      if ((charArray[i] == '\r') && (charArray[(i + 1)] == '\n'))
	        return string.substring(0, i + 1);
	      if (charArray[i] == '\n')
	        return string.substring(0, i);
	    }
	    if (charArray[(sLength - 1)] == '\n') {
	      return string.substring(0, sLength - 1);
	    }

	    if (string.length() < length) {
	      return string;
	    }

	    for (int i = length - 1; i > 0; i--) {
	      if (charArray[i] == ' ') {
	        return string.substring(0, i).trim();
	      }
	    }

	    return string.substring(0, length);
	  }

	  public static final String escapeForXML(String string)
	  {
	    if (string == null) {
	      return null;
	    }
	    int i = 0;
	    int last = 0;
	    char[] input = string.toCharArray();
	    int len = input.length;
	    StringBuffer out = new StringBuffer((int)(len * 1.3D));
	    for (; i < len; i++) {
	      char ch = input[i];
	      if (ch > '>')
	        continue;
	      if (ch == '<') {
	        if (i > last)
	          out.append(input, last, i - last);
	        last = i + 1;
	        out.append(LT_ENCODE);
	      } else if (ch == '&') {
	        if (i > last)
	          out.append(input, last, i - last);
	        last = i + 1;
	        out.append(AMP_ENCODE);
	      } else if (ch == '"') {
	        if (i > last)
	          out.append(input, last, i - last);
	        last = i + 1;
	        out.append(QUOTE_ENCODE);
	      }
	    }
	    if (last == 0)
	      return string;
	    if (i > last)
	      out.append(input, last, i - last);
	    return out.toString();
	  }

	  public static final String unescapeFromXML(String string)
	  {
	    string = replace(string, "&lt;", "<");
	    string = replace(string, "&gt;", ">");
	    string = replace(string, "&quot;", "\"");
	    return replace(string, "&amp;", "&");
	  }

	  public static final String zeroPadString(String string, int length)
	  {
	    if ((string == null) || (string.length() > length))
	      return string;
	    StringBuffer buf = new StringBuffer(length);
	    buf.append(zeroArray, 0, length - string.length()).append(string);
	    return buf.toString();
	  }

	  public static final String dateToMillis(Date date)
	  {
	    return zeroPadString(Long.toString(date.getTime()), 15);
	  }

	  public static String getExtendName(String filename)
	  {
	    int dotIndex = filename.lastIndexOf(46);
	    if (dotIndex == -1)
	      return "";
	    return filename.substring(dotIndex + 1);
	  }

	  public static String[] toStringArray(Object[] objects) {
	    String[] result = new String[objects.length];
	    for (int i = 0; i < objects.length; i++)
	      result[i] = objects[i].toString();
	    return result;
	  }

	  public static String set(String src, String name, String value) {
	    String tempSrc = set0(src, name, value, "%{", "}");
	    return set0(tempSrc, name, value, "%", "");
	  }

	  public static String set(String src, Properties properties) {
	    String result = src;
	    for (Object element : properties.entrySet()) {
	      Map.Entry entry = (Map.Entry)element;
	      result = set(result, (String)entry.getKey(), entry.getValue().toString());
	    }
	    return result;
	  }

	  public static boolean hasLength(String str)
	  {
	    return (str != null) && (str.length() > 0);
	  }

	  public static boolean hasText(String str)
	  {
	    int strLen;
	    if ((str == null) || ((strLen = str.length()) == 0))
	      return false;
	    for (int i = 0; i < strLen; i++)
	      if (!Character.isWhitespace(str.charAt(i)))
	        return true;
	    return false;
	  }

	  private static String set0(String str, String name, String value, String prefix, String suffix) {
	    String tag = prefix + name + suffix;
	    return replace(str, tag, value);
	  }

	  public static String replace(String input, String oldString, String newString) {
	    int indexOf = -1;
	    StringBuffer result = new StringBuffer();
	    String after = input;
	    while ((indexOf = after.indexOf(oldString)) != -1) {
	      String before = after.substring(0, indexOf);
	      after = after.substring(indexOf + oldString.length());
	      result.append(before).append(newString);
	    }
	    return result.append(after).toString();
	  }
	
	/**
	 * 将map中key的特有字符更换成新的特有字符
	 * @param sign
	 * @param newSign
	 * @param map
	 * @return
	 */
	public static Map<String, String> replaceSign(String sign, String newSign, Map<String, String> map){
		if(null == map){
			return null;
		}
		Map<String, String> returnMap = new HashMap<String, String>();
		Set<String> keys = map.keySet();
		for(String key : keys){
			key = key.replace(sign, newSign);
			returnMap.put(key, map.get(key));
		}
		return returnMap;
	}
	
	/**
	 * 将字符串中含有[]的行转换成map
	 * key为中括号号内的字符,value为冒号之前的字符
	 * @param analyze 有规则的字符串
	 * @return
	 */
	public static Map<String, String> analyzeMaps(String analyze) {
		String[] analyzes = analyze.split("\n");
		Map<String, String> map = new HashMap<String, String>();

		Pattern p = Pattern.compile("\\[*\\]");
		for (String ana : analyzes) {
			Matcher m = p.matcher(ana);
			if (m.find()) {
				int slight = ana.indexOf(".");
				int colon = ana.indexOf("：");
				int bracke = ana.indexOf("[");

				if (slight != -1 && colon != -1 && bracke != -1) {
					String key = ana.substring(bracke + 1, slight).trim();
					String value = ana.substring(0, colon).trim();
					map.put(key, value);
				}
			}
		}
		return map;
	}
	/**
	 * 将字符串中含有[]的行转换成集合,值为 中括号号内的字符
	 * @param analyze 有规则的字符串
	 * @return
	 */
	public static List<String> analyzeKeys(String analyze) {
		return new ArrayList<String>(analyzeMaps(analyze).keySet());
	}

	
	/**
	 * 去除换行
	 * @param input
	 * @return
	 */
	public static String removeBlank(String input){
		Pattern CRLF = Pattern.compile("(\r\n|\r|\n|\n\r)");
		Matcher m = CRLF.matcher(input); 
		String returnStr = null;
		if(m.find()){
			returnStr = m.replaceAll("");  
		}else{
			returnStr = input;
		}
		return returnStr;
	}
	
	/**
	 * 将全角字符转换成半角
	 * @param input 带全角的字符
	 * @return
	 */
	public static String ToDBC(String input) {
	     char c[] = input.toCharArray();
	          for (int i = 0; i < c.length; i++) {
	            if (c[i] == '\u3000') {
	              c[i] = ' ';
	            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
	              c[i] = (char) (c[i] - 65248);
	            }
	          }
	          String returnString = new String(c);
	          return returnString;
	 }
	/**
	 * 截取字符串
	 */
	public static String substring(String str, int length) {
		if (str != null && !str.equals("")) {
			return str.length() > length ? str.substring(0, length) + "..." : str;
		} else {
			return "";
		}

	}

	/**
	 * 字符串是否为空
	 */
	public static boolean isEmpty(String str) {
		return org.apache.commons.lang.StringUtils.isEmpty(str);
	}

	/**
	 * <p>
	 * Description: 检查手机号码是否合法
	 * </p>
	 * 
	 * @exist_problem
	 * @amendment_history
	 * @param mobile
	 * @return
	 */
	public static boolean checkMobile(String mobile) {
		String mobileScope = "134,135,136,137,138,139,150,151,152,157,158,159,187,188,130,131,132,153,155,156,133,185,186,180,189";
		String mobileLen = "11,13";
		String mobilePrefix = "86";
		return checkMobileStr(mobile, mobileScope, mobileLen, mobilePrefix);
	}

	/**
	 * <p>
	 * Description: 检查一批手机号码是否合法
	 * </p>
	 * 
	 * @exist_problem
	 * @amendment_history
	 * @param mobiles
	 * @return
	 */
	public static List<String> checkMobile(List<String> mobiles) {

		List<String> error = new LinkedList<String>();
		String checkMobile = "true";
		String mobileScope = "134,135,136,137,138,139,150,151,152,157,158,159,187,188,130,131,132,153,155,156,133,185,186,180,189";
		String mobileLen = "11,13";
		String mobilePrefix = "86";
		// ADD BY ANDERS 040413 增加判断是否检测手机的配置
		if (!checkMobile.equals("true"))
			return error;

		if (mobiles == null)
			return error;
		for (int i = 0; i < mobiles.size(); i++) {
			String str_mobile = mobiles.get(i);
			if (!checkMobileStr(str_mobile, mobileScope, mobileLen, mobilePrefix))
				error.add(str_mobile);
		}
		return error;

	}

	public static boolean checkMobileStr(String mobile, String startStr, String lengthStr, String affixStr) {
		List<String> arr_start = parseStr(startStr);
		List<String> arr_length = parseStr(lengthStr);
		List<String> arr_affix = parseStr(affixStr);
		if (mobile == null || mobile.trim().length() == 0)
			return false;
		if (arr_start.contains("0") && mobile.startsWith("0")) {// 对小灵通的判断
			Pattern p = Pattern.compile("^(?:0(?:10|2[0-57-9]|[3-9]\\d{2})\\d{7,8})$");
			Matcher m = p.matcher(mobile);
			return m.matches();
		}

		boolean pass = false;
		if (lengthStr.trim().length() > 0) {
			for (int i = 0; i < arr_length.size(); i++) {
				int length = Integer.parseInt(arr_length.get(i));
				if (mobile.length() == length) {
					pass = true;
					break;
				}
			}
		} else {
			pass = true;
		}
		if (!pass)
			return false;

		for (int i = 0; i < arr_affix.size(); i++) {
			String str_affix = arr_affix.get(i);
			if (mobile.startsWith(str_affix))
				mobile = mobile.substring(str_affix.length());
		}
		pass = false;
		if (startStr.trim().length() > 0) {
			for (int i = 0; i < arr_start.size(); i++) {
				String start = arr_start.get(i);
				if (mobile.startsWith(start)) {
					mobile = mobile.substring(start.length());
					pass = true;
					break;
				}
			}
		} else {
			pass = true;
		}
		if (!pass)
			return false;

		for (int i = 0; i < mobile.length(); i++) {
			String su = mobile.substring(i, i + 1);
			if (!((su.compareTo("0") >= 0) && (su.compareTo("9") <= 0)))
				return false;
		}
		return true;

	}

	/** 解析字符串 */
	private static List<String> parseStr(String str) {
		List<String> result = new ArrayList<String>();
		StringTokenizer chk1 = new StringTokenizer(str, ",");
		while (chk1.hasMoreTokens()) {
			String get = chk1.nextToken();
			if (get.indexOf("-") != -1) {
				StringTokenizer chk2 = new StringTokenizer(get, "-");
				int start = Integer.parseInt(chk2.nextToken());
				int end = Integer.parseInt(chk2.nextToken());
				for (int i = start; i >= start && i <= end; i++) {
					result.add(String.valueOf(i));
				}
			} else {
				result.add(get);
			}

			result.add(get);
		}
		return result;
	}

	/**
	 * 判断字符串是否是数字组成
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		if (str == null || str.trim().equals(""))
			return false;

		java.util.regex.Pattern p = null;
		java.util.regex.Matcher m = null;
		try {
			p = java.util.regex.Pattern.compile("[^0-9]");
			m = p.matcher(str);
			if (m.find())
				return false;
		} catch (Exception e) {
		}
		return true;
	}

	/**
	 * 获取随机字符串
	 */
	public static String getRandomString(int length) {
		StringBuffer sb = new StringBuffer();

		StringBuffer buffer = new StringBuffer("0123456789");
		int range = buffer.length();
		Random r = new Random();

		for (int i = 0; i < length; i++)
			sb.append(buffer.charAt(r.nextInt(range)));

		return sb.toString();
	}

	/**
	 * 判断邮件地址是否合法
	 */
	public static boolean isEmailAdressFormat(String email) {

		if (isEmpty(email))
			return false;

		boolean isExist = false;

		Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}");
		Matcher m = p.matcher(email);
		boolean b = m.matches();
		if (b) {
			isExist = true;
		}
		return isExist;
	}

	/**
	 * 判断网址是否合法
	 */
	public static boolean isUrl(String url) {

		if (isEmpty(url))
			return false;

		String regEx = "^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$";
		Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
		Matcher matcher = p.matcher(url);
		return matcher.matches();
	}

	/**
	 * 判断电话号码是否合法
	 */
	public static boolean isPhoneNumber(String phoneNum) {

		if (isEmpty(phoneNum))
			return false;

		String regex = "(((010|02\\d{1}|0[3-9]\\d{2})\\d{7,8}|1(3\\d{1}|58|59)\\d{8}))";

		return (Pattern.matches(regex, phoneNum));
	}

	/**
	 * 拆分字符串 andrew add for GroupSend mobile number process
	 * 
	 * @param hid
	 * @param sp
	 * @return
	 */
	public static List<String> splitForMobile(String mobiles, String splitBy) {
		String[] mobile = mobiles.split(splitBy);
		List<String> mobileList = new ArrayList<String>();
		for (int i = 0; i < mobile.length; i++) {
			mobile[i] = mobile[i].trim();// .replaceAll("\r",
											// "").replaceAll("\t", "");
			if (mobile[i] != null && !mobile[i].equals("")) {
				mobileList.add(mobile[i]);
			}
		}
		// mobile = new String[mobileList.size()];
		// return (String[])mobileList.toArray(mobile);
		return mobileList;
	}

	// 去除 字符串中空格回车
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 按字节截取中文
	 */
	public static String parse(String t, int k) {

		// 要显示长度小于字节,大于则全部显示
		if (k <= t.getBytes().length)
			// 从0,到第k个字符
			for (int i = 0; i < k; i++) {
				// 第i个字符,如果字节长度==2其为汉字,k--
				String temp = t.substring(i, i + 1);
				if (temp.getBytes().length == 2)
					k--;
			}
		else
			k = t.length();
		return t.substring(0, k);
	}

	/**
	 * 用*替换一部分字符串
	 * 
	 * @param type
	 *            类型，userName,address
	 * @param value
	 *            需要替换的字符串
	 * @return
	 */
	public static String hideString(String type, String value) {
		int len = value.length();
		int hideLen = 0;
		StringBuilder result = new StringBuilder();
		if ("userName".equals(type)) {
			if (len == 2 || len == 3) {
				result.append(value.substring(0, 1));
				hideLen = len - 1;
				for (int i = 0; i < hideLen; i++) {
					result.append("*");
				}
			} else if (len > 3 || len < 6) {
				result.append(value.substring(0, 2));
				hideLen = len - 2;
				for (int i = 0; i < hideLen; i++) {
					result.append("*");
				}
			} else {
				result.append(value.substring(0, len / 2));
				hideLen = len / 2;
				for (int i = 0; i < hideLen; i++) {
					result.append("*");
				}
			}
		} else if ("address".equals(type)) {
			result.append(value.substring(0, len * 1 / 4));
			hideLen = len * 1 / 4;
			for (int i = 0; i < hideLen; i++) {
				result.append("*");
			}
		}
		return result.toString();
	}

	/**
	 * @param String
	 *            sourceStr 需要替换的源字符串,String type 源字符串类型
	 * @return String 根据类型对输入的源字符串进行替换、隐藏处理
	 * @author liqijian
	 */
	public static String replaceString(String sourceStr, String type) {
		if(!StringUtils.isEmpty(sourceStr)){
			StringBuffer result = new StringBuffer();
			int length = sourceStr.length();
			int hideLen = 0;
			if ("name".equals(type)) {
				String tempStr = sourceStr.substring(0, 1);
				result.append(tempStr);
				for (int i = 0; i < length - 1; i++) {
					result.append("*");
				}
			} else if ("halfName".equals(type)) {
				int end=(int) (sourceStr.length()*0.5);
				String tempStr=sourceStr.substring(0, end);
				result.append(tempStr);
				for (int i = end; i < sourceStr.length(); i++) {
					result.append("*");
				}
			} else if ("address".equals(type)) {
				result.append(sourceStr.substring(0, (int) (length * (0.7))));
				hideLen = (int) (length * (0.3));
				for (int i = 0; i < hideLen; i++) {
					result.append("*");
				}
			} else if ("number".equals(type)) {
				if (length>8) {
					String beginStr = sourceStr.substring(0, 4);
					String endStr = sourceStr.substring(length - 4, length);
					result.append(beginStr);
					for (int i = 0; i < length - 8; i++) {
						result.append("*");
					}
					result.append(endStr);
				}else {
					if (length==0) {
						result.append(sourceStr);
					}else {
						result.append(sourceStr.substring(0, (int) (length * (0.7))));
						hideLen = (int) (length * (0.3));
						for (int i = 0; i < hideLen; i++) {
							result.append("*");
						}
					}
				}
			}else if ("userNumber".equals(type)) {
				if (length>8) {
					String beginStr = sourceStr.substring(0, 3);
					String endStr = sourceStr.substring(length - 3, length);
					result.append(beginStr);
					for (int i = 0; i < length - 6; i++) {
						result.append("*");
					}
					result.append(endStr);
				}else {
					if (length==0) {
						result.append(sourceStr);
					}else {
						result.append(sourceStr.substring(0, (int) (length * (0.7))));
						hideLen = (int) (length * (0.3));
						for (int i = 0; i < hideLen; i++) {
							result.append("*");
						}
					}
				}
			}else if ("phoneNumber".equals(type)) {
				if (length==11) {
					String beginStr = sourceStr.substring(0, 3);
					String endStr = sourceStr.substring(length - 4, length);
					result.append(beginStr);
					for (int i = 0; i < length - 7; i++) {
						result.append("*");
					}
					result.append(endStr);
				}else {
					result.append(sourceStr.substring(0, (int) (length * (0.3))));
					hideLen = (int) (length * (0.7));
					for (int i = 0; i < hideLen; i++) {
						result.append("*");
					}
				}
			}
			else if("postalCode".equals(type)){
				if(length>0){
					result.append(sourceStr.substring(0, (int) (length * (0.5))));
					hideLen = (int) (length * (0.5));
					for (int i = 0; i < hideLen; i++) {
						result.append("*");
					}
				}else {
					result.append("");
				}
			}
			else if("email".equals(type)){
				int index=sourceStr.indexOf("@");
				if(length>0){
					if(index>0){
						if(index<=1){
							result.append("*");
							result.append(sourceStr.substring(index));
						}else{
							String beginStr = sourceStr.substring(0, 1);
							String endStr = sourceStr.substring(index);
							result.append(beginStr);
							for (int i = 0; i < index-1; i++) {
								result.append("*");
							}
							result.append(endStr);
						}
						
						
					}else{
						result.append(sourceStr);
					}
				}else{
					result.append("");
				}
				
			}
			return result.toString();
		}else{
			return "";
		}
	
	}

    /**
     * 转义lucene查询语法词汇
     * @param s
     * @return
     */
	public static String escapeLuceneSyntax(String s) {
		StringBuilder sb = new StringBuilder();
		char pc='0';
		for (int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			if ((c == '\\') || (c == '+') || (c == '-' && pc != ' ') || (c == '!')
					|| (c == '(') || (c == ')') || (c == ':') || (c == '^')
					|| (c == '[') || (c == ']') || (c == '{')
					|| (c == '}') || (c == '~') || (c == '*') || (c == '?')
					|| (c == '|') || (c == '&') || (c == '/')) {
				sb.append('\\');
			}
			sb.append(c);
			pc = c;
		}
		return sb.toString();
	}
	
	/**
     * 转义正则表达式语法词汇
     * @param s
     * @return
     */
	public static String escapeRegSyntax(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			if ((c == '\\') || (c == '+') || (c == '-') || (c == '?')
					|| (c == '(') || (c == ')') || (c == '^')
					|| (c == '[') || (c == ']') || (c == '{')
					|| (c == '}') || (c == '.') || (c == '*') 
					|| (c == '|') || (c == '$') || (c == '/')) {
				sb.append('\\');
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	public static String firstCharacterToUpper(String srcStr) {
		return srcStr.substring(0, 1).toUpperCase() + srcStr.substring(1);
	}
	/**
	 * 替换字符串并让它的下一个字母为大写
	 * 
	 * @param srcStr
	 * @param org
	 * @param ob
	 * @return
	 */
	public static String replaceAndUpper(String srcStr, String org, String ob) {
		String newString = "";
		int first = 0;
		while (srcStr.indexOf(org) != -1) {
			first = srcStr.indexOf(org);
			if (first != srcStr.length()) {
				newString = newString + srcStr.substring(0, first) + ob;
				srcStr = srcStr.substring(first + org.length(), srcStr.length());
				srcStr = firstCharacterToUpper(srcStr);
			}
		}
		newString = newString + srcStr;
		return newString;
	}
	
	/**
	 * 将驼峰表示法转为多单词下划线分割字符串
	 * @param src
	 * @param type (0:全部转小写；1:全部转大写；其它:不变)
	 * @return
	 */
	public static String deHumpString(String src, int type) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < src.length(); ++i) {
			char c = src.charAt(i);
			if ((c>='A' && c<='Z')) {
				sb.append('_');
			}
			sb.append(c);
		}
		src = sb.toString();
		switch (type) {
		case 0:
			src = src.toLowerCase();
			break;
		case 1:
			src = src.toUpperCase();
			break;
		default:
			break;
		}
		return src;
	}
	
	/**
	 * 将多单词下划线分割字符串转为驼峰表示法
	 * @param src
	 * @return
	 */
	public static String toHumpString(String src) {
		src = src.trim().toLowerCase();
		StringBuilder sb = new StringBuilder();
		boolean toUp = false;
		for (int i = 0; i < src.length(); ++i) {
			char c = src.charAt(i);
			if ((c == '_')) {
				toUp = true;
			} else {
				if(toUp && c>='a' && c<='z') {
					c = (char)(c-32);
				}
				toUp = false;
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 将<>内的多单词下划线分割字符串转为驼峰表示法
	 * @param src
	 * @return
	 */
	public static String toHumpLG(String src) {
		Matcher mt = LG_PATTERN.matcher(src);
		StringBuffer sb = new StringBuffer();
		while(mt.find()) {
			mt.appendReplacement(sb, "<"+toHumpString(mt.group(1))+">");
		}
		mt.appendTail(sb);
		return sb.toString();
	}
	
	/**
	 * 让map的key的首字母小写
	 * @param map
	 * @return
	 */
	public static Map setKeyFirstLetterLowcase(Map map){
		Map newMap = new HashMap();
		Set set = map.keySet();
		Iterator iter = set.iterator();
		while(iter.hasNext()){
			String key = (String)iter.next();
			newMap.put(key.substring(0,1).toLowerCase()+key.substring(1), map.get(key));
		}
		return newMap;
	}
	
	/**
	 * Unicode编码的中文字符转成中文
	 * @param dataStr
	 * @return
	 */
	public static String unicodeConvert(String dataStr) {
		//System.out.println("--------data str---->" + dataStr); 
		if(dataStr == null || dataStr.length() == 0) {
			return dataStr;
		}
		int start = 0;
		int end = 0;
		final StringBuffer buffer = new StringBuffer();
		while (start > -1) {
			int system = 10;// 进制
			if (start == 0) {
				int t = dataStr.indexOf("&#");
				if (start != t)
					start = t;
				if(start > 0) {
					buffer.append(dataStr.substring(0, start));
				}
				if(start == -1) {
					return dataStr;
				}
			}
			end = dataStr.indexOf(";", start + 2);
			String charStr = "";
			if (end != -1) {
				charStr = dataStr.substring(start + 2, end);
				// 判断进制
				char s = charStr.charAt(0);
				if (s == 'x' || s == 'X') {
					system = 16;
					charStr = charStr.substring(1);
				}
				// 转换
				try {
					char letter = (char) Integer.parseInt(charStr, system);
					buffer.append(new Character(letter).toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			
			// 处理当前unicode字符到下一个unicode字符之间的非unicode字符
			start = dataStr.indexOf("&#", end);
			if (start - end > 1) {
				buffer.append(dataStr.substring(end + 1, start));
			}
			// 处理最后面的非 unicode字符
			if (start == -1) {
				int length = dataStr.length();
				if (end + 1 != length) {
					buffer.append(dataStr.substring(end + 1, length));
				}
			}
		}
		return buffer.toString();
	}	
	
	public static String inputStream2String(InputStream is) throws IOException{ 
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        int i=-1; 
	    while((i=is.read())!=-1){ 
	        baos.write(i); 
	    } 
	    return baos.toString(); 
	}

	/**
	 * 正则表达式判断输入的内容是否为qq表情
	 * @author liqijian
	 * @param content
	 * @return
	 */
	public static boolean isQqFace(String content) {
		boolean result = false;
		// 判断QQ表情的正则表达式
		String qqfaceRegex = "/::\\)|/::~|/::B|/::\\||/:8-\\)|/::<|/::$|/::X|/::Z|/::'\\(|/::-\\||/::@|/::P|/::D|/::O|/::\\(|/::\\+|/:--b|/::Q|/::T|/:,@P|/:,@-D|/::d|/:,@o|/::g|/:\\|-\\)|/::!|/::L|/::>|/::,@|/:,@f|/::-S|/:\\?|/:,@x|/:,@@|/::8|/:,@!|/:!!!|/:xx|/:bye|/:wipe|/:dig|/:handclap|/:&-\\(|/:B-\\)|/:<@|/:@>|/::-O|/:>-\\||/:P-\\(|/::'\\||/:X-\\)|/::\\*|/:@x|/:8\\*|/:pd|/:<W>|/:beer|/:basketb|/:oo|/:coffee|/:eat|/:pig|/:rose|/:fade|/:showlove|/:heart|/:break|/:cake|/:li|/:bome|/:kn|/:footb|/:ladybug|/:shit|/:moon|/:sun|/:gift|/:hug|/:strong|/:weak|/:share|/:v|/:@\\)|/:jj|/:@@|/:bad|/:lvu|/:no|/:ok|/:love|/:<L>|/:jump|/:shake|/:<O>|/:circle|/:kotow|/:turn|/:skip|/:oY|/:#-0|/:hiphot|/:kiss|/:<&|/:&>";
		Pattern p = Pattern.compile(qqfaceRegex);
		Matcher m = p.matcher(content);
		if (m.matches()) {
			result = true;
		}
		return result;
	}

	/**
	 * 正则表达式判断输入的内容是否包含qq表情
	 * @return
	 */
	public static boolean containQqFace(String content) {
		boolean result = false;
		// 判断QQ表情的正则表达式
		String qqfaceRegex = "/::\\)|/::~|/::B|/::\\||/:8-\\)|/::<|/::$|/::X|/::Z|/::'\\(|/::-\\||/::@|/::P|/::D|/::O|/::\\(|/::\\+|/:--b|/::Q|/::T|/:,@P|/:,@-D|/::d|/:,@o|/::g|/:\\|-\\)|/::!|/::L|/::>|/::,@|/:,@f|/::-S|/:\\?|/:,@x|/:,@@|/::8|/:,@!|/:!!!|/:xx|/:bye|/:wipe|/:dig|/:handclap|/:&-\\(|/:B-\\)|/:<@|/:@>|/::-O|/:>-\\||/:P-\\(|/::'\\||/:X-\\)|/::\\*|/:@x|/:8\\*|/:pd|/:<W>|/:beer|/:basketb|/:oo|/:coffee|/:eat|/:pig|/:rose|/:fade|/:showlove|/:heart|/:break|/:cake|/:li|/:bome|/:kn|/:footb|/:ladybug|/:shit|/:moon|/:sun|/:gift|/:hug|/:strong|/:weak|/:share|/:v|/:@\\)|/:jj|/:@@|/:bad|/:lvu|/:no|/:ok|/:love|/:<L>|/:jump|/:shake|/:<O>|/:circle|/:kotow|/:turn|/:skip|/:oY|/:#-0|/:hiphot|/:kiss|/:<&|/:&>";
		Pattern p = Pattern.compile(qqfaceRegex);
		Matcher m = p.matcher(content);
		if (m.find()) {
			result = true;
		}
		return result;
	}
	/**
	 * 判断字符串是否为颜色,不是则返回默认值,是字符串则返回原来的颜色
	 * @param originColor
	 * @return
	 */
	public static String isColor(String originColor){
		if(null == originColor || "".equals(originColor.trim())){
			return "#FFFFFF";
		}
		String regex = "\\#[0-9a-fA-F]{6}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(originColor);
		if(!matcher.matches()){
			return "#FFFFFF";
		}else{
			return originColor;
		}
	}

	public static String getString(String str, String encoding) {
		if (hasText(str)) {
			try {
				// 进行编码转换，解决问题
				str = new String(str.getBytes("ISO8859-1"), encoding);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return str;
	}
	public static void main(String[] args) {
		String in="dad\r\nssss";
		StringUtils.escapeHTMLTags("dad\r\nssss");
		in=in.replaceAll("\r\n","");//去除字符串中的空格,回车,换行符,制表符
		System.out.println(StringUtils.escapeHTMLTags("dad\r\nss<a>ss"));
	}
}
