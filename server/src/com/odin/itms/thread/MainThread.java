package com.odin.itms.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.corundumstudio.socketio.SocketIOClient;
import com.odin.itms.constant.Constants;
import com.odin.itms.util.ApiUtils;
import com.odin.itms.util.FileUtils;
import com.odin.itms.util.SocketioUtils;
import com.odin.itms.util.StringUtils;

public class MainThread extends Thread{
	private static List<SocketIOClient> clients = new ArrayList<SocketIOClient>();//用于保存socket所有客户端
	private static String port = "9092";
	/**
	 * @Title: main
	 * @author zhz
	 * @Description: TODO
	 * @throws
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainThread thread = new MainThread();
		thread.start();
	}
	@Override
	public void run() {
		boolean running = false;
		while(true){
			if(!running){
				running = true;
				String fileName = "";
				try{
					clients = SocketioUtils.getClients();
					String reg = ".+(.json|.JSON)$";
					List list = new ArrayList();
					list = FileUtils.getFileList(Constants.FTP_FILE_PATH+Constants.BACKUP_PATH+Constants.BA_ALARM_SUB_PATH,reg);
					if(list==null||list.size()<=0){
						running=false;
					}else{
						for(int i = 0;i<list.size();i++){
							if(!StringUtils.hasText(list.get(i)+"")){running=false;};
							fileName = list.get(i).toString();
							System.out.println("当前获取的文件为："+Constants.FTP_FILE_PATH+Constants.BACKUP_PATH+fileName);
							Map<String,Object> map = ApiUtils.converJson(Constants.FTP_FILE_PATH+Constants.BACKUP_PATH+Constants.BA_ALARM_SUB_PATH+fileName);
							Map pullMap = JSONObject.fromObject(map.get("objMap"));
							if(pullMap.get("alarmId")!=null){//警情变化时推送数据
								for(SocketIOClient client : clients) {
						        	Map socketMap = new HashMap();
						        	socketMap.put("alarmId", pullMap.get("alarmId"));
						        	socketMap.put("alarmStatus", pullMap.get("alarmStatus"));
									client.sendEvent("alarmevent", socketMap);
								}
							}
							//操作完数据之后备份并移除原文件
							FileUtils.copyFile(new File(Constants.FTP_FILE_PATH+Constants.BACKUP_PATH+Constants.BA_ALARM_SUB_PATH+fileName),new File(Constants.FTP_FILE_PATH+Constants.BACKUP_PATH+Constants.BA_ALARM_PATH+fileName),true);//备份
							//移除
							System.gc();//考虑程序没来得及释放流资源的情况，手动启动gc
							FileUtils.deleteFile(Constants.FTP_FILE_PATH+Constants.BACKUP_PATH+Constants.BA_ALARM_SUB_PATH+fileName);
						}
						Thread.sleep(500);
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					running = false;
				}
			}
	
		}
	}
}
