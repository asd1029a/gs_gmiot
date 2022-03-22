package com.danusys.web.tcp.server.controller;

import com.danusys.web.tcp.server.socket.CustomServerSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SocketRestController {

    private final CustomServerSocket ServerSocket;


    @RequestMapping("/start")
    public String startServer() throws IOException {
        ServerSocket.connectServer(7777);
        return "서버 시작";
    }

    @RequestMapping("/receive")
    public String receiveText(){
        HashMap<Integer, String> soketList = ServerSocket.serverThread.getSocketList();
        for (Integer integer : soketList.keySet()) {
            log.info("socket={}",soketList.get(integer));
        }
        log.info("total socket={}",soketList);
        return "소켓 정보: "+soketList;
    }
}
