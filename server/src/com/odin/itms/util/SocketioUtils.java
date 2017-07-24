package com.odin.itms.util;

import java.util.ArrayList;
import java.util.List;


import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;

public class SocketioUtils{
	private static String port = AppConfig.getProperty("socketPort","9092");
	private static List<SocketIOClient> clients = new ArrayList<SocketIOClient>();
	
	private static SocketIOServer server;
	
	static{
		SocketioUtils.startSocket();//启动netty-socketio服务
	}
	
	public static void startSocket(){
		Configuration config = new Configuration();
		config = new Configuration();
		config.setHostname(AppConfig.getProperty("SocketIp","16.81.224.65"));
	    config.setPort(Integer.valueOf(port));
	    server = new SocketIOServer(config);
	    System.out.println("开放"+port+"端口用于socket推送");
        server.addConnectListener(new ConnectListener() {//添加客户端连接监听器
			@Override
			public void onConnect(SocketIOClient client) {
				System.out.println("connected:SessionId=" + client.getSessionId());	
				clients.add(client);//保存客户端
			}
		});
        
        AlarmeventListener listner = new AlarmeventListener();
        listner. setServer(server);
        
        // alarmevent为事件名称
        server.addEventListener("alarmevent", Object.class, listner);
        //启动服务
        server.start();
	}
	
	public static void stopSocket(){
		server.stop();
	}
	
	/**
	 * 
	  * @Title: getClients
	  * @author zhz
	  * @Description: 返回当前客户端集合
	  * @throws
	 */
	public static List<SocketIOClient> getClients(){
		return clients;
	}
}
