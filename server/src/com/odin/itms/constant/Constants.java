package com.odin.itms.constant;

import com.odin.itms.util.AppConfig;

public class Constants {

	public static final String VERSION_SITE = "main";// 版本

	// 根路径
	public static final String BASEPATH = "basePath";

	/* 用户Session */
	public static final String USER_IN_SESSION = "UserInSession";
	public static final String GROUP_SESSION = "GroupSession";
	public static final String WXUSER_IN_SESSION = "WxUserInSession";

	public static final String DEFAULT_ATTACHMENT_DESCRI = "entry内容文件";
	public static final String FOLDER_NAME_MHT = "mht";
	public static final String FOLDER_NAME_ATTACHMENT = "attachment";
	public static final String FILE_TYPE_MHT = "mht";
	public static final String FOLDER_NAME_TEMPMHT = "temp_mht";
	public static final String FOLDER_NAME_UEDITOR = "ueditor"; // ueditor文件名
	public static final String FOLDER_NAME_ZIP = "zip";

	public static final String REGEXH1 = "<h1.*>[\\s\\S]*?<\\/h1>"; // 匹配<h1>*</h1>
	public static final String REGEXH2 = "<h2.*>[\\s\\S]*?<\\/h2>"; // 匹配<h2><h2>
	public static final String REGEXH3 = "<h3.*>[\\s\\S]*?<\\/h3>"; // 匹配<h3><h3>
	public static final String REGEXH4 = "<h4.*>[\\s\\S]*?<\\/h4>"; // 匹配<h4>*</h4>
	public static final String REGEXH5 = "<h5.*>[\\s\\S]*?<\\/h5>"; // 匹配<h5><h5>
	public static final String REGEXH6 = "<h6.*>[\\s\\S]*?<\\/h6>"; // 匹配<h6><h6>
	public static final String REGXID = "(?<=id=\")(.*?)(?=\")";// 匹配id=”xxx“
	public static final String REGXVALUE = "(?<=>)([\\s\\S]*?)(?=<)";
	public static final String REGXH = "<h1.*?>";

	// session keys
	public final static String WX_SORT_PATH = "wxSortPath";

	public final static int AUTO_EXTRACT_KEYWORD_COUNT = 10; // 默认自动抽取关键字的数量

	public static final int MAXTRYCOUNT = 2;// 登录允许登录最大次数

	public static final String WEBSERVICE_URL_NAME = "webService.url";
	public static final String WEBSERVICE_USERID_NAME = "webService.userid";
	public static final String WEBSERVICE_PASSWORD_NAME = "webService.password";

	// 智能警务相关文件路径配置
	// FTP服务内层目录
	public static String FTP_FILE_PATH = AppConfig.getProperty("ftpFilePath",
			"F:\\ftp_root\\");
	// 平台反馈附件所在目录
	public static String FEEDBACK_FILE_PATH = AppConfig.getProperty(
			"feedbackFilePath", "D:\\user_data\\");

	public static final String ATTACH_PATH = "synattach\\";
	public static final String BACKUP_PATH = "backup\\";
	public static final String REC_PATH = "recv\\";
	public static final String SEND_PATH = "send\\";

	public static final String SUCC_PATH = "succ\\";
	public static final String FAIL_PATH = "fail\\";
	public static final String XML_PATH = "xml\\";
	public static final String JSON_PATH = "json\\";

	// 三台合一相关文件路径配置
	// FTP服务内层目录
	public static String FTP_T2O_FILE_PATH = AppConfig.getProperty(
			"ftpT2oFilePath", "F:\\sthy\\");
	// 平台反馈附件所在目录
	public static String T2O_FEEDBACK_FILE_PATH = AppConfig.getProperty(
			"T2oFeedbackFilePath", "D:\\user_data\\");

	//警务通历史GPS备份目录
		public static final String BA_GPS_PATH = "gps\\";
		//警务通打卡记录备份目录
		public static final String BA_SIGN_RECORD_PATH = "signRecord\\";
		//警务通附件备份目录
		public static final String BA_FEEDBACK_FILE_PATH = "signRecord\\";
		//警务通、三台合一正常警情备份目录
		public static final String BA_ALARM_PATH = "alarm\\";
		//socket推送数据
		public static final String BA_ALARM_SUB_PATH = BA_ALARM_PATH+"sub\\";
		//警务通、三台合一无效警情目录
		public static final String BA_UNABLE_PATH = "unable\\";
		//三台合一补充警情目录
		public static final String BA_SUPPLY_PATH = "supply\\";
		//警务通、三台合一处警单目录
		public static final String BA_HANDLE_PATH = "handle\\";
		//警务通、三台合一反馈单目录
		public static final String BA_FEEDBACK_PATH = "feedback\\";
		
		public static final String T2O_ATTACH_PATH = "synattach\\";
		public static final String T2O_BACKUP_PATH = "backup\\";
		public static final String T2O_REC_PATH = "recv\\";
		public static final String T2O_SEND_PATH = "send\\";
		

}
