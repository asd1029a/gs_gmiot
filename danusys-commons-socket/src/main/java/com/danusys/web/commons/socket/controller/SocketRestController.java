/*
package com.danusys.web.commons.socket.controller;
*/
/**
 * TCP 서버 테스트용
 * startServer() 메소드 안의 포트를 변경하여 사용
 * /start 를 입력하면 서버가 시작.
 * 그 후 클라이언트로 정보를 보낸 후
 * /receive 를 입력하면 log와 화면에 보낸 정보를 확인가능
 *//*


import com.danusys.web.commons.tcp.socket.CustomServerSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.Socket;
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
        HashMap<Integer, Socket> soketList = ServerSocket.serverThread.getSocketList();
        for (Integer integer : soketList.keySet()) {
            log.info("socket={}",soketList.get(integer));
        }
        log.info("total socket={}",soketList);
        return "소켓 정보: "+soketList;
    }
}
*/
