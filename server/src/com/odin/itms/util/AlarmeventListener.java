package com.odin.itms.util;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

public class AlarmeventListener implements DataListener<Object> {

    SocketIOServer server;

    public void setServer(SocketIOServer server) {
        this.server = server;
    }

    public void onData(SocketIOClient client, Object data,
            AckRequest ackSender) throws Exception {
        // chatevent为 事件的名称， data为发送的内容
        this.server.getBroadcastOperations().sendEvent("alarmevent", data);
    }
}