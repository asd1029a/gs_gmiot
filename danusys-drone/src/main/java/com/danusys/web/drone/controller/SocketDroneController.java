package com.danusys.web.drone.controller;


import com.danusys.web.commons.socket.config.CustomServerSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/drone/api")
@Slf4j
@RequiredArgsConstructor
public class SocketDroneController {
    private final CustomServerSocket ServerSocket;

    @RequestMapping("/droneSocket")
    public ResponseEntity<?> getSocketList() {

        Map<Integer, Socket> socketList = ServerSocket.serverThread.getSocketList();
        List<Map<String,Object>> listMap =new ArrayList<>();

        for (Integer integer : socketList.keySet()) {
            Map<String,Object> map =new HashMap<>();
            map.put("index",integer);
            map.put("port",socketList.get(integer).getPort());
            map.put("address",socketList.get(integer).getInetAddress());
            map.put("localPort",socketList.get(integer).getLocalPort());
            listMap.add(map);
        }


        return ResponseEntity.ok(listMap);

    }
}
